package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TraceDuJourStore {

    // Trace courante (celle du jour)
    var trace by mutableStateOf<TraceDuJour?>(null)
        private set

    // Créer une nouvelle trace
    fun createNewTrace() {
        val now = System.currentTimeMillis()
        trace = TraceDuJour(
            percent = 50,
            note = "",
            tags = emptySet(),          // ✅ Set (pas List)
            createdAtMs = now,
            updatedAtMs = now,
            updateCount = 0
        )
    }

    // Modifier le pourcentage
    fun updatePercent(percent: Int) {
        val t = trace ?: return
        val now = System.currentTimeMillis()
        trace = t.copy(
            percent = percent.coerceIn(0, 100),
            updatedAtMs = now,
            updateCount = t.updateCount + 1
        )
    }

    // Modifier la note
    fun updateNote(note: String) {
        val t = trace ?: return
        val now = System.currentTimeMillis()
        trace = t.copy(
            note = note,
            updatedAtMs = now,
            updateCount = t.updateCount + 1
        )
    }

    // ✅ Toggle tag (AJOUT MANQUANT)
    fun toggleTag(tag: String) {
        val t = trace ?: return
        val now = System.currentTimeMillis()

        val nextTags =
            if (t.tags.contains(tag)) t.tags - tag
            else t.tags + tag

        trace = t.copy(
            tags = nextTags,
            updatedAtMs = now,
            updateCount = t.updateCount + 1
        )
    }
}