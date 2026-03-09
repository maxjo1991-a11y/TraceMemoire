package com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine

import androidx.compose.runtime.mutableStateMapOf

/**
 * TraceSaveCycleEngine
 * = cerveau "Cycle" (maps + helpers)
 *
 * Objectif: sortir du Store tout l'état UI cycle + helpers
 * pour que TraceSaveStore redevienne un orchestrateur mince.
 *
 * Règles conservées:
 * - sliders démarrent à 0 (visuel)
 * - si pas touché => ignoré dans le score
 * - lock => touched=true forcé
 */
class TraceSaveCycleEngine {

    // ─────────────────────────────────────────────
    // MAPS UI (CYCLE)
    // ─────────────────────────────────────────────
    val sliderMap = mutableStateMapOf<String, Int>()
    val noteMap = mutableStateMapOf<String, String>()
    val capturedMap = mutableStateMapOf<String, Boolean>()
    val createdAtMap = mutableStateMapOf<String, Long>()
    val lockedMap = mutableStateMapOf<String, Boolean>()
    val touchedMap = mutableStateMapOf<String, Boolean>()

    // ─────────────────────────────────────────────
    // INIT DEFAULTS
    // ─────────────────────────────────────────────
    fun initDefaults(sliderKeys: List<String>) {
        sliderKeys.forEach { k ->
            sliderMap.putIfAbsent(k, 0)
            noteMap.putIfAbsent(k, "")
            capturedMap.putIfAbsent(k, false)
            createdAtMap.putIfAbsent(k, 0L)
            lockedMap.putIfAbsent(k, false)
            touchedMap.putIfAbsent(k, false)
        }
    }

    // ─────────────────────────────────────────────
    // CAPTURE / TIME
    // ─────────────────────────────────────────────
    fun markCaptured(key: String, nowMillis: Long = System.currentTimeMillis()) {
        capturedMap[key] = true
        if ((createdAtMap[key] ?: 0L) <= 0L) createdAtMap[key] = nowMillis
    }

    fun createdAtMillis(key: String): Long? = createdAtMap[key]?.takeIf { it > 0L }

    // ─────────────────────────────────────────────
    // LOCK / TOUCH
    // ─────────────────────────────────────────────
    fun lockCard(key: String) {
        lockedMap[key] = true
        touchedMap[key] = true
    }

    fun isLocked(key: String): Boolean = lockedMap[key] == true

    fun markTouched(key: String) {
        touchedMap[key] = true
    }

    fun isTouched(key: String): Boolean = touchedMap[key] == true

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    fun applyVisualZeroForUntouched(sliderKeys: List<String>) {
        sliderKeys.forEach { k ->
            if (touchedMap[k] != true) {
                sliderMap[k] = 0
            }
        }
    }

    fun buildTouchedOnlySliderMap(): Map<String, Int> {
        return sliderMap.filter { (k, _) -> touchedMap[k] == true }
    }

    // ─────────────────────────────────────────────
    // RESET
    // ─────────────────────────────────────────────

    /**
     * Reset total de toutes les maps UI cycle.
     * À utiliser quand on change complètement de journée/cycle.
     */
    fun clearAll() {
        sliderMap.clear()
        noteMap.clear()
        capturedMap.clear()
        createdAtMap.clear()
        lockedMap.clear()
        touchedMap.clear()
    }

    /**
     * Reset propre pour une nouvelle journée sur une liste de sliders connue.
     * On garde les clés, mais on remet tout à zéro / vide / false.
     */
    fun resetForNewDay(sliderKeys: List<String>) {
        sliderKeys.forEach { k ->
            sliderMap[k] = 0
            noteMap[k] = ""
            capturedMap[k] = false
            createdAtMap[k] = 0L
            lockedMap[k] = false
            touchedMap[k] = false
        }
    }

    /**
     * Supprime tout puis recrée les defaults du cycle courant.
     * Très utile après minuit quand on veut repartir ultra propre.
     */
    fun rebuildFresh(sliderKeys: List<String>) {
        clearAll()
        initDefaults(sliderKeys)
        applyVisualZeroForUntouched(sliderKeys)
    }
}