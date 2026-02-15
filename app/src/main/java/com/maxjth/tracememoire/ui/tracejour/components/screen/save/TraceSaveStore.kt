// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceSaveStore.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TraceSaveStore {

    val state = mutableStateOf(TraceSaveState())

    // autorisation “cycle non verrouillé”
    val canSaveEnabled = mutableStateOf(true)

    // données UI à conserver
    val sliderMap = mutableStateMapOf<String, Int>()       // "humeur" -> 78
    val noteMap = mutableStateMapOf<String, String>()      // "humeur" -> "..."
    val capturedMap = mutableStateMapOf<String, Boolean>() // "humeur" -> true

    fun setCanSave(enabled: Boolean) {
        canSaveEnabled.value = enabled
    }

    fun markDirty() {
        state.value = state.value.markDirty()
    }

    fun setSaving() {
        state.value = state.value.beginSaving()
    }

    fun setSaved(cycleKey: String) {
        state.value = state.value.savedNow(LocalDateTime.now(), cycleKey)
    }

    // ─────────────────────────────────────────────
    // PERSISTENCE
    // ─────────────────────────────────────────────
    fun loadFromPrefs(context: Context, seedBase: String, cycleKey: String, sliderKeys: List<String>) {
        sliderKeys.forEach { k ->
            sliderMap[k] = TraceJourPrefs.getInt(context, seedBase, cycleKey, "slider_$k", 50)
            noteMap[k] = TraceJourPrefs.getString(context, seedBase, cycleKey, "note_$k", "")
            capturedMap[k] = TraceJourPrefs.getBool(context, seedBase, cycleKey, "cap_$k", false)
        }

        val lastSavedEpoch = TraceJourPrefs.getLong(context, seedBase, cycleKey, "lastSavedEpoch", 0L)
        val lastSavedCycleKey = TraceJourPrefs.getString(context, seedBase, cycleKey, "lastSavedCycleKey", "")
        val isSaved = TraceJourPrefs.getBool(context, seedBase, cycleKey, "isSaved", false)
        val wasDirty = TraceJourPrefs.getBool(context, seedBase, cycleKey, "dirty", false)

        val dt = if (lastSavedEpoch > 0L) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSavedEpoch), ZoneId.systemDefault())
        } else null

        state.value = state.value.copy(
            status = if (isSaved) TraceSaveStatus.SAVED else TraceSaveStatus.IDLE,
            dirty = wasDirty && !isSaved,
            lastSavedAt = dt,
            lastSavedCycleKey = lastSavedCycleKey.ifBlank { null },
            cycleKey = cycleKey,
            errorMessage = null
        )
    }

    fun persistAllToPrefs(context: Context, seedBase: String, cycleKey: String) {
        sliderMap.forEach { (k, v) ->
            TraceJourPrefs.putInt(context, seedBase, cycleKey, "slider_$k", v)
        }
        noteMap.forEach { (k, v) ->
            TraceJourPrefs.putString(context, seedBase, cycleKey, "note_$k", v)
        }
        capturedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(context, seedBase, cycleKey, "cap_$k", v)
        }

        val st = state.value
        val epoch = st.lastSavedAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L

        TraceJourPrefs.putLong(context, seedBase, cycleKey, "lastSavedEpoch", epoch)
        TraceJourPrefs.putString(context, seedBase, cycleKey, "lastSavedCycleKey", st.lastSavedCycleKey ?: "")
        TraceJourPrefs.putBool(context, seedBase, cycleKey, "isSaved", st.status == TraceSaveStatus.SAVED)
        TraceJourPrefs.putBool(context, seedBase, cycleKey, "dirty", st.dirty)
    }
}