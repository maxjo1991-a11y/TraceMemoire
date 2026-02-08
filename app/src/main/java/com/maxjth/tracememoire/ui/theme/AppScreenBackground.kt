package com.maxjth.tracememoire.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

@Composable
fun AppScreenBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(bg, bgSoft, bg)
                )
            ),
        content = content
    )
}

