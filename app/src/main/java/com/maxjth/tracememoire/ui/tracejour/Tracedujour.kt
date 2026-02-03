package com.maxjth.tracememoire.ui.tracejour

data class TraceDuJour(
    val percent: Int,
    val note: String,
    val tags: Set<String>,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val updateCount: Int
)