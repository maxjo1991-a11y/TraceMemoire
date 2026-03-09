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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

/* --------------------------------------------------------
 CONTROLLER
--------------------------------------------------------- */

@Stable
class EmpreinteEffectController(
    private val progress: Animatable<Float, *>,
    private val alpha: Animatable<Float, *>,
    private val ring: Animatable<Float, *>,
    private val ringAlpha: Animatable<Float, *>,
    private val starProgress: Animatable<Float, *>,
    private val starAlpha: Animatable<Float, *>,
    private val sparkAlpha: Animatable<Float, *>
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
            starProgress = starProgress.value,
            starAlpha = starAlpha.value,
            sparkAlpha = sparkAlpha.value,
            isLongPress = isLongPress
        )
    }

    suspend fun trigger(
        position: Offset,
        longPress: Boolean
    ) {
        center = position
        isLongPress = longPress

        // Glow principal
        progress.snapTo(0f)
        alpha.snapTo(if (longPress) 0.94f else 0.80f)

        // Ring premium
        ring.snapTo(0f)
        ringAlpha.snapTo(if (longPress) 0.58f else 0.44f)

        // Petite étoile dans la bulle
        starProgress.snapTo(0f)
        starAlpha.snapTo(if (longPress) 1f else 0.96f)

        // Petit éclat initial
        sparkAlpha.snapTo(if (longPress) 0.90f else 0.72f)

        coroutineScope {
            launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 560 else 380,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                ring.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 720 else 520,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                starProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 760 else 520,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                sparkAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 420 else 260,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 980 else 560,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                ringAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 980 else 560,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                starAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = if (longPress) 980 else 640,
                        easing = LinearOutSlowInEasing
                    )
                )
            }
        }

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
    val starProgress: Float,
    val starAlpha: Float,
    val sparkAlpha: Float,
    val isLongPress: Boolean
)

@Composable
fun rememberEmpreinteEffectController(): EmpreinteEffectController {
    val progress = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val ring = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(0f) }
    val starProgress = remember { Animatable(0f) }
    val starAlpha = remember { Animatable(0f) }
    val sparkAlpha = remember { Animatable(0f) }

    return remember {
        EmpreinteEffectController(
            progress = progress,
            alpha = alpha,
            ring = ring,
            ringAlpha = ringAlpha,
            starProgress = starProgress,
            starAlpha = starAlpha,
            sparkAlpha = sparkAlpha
        )
    }
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
 OVERLAY VISUEL
(glow + ring + étoile locale)
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
    if (
        state.alpha <= 0.01f &&
        state.ringAlpha <= 0.01f &&
        state.starAlpha <= 0.01f &&
        state.sparkAlpha <= 0.01f
    ) return

    Canvas(modifier = modifier) {
        val maxRadius =
            hypot(size.width, size.height) *
                    if (state.isLongPress) 0.60f else 0.44f

        // Glow principal
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

        // Ring fin premium
        val ringRadius = (0.22f + 0.78f * state.ring) * maxRadius
        val ringStroke = Stroke(
            width = if (state.isLongPress) 6f else 5f,
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

        // Éclat initial
        if (state.sparkAlpha > 0.01f) {
            val sparkRadius = min(size.width, size.height) * 0.10f
            val sparkBrush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = state.sparkAlpha * 0.32f),
                    colorEnd.copy(alpha = state.sparkAlpha * 0.18f),
                    Color.Transparent
                ),
                center = state.center,
                radius = sparkRadius
            )

            drawCircle(
                brush = sparkBrush,
                center = state.center,
                radius = sparkRadius
            )
        }

        // Petite étoile qui monte dans la bulle
        if (state.starAlpha > 0.01f) {
            val travelY = size.height * if (state.isLongPress) 0.18f else 0.14f
            val starCenter = Offset(
                x = state.center.x,
                y = state.center.y - travelY * state.starProgress
            )

            val baseStarRadius = min(size.width, size.height) *
                    if (state.isLongPress) 0.062f else 0.050f

            val starScale = 0.92f + (0.18f * (1f - state.starProgress))
            val starRadius = baseStarRadius * starScale

            // halo de l’étoile
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorEnd.copy(alpha = state.starAlpha * 0.22f),
                        colorStart.copy(alpha = state.starAlpha * 0.16f),
                        Color.Transparent
                    ),
                    center = starCenter,
                    radius = starRadius * 2.6f
                ),
                center = starCenter,
                radius = starRadius * 2.6f
            )

            // traînée douce
            if (state.starProgress > 0.08f) {
                val tailHeight = travelY * 0.34f * state.starProgress
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorEnd.copy(alpha = 0f),
                            colorEnd.copy(alpha = state.starAlpha * 0.08f),
                            colorStart.copy(alpha = state.starAlpha * 0.18f)
                        ),
                        startY = starCenter.y + tailHeight,
                        endY = starCenter.y
                    ),
                    start = Offset(starCenter.x, starCenter.y + tailHeight),
                    end = Offset(starCenter.x, starCenter.y + starRadius * 0.2f),
                    strokeWidth = starRadius * 0.55f,
                    cap = StrokeCap.Round
                )
            }

            // étoile
            drawStar(
                center = starCenter,
                outerRadius = starRadius,
                innerRadius = starRadius * 0.46f,
                color = Color.White.copy(alpha = state.starAlpha * 0.96f)
            )

            drawStar(
                center = starCenter,
                outerRadius = starRadius * 0.78f,
                innerRadius = starRadius * 0.34f,
                color = colorEnd.copy(alpha = state.starAlpha * 0.52f)
            )
        }
    }
}

/* --------------------------------------------------------
 DRAW HELPERS
--------------------------------------------------------- */

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    val points = 4
    val step = PI / points
    val startAngle = -PI / 2.0

    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + (step * i)
        val x = center.x + (cos(angle).toFloat() * radius)
        val y = center.y + (sin(angle).toFloat() * radius)

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    path.close()
    drawPath(path = path, color = color)
}