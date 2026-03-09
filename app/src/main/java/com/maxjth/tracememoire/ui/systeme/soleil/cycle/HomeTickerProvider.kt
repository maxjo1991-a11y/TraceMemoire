package com.maxjth.tracememoire.ui.noyau.systeme.soleil.cycle

import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.noyau.lois.HomeCycleTickerLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import java.time.LocalDateTime

object HomeTickerProvider {

    /**
     * Fournit un snapshot cohérent au ticker (source = store + logique HomeCycleTickerLogic).
     *
     * IMPORTANT:
     * - lastCycleSaved est un TypeCycleHome? (PAS de CycleId ici)
     * - conversion depuis store.state.value.lastSavedCycleKey (String?) -> TypeCycleHome?
     */
    fun snapshotFromStore(
        store: TraceSaveStore,
        now: LocalDateTime = LocalDateTime.now()
    ): HomeCycleTickerLogic.HomeSnapshot {

        val completed: Set<TypeCycleHome> =
            store.getFreeDotsForCycle("MATIN").let { _ ->
                // completedToday doit être fourni par le store via ses dots/prefs HOME.
                // Ici, on récupère via home.getFreeDotsForCycle + flags internes,
                // mais ton store expose déjà `getFreeDotsForCycle(cycleKey)`.
                // Comme HomeCycleTickerLogic attend un Set<TypeCycleHome>,
                // on reconstruit à partir des cycles connus.
                buildSet {
                    if (store.getFreeDotsForCycle("MATIN").any { it }) add(TypeCycleHome.MATIN)
                    if (store.getFreeDotsForCycle("JOUR").any { it }) add(TypeCycleHome.JOUR)
                    if (store.getFreeDotsForCycle("SOIR").any { it }) add(TypeCycleHome.SOIR)
                    if (store.getFreeDotsForCycle("NUIT").any { it }) add(TypeCycleHome.NUIT)
                }
            }

        val lastSavedCycle: TypeCycleHome? =
            store.state.value.lastSavedCycleKey
                ?.let { keyToTypeCycleHomeOrNull(raw = it) }

        return HomeCycleTickerLogic.HomeSnapshot(
            now = now,
            completedToday = completed,
            lastPercent = store.lastPercent.value,
            lastCycleSaved = lastSavedCycle,
            yearlyPercent = store.yearlyPercent.value
        )
    }

    private fun keyToTypeCycleHomeOrNull(raw: String): TypeCycleHome? {
        return when (raw.trim().uppercase()) {
            "NUIT" -> TypeCycleHome.NUIT
            "MATIN" -> TypeCycleHome.MATIN
            "JOUR" -> TypeCycleHome.JOUR
            "SOIR" -> TypeCycleHome.SOIR
            else -> null
        }
    }
}