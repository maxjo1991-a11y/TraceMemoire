package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.moteur.cycle.logique.MoteurCycleHome
import com.maxjth.tracememoire.ui.moteur.validation.TraceScoreValidator
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceJourPrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine.TraceSaveConfirmationEngine
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine.TraceSaveCycleEngine
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveHomeIO
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveStoreHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.logique.TraceHomeScoreLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveCycleStorage
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys
import com.maxjth.tracememoire.ui.terre.logic.TerreHistoryStore
import java.time.LocalDate
import java.time.LocalDateTime

class TraceSaveStore {

    // ─────────────────────────────────────────────
    // DEPENDENCIES
    // ─────────────────────────────────────────────

    private val home = TraceSaveStoreHome()
    private val moteurCycleHome = MoteurCycleHome()
    private val homeIO = TraceSaveHomeIO(home, moteurCycleHome)

    private val cycleStorage = TraceSaveCycleStorage()
    private val cycle = TraceSaveCycleEngine()

    private val confirmation = TraceSaveConfirmationEngine(
        home = home,
        homeIO = homeIO,
        verrouillerCarte = { k -> lockCard(k) },
        recalculerSoleil = { includePremium -> recomputeHomeScoreFromSliders(includePremium) }
    )

    // ─────────────────────────────────────────────
    // SATELLITES / ATTACH MEMORY
    // ─────────────────────────────────────────────

    private var attachedContext: Context? = null
    private var attachedSeedBase: String? = null

    private val satellitePrefsName = "trace_memoire_satellites"

    private fun satelliteDayKey(seedBase: String) =
        "last_home_tick_day_$seedBase"

    private fun satellitePrefs(context: Context) =
        context.applicationContext
            .getSharedPreferences(satellitePrefsName, Context.MODE_PRIVATE)

    private fun readLastProcessedDay(context: Context, seedBase: String): String? {
        return satellitePrefs(context).getString(
            satelliteDayKey(seedBase),
            null
        )
    }

    private fun writeLastProcessedDay(context: Context, seedBase: String, day: LocalDate) {
        satellitePrefs(context)
            .edit()
            .putString(
                satelliteDayKey(seedBase),
                day.toString()
            )
            .apply()
    }

    // ─────────────────────────────────────────────
    // GARDE-FOU LUNE
    // ─────────────────────────────────────────────

    private fun safeIncrementLuneTick() {
        val next = luneTick.intValue + 1
        luneTick.intValue = next.coerceIn(0, 999_999)
    }

    // ─────────────────────────────────────────────
    // GARDE-FOU SOLEIL
    // ─────────────────────────────────────────────

    private fun safeSetLastPercent(percent: Int?) {
        val safePercent = percent?.coerceIn(0, 100) ?: 0
        home.setLastPercent(safePercent)
    }

    // ─────────────────────────────────────────────
    // SOURCE FIABLE POUR ARCHIVE TERRE
    // ─────────────────────────────────────────────

    private fun resolveScoreForTerreArchive(): Int? {
        val official = home.officialCurrent.value?.coerceIn(0, 100)
        if (official != null) return official

        val hasCompletedCycle = home.completedCycleMap.values.any { it == true }
        val live = home.lastPercent.value?.coerceIn(0, 100)

        return if (hasCompletedCycle) live else null
    }

    // ─────────────────────────────────────────────
    // ARCHIVE SOLEIL → TERRE
    // ─────────────────────────────────────────────

    private fun archiveYesterdayIfNeeded(now: LocalDateTime) {

        val context = attachedContext ?: run {
            println("DEBUG_ARCHIVE → ERREUR : attachedContext null → impossible d'archiver")
            return
        }
        val seedBase = attachedSeedBase ?: run {
            println("DEBUG_ARCHIVE → ERREUR : attachedSeedBase null → impossible d'archiver")
            return
        }

        val today = now.toLocalDate()
        val todayIso = today.toString()

        val lastProcessedDay = readLastProcessedDay(context, seedBase)

        println("DEBUG_ARCHIVE → Début archive | now=$now | today=$todayIso | lastProcessedDay=$lastProcessedDay")

        if (lastProcessedDay == null) {
            println("DEBUG_ARCHIVE → Premier lancement → on écrit today=$todayIso et on skip archive")
            writeLastProcessedDay(context, seedBase, today)
            return
        }

        if (lastProcessedDay == todayIso) {
            println("DEBUG_ARCHIVE → Même jour ($todayIso == $lastProcessedDay) → skip archive")
            return
        }

        val yesterday = today.minusDays(1)

        val officialScore = home.officialCurrent.value?.coerceIn(0, 100)
        val liveScore = home.lastPercent.value?.coerceIn(0, 100)
        val hasCompletedCycle = home.completedCycleMap.values.any { it == true }

        val scoreToArchive = resolveScoreForTerreArchive()

        println(
            "DEBUG_ARCHIVE → Nouveau jour détecté → archive hier $yesterday | " +
                    "official=$officialScore | live=$liveScore | hasCompletedCycle=$hasCompletedCycle | scoreToArchive=$scoreToArchive"
        )

        if (scoreToArchive != null && scoreToArchive in 0..100) {
            println("DEBUG_ARCHIVE → Upsert exécuté : $yesterday = $scoreToArchive")
            TerreHistoryStore.upsertForDate(
                context = context,
                seedBase = seedBase,
                date = yesterday,
                percent = scoreToArchive
            )
            terreTick.intValue += 1
            println("DEBUG_ARCHIVE → Succès : terreTick incrémenté à ${terreTick.intValue}")
        } else {
            println("DEBUG_ARCHIVE → Aucun score fiable à archiver → PAS d'upsert")
        }

        writeLastProcessedDay(context, seedBase, today)
        println("DEBUG_ARCHIVE → Fin : lastProcessedDay mis à jour à $todayIso")
    }

