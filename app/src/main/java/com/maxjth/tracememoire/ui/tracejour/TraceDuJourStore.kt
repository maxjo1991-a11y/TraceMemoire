package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


object TraceDuJourStore {

    private val _trace = mutableStateOf<TraceDuJour?>(null)
    val trace: State<TraceDuJour?> = _trace

    fun createNewTrace() {
        _trace.value = TraceDuJour()
    }

    fun updatePercent(percent: Int) {
        val t = _trace.value ?: return
        _trace.value = t.withPercent(percent)
    }

    fun updateNote(note: String) {
        val t = _trace.value ?: return
        _trace.value = t.withNote(note)
    }

    fun toggleTag(tag: String) {
        val t = _trace.value ?: return
        val next =
            if (t.tags.contains(tag)) t.tags - tag
            else t.tags + tag

        _trace.value = t.withTags(next)
    }
}