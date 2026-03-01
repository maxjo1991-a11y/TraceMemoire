// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/store/TraceSaveStore.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveStoreHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.logique.TraceHomeScoreLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveCycleStorage
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys
import java.time.LocalDateTime

/**
 * TraceSaveStore = ORCHESTRATEUR (mince)
 *
 * ✅ MISE À JOUR (ton choix “0 au lieu de 50” + “non touché = non compté”):
 * - Les sliders “visuellement” démarrent à 0 (au lieu de 50)
 * - Tant qu’un slider n’a pas été touché, il est IGNORÉ dans le calcul du % (pas compté comme 0)
 *
 * ✅ FIX IMPORTANT:
 * - Au lock, on force touched=true, sinon un lock “sans glisser” peut être ignoré dans le calcul.
 *
 * ✅ NOUVEAU:
 * - Dots FREE (4 pastilles) gérés dans TraceSaveStoreHome
 *   → TraceSaveStore expose getFreeDotsForCycle(cycleKey)
 *   → Au lock d’un slider FREE, on allume sa pastille via home.onFreeSliderLocked(...)
 *
 * ✅ FIX PERSIST HOME (BUG: “ça disparaît après reboot”)
 * - On expose loadHomeOnlyForHomeScreen(...)
 * - On persist HOME-only quand lastPercent change (hors loadFromPrefs)
 */
class TraceSaveStore {

    // ─────────────────────────────────────────────
    // DEPENDENCIES (dossiers séparés)
    // ─────────────────────────────────────────────
    private val home = TraceSaveStoreHome()
    private val cycleStorage = TraceSaveCycleStorage()

    // ─────────────────────────────────────────────
    // EXPOSITIONS POUR HomeScreen
    // ─────────────────────────────────────────────
    val lastPercent = home.lastPercent
    val cyclePercentMap = home.cyclePercentMap
    val yearlyPercent = home.yearlyPercent

    // ✅ intValue existe seulement avec mutableIntStateOf
    val luneTick = mutableIntStateOf(0)

