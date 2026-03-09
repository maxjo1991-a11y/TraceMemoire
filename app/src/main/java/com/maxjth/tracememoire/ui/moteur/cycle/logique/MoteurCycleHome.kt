// FILE: app/src/main/java/com/maxjth/tracememoire/ui/moteur/cycle/logique/MoteurCycleHome.kt
package com.maxjth.tracememoire.ui.moteur.cycle.logique

import android.content.Context
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.stockage.CyclePrefsHome

class MoteurCycleHome {

    // ─────────────────────────────────────────────
    // LOAD
    // ─────────────────────────────────────────────

    fun loadPercentMap(
        context: Context,
        seedBase: String
    ): MutableMap<String, Int> {
        return CyclePrefsHome
            .readCyclePercentMap(context, seedBase)
            .toMutableMap()
    }

    fun loadCompletedToday(
        context: Context,
        seedBase: String
    ): MutableSet<TypeCycleHome> {
        return CyclePrefsHome
            .readCompletedToday(context, seedBase)
            .toMutableSet()
    }

    fun loadHomeLastPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastPercent(context, seedBase)
    }

    // ✅ AJOUT: valeur précédente (pour delta stable)
    fun loadHomeLastPercentPrev(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastPercentPrev(context, seedBase)
    }

    // ✅ AJOUT: delta stable du jour (ex: -20 / +8)
    fun loadHomeLastDeltaToday(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastDeltaToday(context, seedBase)
    }

    fun loadHomeDailyPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeDailyPercent(context, seedBase)
    }

    // (optionnel, seulement si tu l’utilises)
    fun loadHomeYearlyPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeYearlyPercent(context, seedBase)
    }

    // ─────────────────────────────────────────────
    // SAVE
    // ─────────────────────────────────────────────

    fun persistCyclePercent(
        context: Context,
        seedBase: String,
        type: TypeCycleHome,
        percent: Int?
    ) {
        CyclePrefsHome.writeCyclePercent(
            context = context,
            seedBase = seedBase,
            cycleKey = type.storageKey(),
            percent = percent
        )

        // Si un percent existe → cycle considéré complété
        if (percent != null) {
            CyclePrefsHome.writeCycleDone(
                context = context,
                seedBase = seedBase,
                cycleKey = type.storageKey(),
                done = true
            )
        }
    }

    fun persistCyclePercentMap(
        context: Context,
        seedBase: String,
        map: Map<String, Int>
    ) {
        CyclePrefsHome.writeCyclePercentMap(
            context = context,
            seedBase = seedBase,
            map = map
        )
    }

    /**
     * ✅ PATCH IMPORTANT
     * Quand tu écris HOME_LAST_PERCENT, on sauvegarde aussi:
     * - HOME_LAST_PERCENT_PREV (ancienne valeur)
     * - HOME_LAST_DELTA_TODAY (new - old)
     *
     * Comme ça le “deltaText” sous ton 56 reste stable, même après reload.
     */
    fun persistHomeLastPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        // lire l’ancien LAST avant d’écrire le nouveau
        val prev = CyclePrefsHome.readHomeLastPercent(context, seedBase)

        // 1) prev
        CyclePrefsHome.writeHomeLastPercentPrev(
            context = context,
            seedBase = seedBase,
            value = prev
        )

        // 2) delta
        val delta = if (prev != null && percent != null) (percent - prev) else null
        CyclePrefsHome.writeHomeLastDeltaToday(
            context = context,
            seedBase = seedBase,
            value = delta
        )

        // 3) last (valeur actuelle)
        CyclePrefsHome.writeHomeLastPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    fun persistHomeDailyPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        CyclePrefsHome.writeHomeDailyPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    // (optionnel, seulement si tu l’utilises)
    fun persistHomeYearlyPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        CyclePrefsHome.writeHomeYearlyPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    // ─────────────────────────────────────────────
    // RESET JOURNALIER
    // ─────────────────────────────────────────────

    /**
     * Nettoie les prefs HOME du jour (ce que ton CyclePrefsHome sait déjà faire).
     */
    fun resetHomeDay(
        context: Context,
        seedBase: String
    ) {
        CyclePrefsHome.clearHomeDay(context, seedBase)
    }

    /**
     * ✅ RESET cycles seulement (rectangle + done flags)
     * - vide la percentMap -> plus aucun % d'hier
     * - remet done=false pour chaque cycle
     */
    fun resetCyclesOnly(
        context: Context,
        seedBase: String
    ) {
        // 1) vider map (source de vérité)
        CyclePrefsHome.writeCyclePercentMap(
            context = context,
            seedBase = seedBase,
            map = emptyMap()
        )

        // 2) remettre done=false (si tu as des flags done)
        TypeCycleHome.entries.forEach { type ->
            CyclePrefsHome.writeCycleDone(
                context = context,
                seedBase = seedBase,
                cycleKey = type.storageKey(),
                done = false
            )
        }
    }

    /**
     * ✅ RESET complet "nouveau jour" (recommandé)
     * - reset HOME day
     * - reset cycles (map + done)
     */
    fun resetNewDay(
        context: Context,
        seedBase: String
    ) {
        resetHomeDay(context, seedBase)
        resetCyclesOnly(context, seedBase)
    }
}