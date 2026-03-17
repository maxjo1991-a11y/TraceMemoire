package com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model

enum class CardOpenState {
    CLOSED,
    PREVIEW,
    FULL;

    fun next(): CardOpenState = when (this) {
        CLOSED -> PREVIEW
        PREVIEW -> FULL
        FULL -> CLOSED
    }
}