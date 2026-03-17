package com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic

import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model.MemoryMetaUi

fun parseMemoryMeta(memoryMeta: String?): MemoryMetaUi? {

    if (memoryMeta.isNullOrBlank()) {
        return null
    }

    val parts = memoryMeta
        .split("•")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    return when {

        parts.size >= 3 -> {
            MemoryMetaUi(
                topLine = "${parts[0]} • ${parts[1]}",
                bottomLine = parts.drop(2).joinToString(" • ")
            )
        }

        parts.size == 2 -> {
            MemoryMetaUi(
                topLine = "${parts[0]} • ${parts[1]}",
                bottomLine = null
            )
        }

        parts.size == 1 -> {
            MemoryMetaUi(
                topLine = parts.first(),
                bottomLine = null
            )
        }

        else -> {
            null
        }
    }
}

