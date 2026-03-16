package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceApprofondirDiamondButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "diamond_anim")

    val haloPulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.13f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    val corePulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    val shimmerShift by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerShift"
    )

    val slowRotate by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slowRotate"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(132.dp)
        ) {
            Canvas(modifier = Modifier.size(132.dp)) {
                val c = center

                val outerDiamondSize = size.minDimension * 0.30f
                val innerDiamondSize = outerDiamondSize * 0.58f

                val haloOuterRadius = size.minDimension * 0.66f * haloPulse
                val haloInnerRadius = size.minDimension * 0.40f * haloPulse

                // Halo externe turquoise / mauve plus présent
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = 0.18f),
                            MAUVE.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        center = c,
                        radius = haloOuterRadius
                    ),
                    radius = haloOuterRadius,
                    center = c
                )

                // Halo interne plus dense
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.22f),
                            TURQUOISE.copy(alpha = 0.13f),
                            Color.Transparent
                        ),
                        center = c,
                        radius = haloInnerRadius
                    ),
                    radius = haloInnerRadius,
                    center = c
                )

                // Trait cosmique horizontal plus visible
                val lightCenterX = c.x + (size.width * 0.18f * shimmerShift)
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MAUVE.copy(alpha = 0.14f),
                            WHITE_SOFT.copy(alpha = 0.22f),
                            TURQUOISE.copy(alpha = 0.28f),
                            Color.Transparent
                        ),
                        startX = lightCenterX - size.width * 0.42f,
                        endX = lightCenterX + size.width * 0.42f
                    ),
                    topLeft = Offset(0f, c.y - 1.4f),
                    size = Size(size.width, 2.8f),
                    blendMode = BlendMode.SrcOver
                )

                val outerPath = Path().apply {
                    moveTo(c.x, c.y - outerDiamondSize)
                    lineTo(c.x + outerDiamondSize, c.y)
                    lineTo(c.x, c.y + outerDiamondSize)
                    lineTo(c.x - outerDiamondSize, c.y)
                    close()
                }

                val innerPath = Path().apply {
                    moveTo(c.x, c.y - innerDiamondSize)
                    lineTo(c.x + innerDiamondSize, c.y)
                    lineTo(c.x, c.y + innerDiamondSize)
                    lineTo(c.x - innerDiamondSize, c.y)
                    close()
                }

                rotate(slowRotate, pivot = c) {
                    // Diamant externe
                    drawPath(
                        path = outerPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MAUVE.copy(alpha = 0.98f),
                                TURQUOISE.copy(alpha = 1f)
                            ),
                            start = Offset(c.x - outerDiamondSize, c.y),
                            end = Offset(c.x + outerDiamondSize, c.y)
                        ),
                        style = Stroke(
                            width = 3.5f,
                            cap = StrokeCap.Round
                        )
                    )

                    // Diamant interne
                    drawPath(
                        path = innerPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                WHITE_SOFT.copy(alpha = 0.96f),
                                TURQUOISE.copy(alpha = 0.90f)
                            ),
                            start = Offset(c.x - innerDiamondSize, c.y),
                            end = Offset(c.x + innerDiamondSize, c.y)
                        ),
                        style = Stroke(
                            width = 2.6f,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Aura centrale blanche
                drawCircle(
                    color = WHITE_SOFT.copy(alpha = 0.12f),
                    radius = 18f * corePulse,
                    center = c
                )

                // Noyau bleu lumineux
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.98f),
                            Color(0xFFBFEFFF).copy(alpha = 0.92f),
                            TURQUOISE.copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        center = c,
                        radius = 14f * corePulse
                    ),
                    radius = 14f * corePulse,
                    center = c
                )

                // Point central net
                drawCircle(
                    color = WHITE_SOFT.copy(alpha = 1f),
                    radius = 4.8f * corePulse,
                    center = c
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Approfondir",
            fontSize = 16.sp,
            color = WHITE_SOFT.copy(alpha = 0.96f)
        )
    }
}