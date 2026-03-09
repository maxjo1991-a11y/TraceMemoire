package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home

import android.content.Context
import com.maxjth.tracememoire.ui.moteur.cycle.logique.MoteurCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome

class TraceSaveHomeIO(
    private val home: TraceSaveStoreHome,
    private val moteurCycleHome: MoteurCycleHome
) {

    // ─────────────────────────────────────────────
    // ATTACH / FLAGS
    // ─────────────────────────────────────────────
    private var appContext: Context? = null
    private var seedBaseAttached: String? = null
    private var isLoadingPrefs: Boolean = false

    fun isLoadingPrefs(): Boolean = isLoadingPrefs

    fun attach(context: Context, seedBase: String) {
        appContext = context.applicationContext
        seedBaseAttached = seedBase
        home.attach(context.applicationContext, seedBase)
    }

    private fun beginLoadingPrefs() { isLoadingPrefs = true }
    private fun endLoadingPrefs() { isLoadingPrefs = false }

    private inline fun <T> runLoading(block: () -> T): T {
        beginLoadingPrefs()
        return try { block() } finally { endLoadingPrefs() }
    }

    // ─────────────────────────────────────────────
    // HOME LOAD (home + moteur)
    // ─────────────────────────────────────────────
    private fun loadHomeOnlyInternal(
        context: Context,
        seedBase: String,
        cyclePercentMap: MutableMap<String, Int>,
        dailyPercentSetter: (Int?) -> Unit,
        lastDeltaTodaySetter: (Int?) -> Unit
    ) {
        // 1) HOME-only
        home.loadHomeOnly(context = context, seedBase = seedBase)

        // 2) percentMap cycles (source de vérité : moteur)
        val loadedMap: Map<String, Int> = moteurCycleHome.loadPercentMap(context, seedBase)
        cyclePercentMap.clear()
        cyclePercentMap.putAll(loadedMap)

        // 3) daily percent Home
        dailyPercentSetter(
            moteurCycleHome
                .loadHomeDailyPercent(context, seedBase)
                ?.coerceIn(0, 100)
                ?: 0
        )

        // 4) delta officiel (home)
        lastDeltaTodaySetter(home.officialDelta.value)
    }

    // ─────────────────────────────────────────────
    // HOME PERSIST (home + moteur)
    // ─────────────────────────────────────────────
    private fun persistHomeOnlyInternal(
        cyclePercentMap: Map<String, Int>,
        dailyPercentValue: Int?,
        lastDeltaTodaySetter: (Int?) -> Unit
    ) {
        if (isLoadingPrefs) return
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        // 1) HOME-only
        home.persistHomeOnlyIfAttached()

        // 2) daily percent Home
        moteurCycleHome.persistHomeDailyPercent(
            context = ctx,
            seedBase = seedBase,
            percent = dailyPercentValue?.coerceIn(0, 100) ?: 0
        )

        // 3) percentMap cycles
        val safeMap: Map<String, Int> = cyclePercentMap.mapValues { (_, value) ->
            value.coerceIn(0, 100)
        }

        moteurCycleHome.persistCyclePercentMap(
            context = ctx,
            seedBase = seedBase,
            map = safeMap
        )

        // 4) refresh delta officiel en mémoire
        lastDeltaTodaySetter(home.officialDelta.value)
    }

    fun persistCyclePercentIfPossible(
        cycleKey: String,
        cyclePercentMap: Map<String, Int>
    ) {
        if (isLoadingPrefs) return
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        val type = TypeCycleHome.fromKey(cycleKey) ?: return
        val storedKey = type.storageKey()
        val p = cyclePercentMap[storedKey] ?: return

        moteurCycleHome.persistCyclePercent(
            ctx,
            seedBase,
            type,
            p.coerceIn(0, 100)
        )
    }

    /**
     * Pour HomeScreen: charge HOME-only + moteur sans écrire pendant le load.
     */
    fun loadHomeOnlyForHomeScreen(
        context: Context,
        seedBase: String,
        cyclePercentMap: MutableMap<String, Int>,
        dailyPercentSetter: (Int?) -> Unit,
        lastDeltaTodaySetter: (Int?) -> Unit
    ) {
        runLoading {
            attach(context, seedBase)
            loadHomeOnlyInternal(
                context = context,
                seedBase = seedBase,
                cyclePercentMap = cyclePercentMap,
                dailyPercentSetter = dailyPercentSetter,
                lastDeltaTodaySetter = lastDeltaTodaySetter
            )
        }
    }

    /**
     * Helper: persist safe
     */
    fun persistHomeSafe(
        cyclePercentMap: Map<String, Int>,
        dailyPercentValue: Int?,
        lastDeltaTodaySetter: (Int?) -> Unit
    ) {
        persistHomeOnlyInternal(
            cyclePercentMap = cyclePercentMap,
            dailyPercentValue = dailyPercentValue,
            lastDeltaTodaySetter = lastDeltaTodaySetter
        )
    }
}