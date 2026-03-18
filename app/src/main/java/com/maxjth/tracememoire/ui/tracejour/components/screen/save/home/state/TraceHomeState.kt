package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

/**
 * TraceHomeState
 *
 * Contient uniquement l’état HOME en mémoire.
 * Aucun accès prefs ici.
 * Aucune logique de date ici.
 * Aucune logique de persistance ici.
 */
class TraceHomeState {

    // -----------------------------
    // STATE PRINCIPAL
    // -----------------------------
    val lastPercent = mutableStateOf<Int?>(null)
    val yesterdayPercent = mutableStateOf<Int?>(null)
    val yearlyPercent = mutableStateOf<Int?>(null)

    // ✅ logique écran 1
    val todayValue = mutableStateOf(0)
    val yesterdayValue = mutableStateOf(0)

    val premiumTouchedToday = mutableStateOf(false)

    // -----------------------------
    // CYCLES HOME
    // -----------------------------
    val completedCycleMap = mutableStateMapOf<String, Boolean>()
    val cyclePercentMap = mutableStateMapOf<String, Int>()

    // ✅ dots FREE (bitmask par cycle)
    val cycleDotsFreeMaskMap = mutableStateMapOf<String, Int>()

    // ✅ heure figée (millis) par cycle
    val cycleCreatedAtMillisMap = mutableStateMapOf<String, Long>()

    // -----------------------------
    // OFFICIEL SOLEIL
    // -----------------------------
    val officialCurrent = mutableStateOf<Int?>(null)
    val officialPrev = mutableStateOf<Int?>(null)
    val officialDelta = mutableStateOf<Int?>(null)

    // -----------------------------
    // HELPERS STATE
    // -----------------------------
    fun clearCycleMaps() {
        completedCycleMap.clear()
        cyclePercentMap.clear()
        cycleDotsFreeMaskMap.clear()
        cycleCreatedAtMillisMap.clear()
    }

    fun resetOfficial() {
        officialCurrent.value = null
        officialPrev.value = null
        officialDelta.value = null
    }

    fun resetForNewDay() {
        lastPercent.value = null
        premiumTouchedToday.value = false
        todayValue.value = 0

        clearCycleMaps()
        resetOfficial()
    }
}