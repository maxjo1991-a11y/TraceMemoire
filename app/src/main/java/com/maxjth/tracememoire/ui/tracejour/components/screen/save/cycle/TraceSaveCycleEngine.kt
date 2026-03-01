package com.maxjth.tracememoire.ui.tracejour.components.screen.save.cycle

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

class TraceSaveCycleEngine {

    // ================================
    // STATE INTERNE (cycle instantané)
    // ================================

    val sliderMap = mutableStateMapOf<String, Int?>()
    val noteMap = mutableStateMapOf<String, String>()
    val capturedMap = mutableStateMapOf<String, Boolean>()
    val createdAtMap = mutableStateMapOf<String, Long>()
    val lockedMap = mutableStateMapOf<String, Boolean>()
    val touchedMap = mutableStateMapOf<String, Boolean>()

    val canSaveEnabled = mutableStateOf(false)

    // ================================
    // BASE API
    // ================================

    fun setSliderValue(key: String, value: Int?) {
        if (lockedMap[key] == true) return
        sliderMap[key] = value
        touchedMap[key] = true
        updateCanSave()
    }

    fun setNote(key: String, note: String) {
        if (lockedMap[key] == true) return
        noteMap[key] = note
    }

    fun lockSlider(key: String) {
        lockedMap[key] = true
    }

    fun unlockSlider(key: String) {
        lockedMap[key] = false
    }

    fun clearCycle() {
        sliderMap.clear()
        noteMap.clear()
        capturedMap.clear()
        createdAtMap.clear()
        lockedMap.clear()
        touchedMap.clear()
        canSaveEnabled.value = false
    }

    private fun updateCanSave() {
        canSaveEnabled.value = sliderMap.values.any { it != null }
    }
}