    // ─────────────────────────────────────────────
    // EXPOSITIONS POUR HOME
    // ─────────────────────────────────────────────

    val lastPercent = home.lastPercent
    val cyclePercentMap = home.cyclePercentMap
    val yearlyPercent = home.yearlyPercent

    val lastDeltaToday = mutableStateOf<Int?>(null)
    val luneTick = mutableIntStateOf(0)
    val terreTick = mutableIntStateOf(0)

    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        return home.getFreeDotsForCycle(cycleKey)
    }

    // ─────────────────────────────────────────────
    // ATTACH
    // ─────────────────────────────────────────────

    fun attach(context: Context, seedBase: String) {
        attachedContext = context.applicationContext
        attachedSeedBase = seedBase
        homeIO.attach(context, seedBase)
    }

    // ─────────────────────────────────────────────
    // STATE GENERAL
    // ─────────────────────────────────────────────

    val state = mutableStateOf(TraceSaveState())
    val canSaveEnabled = mutableStateOf(true)

    fun setCanSave(enabled: Boolean) {
        canSaveEnabled.value = enabled
    }

    fun markDirty() {
        state.value = state.value.markDirty()
    }

    fun setSaving() {
        state.value = state.value.beginSaving()
    }

    // ─────────────────────────────────────────────
    // MAPS (CycleEngine)
    // ─────────────────────────────────────────────

    val sliderMap get() = cycle.sliderMap
    val noteMap get() = cycle.noteMap
    val capturedMap get() = cycle.capturedMap
    val createdAtMap get() = cycle.createdAtMap
    val lockedMap get() = cycle.lockedMap
    val touchedMap get() = cycle.touchedMap

    fun markCaptured(key: String) {
        cycle.markCaptured(key)
        markDirty()
    }

    fun lockCard(key: String) {
        cycle.lockCard(key)
        markDirty()
    }

    fun isLocked(key: String): Boolean =
        cycle.isLocked(key)

    fun createdAtMillis(key: String): Long? =
        home.createdAtMillis(key)

    // ─────────────────────────────────────────────
    // PREMIUM TOUCH
    // ─────────────────────────────────────────────

    private fun isPremiumSlider(key: String): Boolean {
        return key == TraceSaveKeys.SLIDER_REPOS ||
                key == TraceSaveKeys.SLIDER_ARCHI_EMO ||
                key == TraceSaveKeys.SLIDER_TYPE_JOURNEE ||
                key == TraceSaveKeys.SLIDER_MOTIFS ||
                key == TraceSaveKeys.SLIDER_ENVIRONNEMENT ||
                key == TraceSaveKeys.SLIDER_CLARTE
    }

    fun markSliderTouched(key: String) {
        cycle.markTouched(key)

        if (isPremiumSlider(key)) {
            home.onPremiumTouched()
        }

        markDirty()
    }

    fun isSliderTouched(key: String): Boolean =
        cycle.isTouched(key)

    // ─────────────────────────────────────────────
    // HOME TICK
    // ─────────────────────────────────────────────

    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {

        val context = attachedContext ?: return
        val seedBase = attachedSeedBase ?: return

        val today = now.toLocalDate()

        val lastProcessedDay =
            readLastProcessedDay(context, seedBase)

        val newDayDetected =
            lastProcessedDay != today.toString()

        // 1) archive Soleil -> Terre
        archiveYesterdayIfNeeded(now)

        // 2) reset / refresh HOME
        home.onHomeTick(now)

        // 3) reset Écran 2 si nouveau jour
        if (newDayDetected) {
            cycle.clearAll()
            state.value = TraceSaveState()
            yearlyPercent.value = 0
        }

        // 4) tick Lune sécurisé
        safeIncrementLuneTick()

        // 5) persist HOME
        homeIO.persistHomeSafe(
            cyclePercentMap = cyclePercentMap,
            dailyPercentValue = yearlyPercent.value,
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )
    }

    // ─────────────────────────────────────────────
    // SCORE → SOLEIL
    // ─────────────────────────────────────────────

    fun recomputeHomeScoreFromSliders(
        includePremiumAxes: Boolean = true
    ) {

        val previousHomeScore =
            home.lastPercent.value?.coerceIn(0, 100)

        val touchedOnly =
            cycle.buildTouchedOnlySliderMap()

        if (touchedOnly.isEmpty()) {

            safeSetLastPercent(0)
            yearlyPercent.value = 0

            if (!homeIO.isLoadingPrefs()) {
                homeIO.persistHomeSafe(
                    cyclePercentMap = cyclePercentMap,
                    dailyPercentValue = yearlyPercent.value,
                    lastDeltaTodaySetter = { lastDeltaToday.value = it }
                )
            }

            val computedDelta = previousHomeScore?.let {
                0 - it
            }

            TraceScoreValidator.validateAll(
                previousJour = previousHomeScore,
                currentJour = 0,
                shownJourDelta = computedDelta,
                previousTraces = null,
                currentTraces = null,
                humeur = null,
                energie = null,
                corps = null,
                presence = null
            )

            return
        }

        val resultPercent =
            TraceHomeScoreLogic.recomputeHomeScoreFromSliders(
                sliderMap = touchedOnly,
                premiumTouchedToday = home.premiumTouchedToday.value,
                includePremiumAxes = includePremiumAxes
            )

        val humeur = touchedOnly[TraceSaveKeys.SLIDER_HUMEUR]
        val energie = touchedOnly[TraceSaveKeys.SLIDER_ENERGIE]
        val corps = touchedOnly[TraceSaveKeys.SLIDER_CORPS]
        val presence = touchedOnly[TraceSaveKeys.SLIDER_PRESENCE]

        val computedDelta = if (
            previousHomeScore != null && resultPercent != null
        ) {
            resultPercent - previousHomeScore
        } else {
            null
        }

        TraceScoreValidator.validateAll(
            previousJour = previousHomeScore,
            currentJour = resultPercent,
            shownJourDelta = computedDelta,
            previousTraces = null,
            currentTraces = null,
            humeur = humeur,
            energie = energie,
            corps = corps,
            presence = presence
        )

        // GARDE-FOU SOLEIL
        safeSetLastPercent(resultPercent)

        if (!homeIO.isLoadingPrefs()) {
            homeIO.persistHomeSafe(
                cyclePercentMap = cyclePercentMap,
                dailyPercentValue = yearlyPercent.value,
                lastDeltaTodaySetter = { lastDeltaToday.value = it }
            )
        }
    }

    // ─────────────────────────────────────────────
    // LOCK → COMMIT OFFICIEL
    // ─────────────────────────────────────────────

    fun handleLockForHomeAndRect(
        cycleKey: String,
        sliderKey: String
    ) {
        confirmation.confirmerEtVerrouiller(
            cycleKey = cycleKey,
            sliderKey = sliderKey,
            lockedMap = lockedMap,
            touchedMap = touchedMap,
            cyclePercentMap = cyclePercentMap,
            yearlyPercentValue = yearlyPercent.value,
            lastDeltaToday = lastDeltaToday
        )
    }

    // ─────────────────────────────────────────────
    // LOAD HOME (HomeScreen)
    // ─────────────────────────────────────────────

    fun loadHomeOnlyForHomeScreen(context: Context, seedBase: String) {
        homeIO.loadHomeOnlyForHomeScreen(
            context = context,
            seedBase = seedBase,
            cyclePercentMap = cyclePercentMap,
            dailyPercentSetter = { yearlyPercent.value = it },
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )
    }

    // ─────────────────────────────────────────────
    // DEBUG
    // ─────────────────────────────────────────────

    fun debugClearSeedAndReload(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        TraceJourPrefs.clearSeed(
            context = context,
            seedBase = seedBase
        )

        satellitePrefs(context)
            .edit()
            .remove(satelliteDayKey(seedBase))
            .apply()

        loadFromPrefs(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderKeys = sliderKeys
        )
    }

    // ─────────────────────────────────────────────
    // LOAD / SAVE CYCLE
    // ─────────────────────────────────────────────

    fun loadFromPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        attach(context, seedBase)

        cycle.initDefaults(sliderKeys)

        cycleStorage.loadCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderKeys = sliderKeys,
            sliderMap = sliderMap,
            noteMap = noteMap,
            capturedMap = capturedMap,
            createdAtMap = createdAtMap,
            lockedMap = lockedMap,
            touchedMap = touchedMap
        )

        cycle.applyVisualZeroForUntouched(sliderKeys)

        state.value = TraceSaveState()

        homeIO.loadHomeOnlyForHomeScreen(
            context = context,
            seedBase = seedBase,
            cyclePercentMap = cyclePercentMap,
            dailyPercentSetter = { yearlyPercent.value = it },
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )
    }

    fun persistAllToPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String
    ) {
        cycleStorage.persistCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderMap = sliderMap,
            noteMap = noteMap,
            capturedMap = capturedMap,
            createdAtMap = createdAtMap,
            lockedMap = lockedMap,
            touchedMap = touchedMap
        )

        homeIO.persistHomeSafe(
            cyclePercentMap = cyclePercentMap,
            dailyPercentValue = yearlyPercent.value,
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )

        homeIO.persistCyclePercentIfPossible(cycleKey, cyclePercentMap)
    }
}