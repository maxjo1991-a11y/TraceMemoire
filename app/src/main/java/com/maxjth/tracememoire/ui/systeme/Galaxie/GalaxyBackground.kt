package com.maxjth.tracememoire.ui.systeme.Galaxie

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun GalaxyBackground(
    modifier: Modifier = Modifier,
    nebulaAlpha: Float = 0.13f,
    dustAlpha: Float = 0.18f,
    starsAlpha: Float = 0.64f
) {
    val inf = rememberInfiniteTransition(label = "galaxy_background")

    // Rotation visible mais élégante
    val slowRotation by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 48_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slowRotation"
    )

    // Respiration cosmique générale
    val breathingScale by inf.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    // Halo central vivant
    val corePulse by inf.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    // Scintillement étoiles
    val starTwinkle by inf.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starTwinkle"
    )

    // Dérive poussières
    val dustDrift by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dustDrift"
    )

    // Étoile filante A
    val shootingStarProgressA by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shootingStarProgressA"
    )

    // Étoile filante B
    val shootingStarProgressB by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 31_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shootingStarProgressB"
    )

    // Tourbillon central
    val swirlRotation by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 32_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "swirlRotation"
    )

    // Contre-rotation secondaire
    val swirlRotationReverse by inf.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 52_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "swirlRotationReverse"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val shortest = min(w, h)

        val center = Offset(w * 0.5f, h * 0.58f)
        val lowerCenter = Offset(w * 0.5f, h * 0.73f)
        val leftCloud = Offset(w * 0.29f, h * 0.60f)
        val rightCloud = Offset(w * 0.71f, h * 0.60f)

        fun orbitPoint(
            orbitCenter: Offset,
            radiusX: Float,
            radiusY: Float,
            angleDeg: Float
        ): Offset {
            val angleRad = angleDeg * (PI.toFloat() / 180f)
            return Offset(
                x = orbitCenter.x + cos(angleRad) * radiusX,
                y = orbitCenter.y + sin(angleRad) * radiusY
            )
        }

        // ─────────────────────────────────────────────
        // GALAXIE PRINCIPALE
        // ─────────────────────────────────────────────
        rotate(
            degrees = slowRotation * 0.22f,
            pivot = center
        ) {
            scale(
                scale = breathingScale,
                pivot = center
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = nebulaAlpha),
                            TURQUOISE.copy(alpha = nebulaAlpha * 0.55f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = shortest * 0.46f
                    ),
                    radius = shortest * 0.46f,
                    center = center
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = nebulaAlpha * 0.42f),
                            Color.Transparent
                        ),
                        center = lowerCenter,
                        radius = shortest * 0.34f
                    ),
                    radius = shortest * 0.34f,
                    center = lowerCenter
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = dustAlpha * 0.26f),
                            Color.Transparent
                        ),
                        center = leftCloud,
                        radius = shortest * 0.24f
                    ),
                    radius = shortest * 0.24f,
                    center = leftCloud
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = dustAlpha * 0.26f),
                            Color.Transparent
                        ),
                        center = rightCloud,
                        radius = shortest * 0.24f
                    ),
                    radius = shortest * 0.24f,
                    center = rightCloud
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = nebulaAlpha * 0.30f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.50f, h * 0.66f),
                        radius = shortest * 0.29f
                    ),
                    radius = shortest * 0.29f,
                    center = Offset(w * 0.50f, h * 0.66f)
                )
            }
        }

        // ─────────────────────────────────────────────
        // TOURBILLON DOUX
        // ─────────────────────────────────────────────
        val swirlDots = listOf(
            Triple(0f, 0.18f, MAUVE.copy(alpha = 0.14f)),
            Triple(55f, 0.24f, TURQUOISE.copy(alpha = 0.14f)),
            Triple(120f, 0.30f, Color.White.copy(alpha = 0.10f)),
            Triple(190f, 0.36f, MAUVE.copy(alpha = 0.10f)),
            Triple(250f, 0.42f, TURQUOISE.copy(alpha = 0.10f)),
            Triple(315f, 0.48f, Color.White.copy(alpha = 0.08f))
        )

        swirlDots.forEachIndexed { index, (baseAngle, distFactor, color) ->
            val pos = orbitPoint(
                orbitCenter = center,
                radiusX = shortest * distFactor * 0.55f,
                radiusY = shortest * distFactor * 0.34f,
                angleDeg = baseAngle + swirlRotation + (index * 6f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = pos,
                    radius = shortest * 0.020f
                ),
                radius = shortest * 0.020f,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = color.alpha * 0.95f),
                radius = shortest * 0.0030f,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // PARTICULES ORBITANTES
        // ─────────────────────────────────────────────
        val orbitParticles = listOf(
            Triple(20f, 0.62f, MAUVE.copy(alpha = 0.12f)),
            Triple(110f, 0.70f, TURQUOISE.copy(alpha = 0.12f)),
            Triple(210f, 0.78f, Color.White.copy(alpha = 0.08f)),
            Triple(300f, 0.66f, MAUVE.copy(alpha = 0.08f))
        )

        orbitParticles.forEach { (baseAngle, distFactor, color) ->
            val pos = orbitPoint(
                orbitCenter = center,
                radiusX = shortest * distFactor * 0.42f,
                radiusY = shortest * distFactor * 0.24f,
                angleDeg = baseAngle + swirlRotationReverse
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = pos,
                    radius = shortest * 0.016f
                ),
                radius = shortest * 0.016f,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = color.alpha * 0.95f),
                radius = shortest * 0.0028f,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // POUSSIÈRES COSMIQUES
        // ─────────────────────────────────────────────
        val dust = listOf(
            Triple(
                Offset(w * 0.46f + shortest * 0.010f * dustDrift, h * 0.52f),
                shortest * 0.0074f,
                MAUVE.copy(alpha = dustAlpha)
            ),
            Triple(
                Offset(w * 0.54f - shortest * 0.010f * dustDrift, h * 0.54f),
                shortest * 0.0074f,
                TURQUOISE.copy(alpha = dustAlpha)
            ),
            Triple(
                Offset(w * 0.42f, h * 0.60f + shortest * 0.006f * dustDrift),
                shortest * 0.0060f,
                Color.White.copy(alpha = dustAlpha * 0.66f)
            ),
            Triple(
                Offset(w * 0.58f, h * 0.61f - shortest * 0.006f * dustDrift),
                shortest * 0.0060f,
                Color.White.copy(alpha = dustAlpha * 0.66f)
            ),
            Triple(
                Offset(w * 0.36f, h * 0.69f),
                shortest * 0.0052f,
                MAUVE.copy(alpha = dustAlpha * 0.82f)
            ),
            Triple(
                Offset(w * 0.64f, h * 0.70f),
                shortest * 0.0052f,
                TURQUOISE.copy(alpha = dustAlpha * 0.82f)
            ),
            Triple(
                Offset(w * 0.50f, h * 0.66f),
                shortest * 0.0070f,
                Color.White.copy(alpha = dustAlpha * 0.88f)
            ),
            Triple(
                Offset(w * 0.47f, h * 0.74f),
                shortest * 0.0045f,
                Color.White.copy(alpha = dustAlpha * 0.62f)
            ),
            Triple(
                Offset(w * 0.53f, h * 0.75f),
                shortest * 0.0045f,
                Color.White.copy(alpha = dustAlpha * 0.62f)
            )
        )

        dust.forEach { (pos, radius, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    ),
                    center = pos,
                    radius = radius * 5.2f
                ),
                radius = radius * 5.2f,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // ÉTOILES
        // ─────────────────────────────────────────────
        val stars = listOf(
            Triple(Offset(w * 0.29f, h * 0.55f), shortest * 0.0035f, Color.White),
            Triple(Offset(w * 0.71f, h * 0.55f), shortest * 0.0035f, Color.White),
            Triple(Offset(w * 0.40f, h * 0.76f), shortest * 0.0030f, MAUVE),
            Triple(Offset(w * 0.60f, h * 0.77f), shortest * 0.0030f, TURQUOISE),
            Triple(Offset(w * 0.47f, h * 0.81f), shortest * 0.0024f, Color.White),
            Triple(Offset(w * 0.53f, h * 0.82f), shortest * 0.0024f, Color.White),
            Triple(Offset(w * 0.35f, h * 0.73f), shortest * 0.0022f, Color.White),
            Triple(Offset(w * 0.65f, h * 0.73f), shortest * 0.0022f, Color.White),
            Triple(Offset(w * 0.44f, h * 0.70f), shortest * 0.0019f, Color.White),
            Triple(Offset(w * 0.56f, h * 0.70f), shortest * 0.0019f, Color.White)
        )

        stars.forEach { (pos, radius, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = starsAlpha * 0.26f * starTwinkle),
                        Color.Transparent
                    ),
                    center = pos,
                    radius = radius * 6.0f
                ),
                radius = radius * 6.0f,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = starsAlpha * starTwinkle.coerceAtMost(1.12f)),
                radius = radius,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // ÉTOILE CENTRALE
        // ─────────────────────────────────────────────
        val core = Offset(w * 0.5f, h * 0.68f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = corePulse),
                    MAUVE.copy(alpha = corePulse * 0.36f),
                    Color.Transparent
                ),
                center = core,
                radius = shortest * (0.082f + corePulse * 0.08f)
            ),
            radius = shortest * (0.082f + corePulse * 0.08f),
            center = core
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.92f),
                    Color.White.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = core,
                radius = shortest * 0.036f
            ),
            radius = shortest * 0.036f,
            center = core
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = shortest * 0.009f,
            center = core
        )

        // ─────────────────────────────────────────────
        // ÉTOILE FILANTE A
        // ─────────────────────────────────────────────
        if (shootingStarProgressA in 0.08f..0.15f) {
            val local = (shootingStarProgressA - 0.08f) / 0.07f

            val start = Offset(
                x = w * 0.18f + w * 0.18f * local,
                y = h * 0.30f + h * 0.08f * local
            )

            val end = Offset(
                x = start.x + shortest * 0.10f,
                y = start.y + shortest * 0.06f
            )

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.0f),
                        TURQUOISE.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.78f)
                    ),
                    start = start,
                    end = end
                ),
                start = start,
                end = end,
                strokeWidth = shortest * 0.0056f,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.92f),
                radius = shortest * 0.0046f,
                center = end
            )
        }

        // ─────────────────────────────────────────────
        // ÉTOILE FILANTE B
        // ─────────────────────────────────────────────
        if (shootingStarProgressB in 0.62f..0.68f) {
            val local = (shootingStarProgressB - 0.62f) / 0.06f

            val start = Offset(
                x = w * 0.72f - w * 0.16f * local,
                y = h * 0.36f + h * 0.10f * local
            )

            val end = Offset(
                x = start.x - shortest * 0.09f,
                y = start.y + shortest * 0.05f
            )

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.0f),
                        MAUVE.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.74f)
                    ),
                    start = start,
                    end = end
                ),
                start = start,
                end = end,
                strokeWidth = shortest * 0.0052f,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.88f),
                radius = shortest * 0.0040f,
                center = end
            )
        }
    }
}