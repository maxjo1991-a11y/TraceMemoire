// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/ajout/effect/EmpreinteEffect.kt
package com.maxjth.tracememoire.ui.accueil.ajout.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.hypot

/* --------------------------------------------------------
  CONTROLLER
--------------------------------------------------------- */

@Stable
class EmpreinteEffectController(
    private val progress: Animatable<Float, *>,
    private val alpha: Animatable<Float, *>,
    private val ring: Animatable<Float, *>,
    private val ringAlpha: Animatable<Float, *>
) {
    var center by mutableStateOf(Offset.Unspecified)
        private set

    var isLongPress by mutableStateOf(false)
        private set

    fun state(): EmpreinteState {
        return EmpreinteState(
            center = center,
            progress = progress.value,
            alpha = alpha.value,
            ring = ring.value,
            ringAlpha = ringAlpha.value,
            isLongPress = isLongPress
        )
    }

    suspend fun trigger(
        position: Offset,
        longPress: Boolean
    ) {
        center = position
        isLongPress = longPress

        // BASE GLOW
        progress.snapTo(0f)
        alpha.snapTo(if (longPress) 0.92f else 0.78f)

        // RING (halo fin, très “premium”)
        ring.snapTo(0f)
        ringAlpha.snapTo(if (longPress) 0.55f else 0.42f)

        // Glow principal
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (longPress) 560 else 380,
                easing = FastOutSlowInEasing
            )
        )

        // Halo ring (un peu plus lent pour “respirer”)
        ring.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (longPress) 720 else 520,
                easing = LinearOutSlowInEasing
            )
        )

        // Fade out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = if (longPress) 980 else 560,
                easing = LinearOutSlowInEasing
            )
        )
        ringAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = if (longPress) 980 else 560,
                easing = LinearOutSlowInEasing
            )
        )

        center = Offset.Unspecified
    }
}

@Stable
data class EmpreinteState(
    val center: Offset,
    val progress: Float,
    val alpha: Float,
    val ring: Float,
    val ringAlpha: Float,
    val isLongPress: Boolean
)

@Composable
fun rememberEmpreinteEffectController(): EmpreinteEffectController {
    val progress = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val ring = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(0f) }

    return remember { EmpreinteEffectController(progress, alpha, ring, ringAlpha) }
}

/* --------------------------------------------------------
  INPUT MODIFIER
--------------------------------------------------------- */

fun Modifier.empreinteInput(
    controller: EmpreinteEffectController,
    onClick: () -> Unit,
    onLongPress: () -> Unit
): Modifier = composed {
    val scope = rememberCoroutineScope()

    pointerInput(controller) {
        detectTapGestures(
            onTap = { offset ->
                scope.launch { controller.trigger(offset, false) }
                onClick()
            },
            onLongPress = { offset ->
                scope.launch { controller.trigger(offset, true) }
                onLongPress()
            }
        )
    }
}

/* --------------------------------------------------------
  OVERLAY VISUEL (glow + ring)
--------------------------------------------------------- */

@Composable
fun EmpreinteOverlay(
    controller: EmpreinteEffectController,
    modifier: Modifier = Modifier,
    colorStart: Color,
    colorEnd: Color
) {
    val state = controller.state()

    if (state.center == Offset.Unspecified) return
    if (state.alpha <= 1.01f && state.ringAlpha <= 0.01f) return

    Canvas(modifier = modifier) {

        val maxRadius =
            hypot(size.width, size.height) *
                    if (state.isLongPress) 0.60f else 0.44f

        // Glow principal (radial)
        val glowRadius = (0.10f + 0.90f * state.progress) * maxRadius

        val glowBrush = Brush.radialGradient(
            colors = listOf(
                colorStart.copy(alpha = state.alpha * 0.52f),
                colorEnd.copy(alpha = state.alpha * 0.26f),
                Color.Transparent
            ),
            center = state.center,
            radius = glowRadius
        )

        drawCircle(
            brush = glowBrush,
            center = state.center,
            radius = glowRadius
        )

        // Ring fin (stroke) — donne le “wow” sans être agressif
        val ringRadius = (.22f + 0.78f * state.ring) * maxRadius
        val ringStroke = Stroke(
            width = (if (state.isLongPress) 6f else 5f),
            cap = StrokeCap.Round
        )

        val ringBrush = Brush.radialGradient(
            colors = listOf(
                colorStart.copy(alpha = state.ringAlpha * 0.55f),
                colorEnd.copy(alpha = state.ringAlpha * 0.45f),
                Color.Transparent
            ),
            center = state.center,
            radius = ringRadius * 1.15f
        )

        drawCircle(
            brush = ringBrush,
            center = state.center,
            radius = ringRadius,
            style = ringStroke
        )
    }
}