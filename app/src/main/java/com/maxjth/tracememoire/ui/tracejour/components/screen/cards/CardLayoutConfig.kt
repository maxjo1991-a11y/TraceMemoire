package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.ui.unit.dp

internal val CARD_RADIUS = 24.dp
internal val CARD_MIN_HEIGHT = 76.dp
internal val CARD_PADDING = 16.dp

internal fun formatCardTitle(raw: String): String {
    return raw.replace(" / ", " ").trim()
}

