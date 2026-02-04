package com.maxjth.tracememoire.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeModuleCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF2ED1C3),         // turquoise doux
    cardBg: Color = Color.White.copy(alpha = 0.045f),
    textColor: Color = Color(0xFFF5F5F5)
) {
    val density = LocalDensity.current
    val tr = rememberInfiniteTransition(label = "home_module_card")

    // Respiration très douce (comme un objet vivant)
    val breath by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Micro-orbite (gravité) très lente
    val orbitPhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    // Tilt ultra lent (0.20° -> 0.40°)
    val tiltPhase by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    val scale = lerpFloat(0.995f, 1.008f, breath)
    val glowA = lerpFloat(0.06f, 0.16f, breath)
    val glowR = lerpFloat(0.95f, 1.10f, breath)

    val orbitRadiusDp = lerpFloat(0.8f, 1.6f, breath)
    val orbitXdp = (cos(orbitPhase.toDouble()) * orbitRadiusDp).toFloat()
    val orbitYdp = (sin(orbitPhase.toDouble()) * orbitRadiusDp * 0.70f).toFloat()

    val tiltDeg = lerpFloat(0.20f, 0.40f, breath) * tiltPhase

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = with(density) { orbitXdp.dp.toPx() },
                translationY = with(density) { orbitYdp.dp.toPx() },
                rotationZ = tiltDeg
            )
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        // Glow interne (radial) pour effet “flottant”
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = glowA),
                            Color.Transparent
                        ),
                        radius = 900f * glowR
                    )
                )
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Petit “point” d’accent (signature sobre)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(accent.copy(alpha = 0.80f))
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.62f),
                    lineHeight = 18.sp
                )
            }

            Text(
                text = "›",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.45f)
            )
        }
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)