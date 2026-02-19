package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TraceSaveState(
    val status: TraceSaveStatus = TraceSaveStatus.IDLE,
    val dirty: Boolean = false,
    val lastSavedAt: LocalDateTime? = null,

    // Le cycle sur lequel on a confirmé la sauvegarde (ex: "MATIN", "APREM", etc.)
    val lastSavedCycleKey: String? = null,

    // Cycle courant de l’écran (clé string)
    val cycleKey: String? = null,

    val errorMessage: String? = null
) {

    fun canSave(enabled: Boolean): Boolean {
        if (!enabled) return false
        if (status == TraceSaveStatus.SAVING) return false
        return dirty
    }

    fun markDirty(): TraceSaveState {
        val nextStatus = if (status == TraceSaveStatus.SAVED) TraceSaveStatus.IDLE else status
        return copy(status = nextStatus, dirty = true, errorMessage = null)
    }

    fun beginSaving(): TraceSaveState =
        copy(status = TraceSaveStatus.SAVING, errorMessage = null)

    fun savedNow(now: LocalDateTime, cycleKey: String): TraceSaveState =
        copy(
            status = TraceSaveStatus.SAVED,
            dirty = false,
            lastSavedAt = now,
            lastSavedCycleKey = cycleKey,
            cycleKey = cycleKey,
            errorMessage = null
        )

    fun failed(message: String? = null): TraceSaveState =
        copy(status = TraceSaveStatus.ERROR, errorMessage = message)

    fun savedLabelFr(currentCycleKey: String): String? {
        val at = lastSavedAt ?: return null
        val c = lastSavedCycleKey ?: return null
        if (c != currentCycleKey) return null

        val time = at.format(DateTimeFormatter.ofPattern("HH:mm"))
        return "Enregistré • $time"
    }
}



enum class TraceSaveStatus {
    IDLE,      // rien de spécial
    SAVING,    // animation / verrouillage bouton
    SAVED,     // sauvegarde confirmée
    ERROR      // échec optionnel
}