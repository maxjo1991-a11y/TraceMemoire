package com.maxjth.tracememoire.ui.history

data class HistoryUiEvent(
    val id: String,
    val dayKey: String,
    val prettyDay: String,
    val title: String,
    val subtitle: String? = null,
    val percent: Int? = null,
    val time: String? = null
)
