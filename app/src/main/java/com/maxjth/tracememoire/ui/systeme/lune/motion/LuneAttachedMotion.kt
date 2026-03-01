package com.maxjth.tracememoire.ui.systeme.lune.motion

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun LuneAttachedMotion(
    swingX: Float = 6f,
    swingY: Float = 4f,
    periodMs: Int = 4000,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "lune_attached_motion")

    val t by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = periodMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lune_attached_motion_anim"
    )

    Box(
        modifier = modifier.graphicsLayer {
            translationX = t * swingX
            translationY = t * swingY
        }
    ) {
        content()
    }
}