    // ✅ Dots FREE (HomeTemporalGridRect)
    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        return home.getFreeDotsForCycle(cycleKey)
    }

    // ─────────────────────────────────────────────
    // ATTACH / FLAGS
    // ─────────────────────────────────────────────
    private var appContext: Context? = null
    private var seedBaseAttached: String? = null
    private var isLoadingPrefs: Boolean = false

    fun attach(context: Context, seedBase: String) {
        appContext = context.applicationContext
        seedBaseAttached = seedBase
        home.attach(context.applicationContext, seedBase)
    }

    /**
     * ✅ IMPORTANT pour HomeScreen:
     * Recharge le HOME-only (HOME_LAST_PERCENT + rectangle + dots) au démarrage.
     */
    fun loadHomeOnlyForHomeScreen(context: Context, seedBase: String) {
        attach(context, seedBase)
        home.loadHomeOnly(context, seedBase)
    }

    // ─────────────────────────────────────────────
    // STATE GENERAL (SAVE)
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
    // MAPS UI (CYCLE)
    // ─────────────────────────────────────────────
    val sliderMap = mutableStateMapOf<String, Int>()
    val noteMap = mutableStateMapOf<String, String>()
    val capturedMap = mutableStateMapOf<String, Boolean>()
    val createdAtMap = mutableStateMapOf<String, Long>()
    val lockedMap = mutableStateMapOf<String, Boolean>()
    val touchedMap = mutableStateMapOf<String, Boolean>()

    fun markCaptured(key: String, nowMillis: Long = System.currentTimeMillis()) {
        capturedMap[key] = true
        if ((createdAtMap[key] ?: 0L) <= 0L) createdAtMap[key] = nowMillis
        markDirty()
    }

    fun lockCard(key: String) {
        lockedMap[key] = true
        markDirty()
    }

    fun isLocked(key: String): Boolean = lockedMap[key] == true
    fun createdAtMillis(key: String): Long? = createdAtMap[key]?.takeIf { it > 0L }

    // ─────────────────────────────────────────────
    // PREMIUM TOUCH (CYCLE -> HOME)
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
        touchedMap[key] = true
        if (isPremiumSlider(key)) {
            home.onPremiumTouched()
        }
        markDirty()
    }

    fun isSliderTouched(key: String): Boolean = touchedMap[key] == true

    // ─────────────────────────────────────────────
    // HOME TICK (minuit) -> délégué au HOME store
    // ─────────────────────────────────────────────
    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        home.onHomeTick(now)
        luneTick.intValue += 1
    }

    // ─────────────────────────────────────────────
    // ✅ HELPERS: “0 visuel” + “non touché = ignoré”
    // ─────────────────────────────────────────────
    private fun applyVisualZeroForUntouched(sliderKeys: List<String>) {
        sliderKeys.forEach { k ->
            if (touchedMap[k] != true) {
                sliderMap[k] = 0
            }
        }
    }

    private fun buildTouchedOnlySliderMap(): Map<String, Int> {
        // On garde seulement les sliders touchés → les autres sont “non renseignés”
        return sliderMap.filter { (k, _) -> touchedMap[k] == true }
    }

    // ─────────────────────────────────────────────
    // SCORE (pur) -> HOME lastPercent uniquement
    // ✅ non touché = ignoré dans le calcul
    // ✅ persist HOME-only (hors loadFromPrefs)
    // ─────────────────────────────────────────────
    fun recomputeHomeScoreFromSliders(includePremiumAxes: Boolean = true) {
        val touchedOnly = buildTouchedOnlySliderMap()

        val resultPercent = TraceHomeScoreLogic.recomputeHomeScoreFromSliders(
            sliderMap = touchedOnly,
            premiumTouchedToday = home.premiumTouchedToday.value,
            includePremiumAxes = includePremiumAxes
        )

        home.setLastPercent(resultPercent)

        // ✅ FIX: sans ça, tu vois le score en live, mais après restart il peut disparaître.
        // On ne persist pas pendant loadFromPrefs pour éviter des écritures parasites.
        if (!isLoadingPrefs) {
            home.persistHomeOnlyIfAttached()
        }
    }

    // ─────────────────────────────────────────────
    // LOCK / SAVE (cycle) -> factorisé via HOME
    // ─────────────────────────────────────────────
    fun handleLockForHomeAndRect(cycleKey: String, sliderKey: String) {
        if (lockedMap[sliderKey] == true) return

        // ✅ FIX: lock = considéré “renseigné”
        touchedMap[sliderKey] = true

        // 1) lock UI
        lockCard(sliderKey)

        // ✅ 1.5) dots FREE: si c’est un slider FREE (humeur/energie/corps/presence)
        // TraceSaveStoreHome filtre tout seul (ignore si pas FREE)
        home.onFreeSliderLocked(cycleKey = cycleKey, sliderKey = sliderKey)

        // 2) recalc Soleil
        recomputeHomeScoreFromSliders(includePremiumAxes = true)

        // 3) HOME: cycle done + snapshot rect + persist HOME-only
        home.onCycleLocked(
            cycleKey = cycleKey,
            currentSoleil = home.lastPercent.value
        )
    }

    fun setSaved(cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        state.value = state.value.savedNow(LocalDateTime.now(), key)

        // recalc Soleil
        recomputeHomeScoreFromSliders(includePremiumAxes = true)

        // HOME: même logique que lock (pas de duplication)
        home.onCycleSaved(
            cycleKey = key,
            currentSoleil = home.lastPercent.value
        )

        // persist cycle
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        persistAllToPrefs(ctx, seedBase, key)
    }

    // ─────────────────────────────────────────────
    // LOAD / PERSIST (CYCLE + STATE minimal) -> IO séparé
    // ─────────────────────────────────────────────
    fun loadFromPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        isLoadingPrefs = true
        try {
            attach(context, seedBase)

            // ✅ défaut VISUEL = 0 (au lieu de 50)
            sliderKeys.forEach { k ->
                sliderMap.putIfAbsent(k, 0)
                noteMap.putIfAbsent(k, "")
                capturedMap.putIfAbsent(k, false)
                createdAtMap.putIfAbsent(k, 0L)
                lockedMap.putIfAbsent(k, false)
                touchedMap.putIfAbsent(k, false)
            }

            // 1) LOAD CYCLE IO via storage
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

            // ✅ après load: si pas touché → on force 0 visuel
            applyVisualZeroForUntouched(sliderKeys)

            // 2) LOAD SAVE STATE (minimal: reset propre)
            state.value = TraceSaveState()

            // 3) premiumTouched (dépend de touchedMap)
            val premiumTouched = touchedMap.any { (k, v) -> v && isPremiumSlider(k) }
            home.setPremiumTouchedFlag(premiumTouched)

            // 4) LOAD HOME-only (inclut dots FREE mask)
            home.loadHomeOnly(context = context, seedBase = seedBase)

            // 5) calc Soleil si absent (avec map filtrée “touché seulement”)
            val hasRealSliders = sliderKeys.isNotEmpty()
            if (hasRealSliders && home.lastPercent.value == null) {
                recomputeHomeScoreFromSliders(includePremiumAxes = true)
            }
        } finally {
            isLoadingPrefs = false
        }
    }

    fun persistAllToPrefs(context: Context, seedBase: String, cycleKey: String) {
        if (isLoadingPrefs) return

        // 1) persist CYCLE IO
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

        // 2) persist HOME-only (inclut dots FREE mask)
        home.persistHomeOnlyIfAttached()
    }
}