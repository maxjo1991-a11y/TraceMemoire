// BLOC 1 — NOUVEAU FICHIER (à créer)
// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceSaveStoreHome.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context
import java.time.LocalDate
import java.time.ZoneId

/**
 * TRACE SAVE STORE — HOME (Jupiter/Mars + cycles + premiumTouched)
 * Objectif : sortir toute la logique HOME du gros TraceSaveStore.kt
 */
object TraceSaveStoreHome {

    private const val HOME_KEY = "HOME"
    private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")

    private const val HOME_LAST_PERCENT = "home_lastPercent"
    private const val HOME_PREMIUM_TOUCHED = "home_premium_touched"

    /**
     * Bridge attaché au store pour persister HOME même si tu changes d’écran.
     * → Tu l’attaches UNE fois au démarrage.
     */
    class Bridge {
        private var appContext: Context? = null
        private var seedBaseAttached: String? = null

        fun attach(context: Context, seedBase: String) {
            appContext = context.applicationContext
            seedBaseAttached = seedBase
        }

        fun persistHomeOnlyIfAttached(store: TraceSaveStore) {
            val ctx = appContext ?: return
            val seedBase = seedBaseAttached ?: return
            persistHomeOnly(ctx, seedBase, store)
        }
    }

    fun persistHomeOnly(
        context: Context,
        seedBase: String,
        store: TraceSaveStore
    ) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // lastPercent
        TraceJourPrefs.putInt(
            context,
            daySeed,
            HOME_KEY,
            HOME_LAST_PERCENT,
            store.lastPercent.value ?: -1
        )

        // cycles done
        VALID_CYCLES.forEach { c ->
            TraceJourPrefs.putBool(
                context,
                daySeed,
                HOME_KEY,
                "home_done_$c",
                store.completedCycleMap[c] == true
            )
        }

        // premium touched
        TraceJourPrefs.putBool(
            context,
            daySeed,
            HOME_KEY,
            HOME_PREMIUM_TOUCHED,
            store.premiumTouchedToday.value
        )
    }

    fun loadHomeOnlyIntoStore(
        context: Context,
        seedBase: String,
        store: TraceSaveStore
    ) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // ✅ Jupiter
        store.lastPercent.value = TraceJourPrefs
            .getInt(context, daySeed, HOME_KEY, HOME_LAST_PERCENT, -1)
            .let { if (it in 0..100) it else null }

        // ✅ cycles done
        store.completedCycleMap.clear()
        VALID_CYCLES.forEach { c ->
            val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, "home_done_$c", false)
            if (done) store.completedCycleMap[c] = true
        }

        // ✅ premium touched (on OR avec ce qu’on a déjà)
        val storedPremiumTouched =
            TraceJourPrefs.getBool(context, daySeed, HOME_KEY, HOME_PREMIUM_TOUCHED, false)
        store.premiumTouchedToday.value = store.premiumTouchedToday.value || storedPremiumTouched

        // ✅ Mars (hier)
        val ySeed = seedForYesterday(seedBase)
        store.yesterdayPercent.value = TraceJourPrefs
            .getInt(context, ySeed, HOME_KEY, HOME_LAST_PERCENT, -1)
            .let { if (it in 0..100) it else null }
    }

    private fun seedForYesterday(seedBase: String, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        return TraceJourPrefs.seedForDate(seedBase, yesterday)
    }

    fun isValidCycleKey(cycleKey: String): Boolean {
        val key = cycleKey.trim().uppercase()
        return key in VALID_CYCLES
    }

    fun markCycleDone(store: TraceSaveStore, cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        if (key in VALID_CYCLES) {
            store.completedCycleMap[key] = true
        }
    }
}

