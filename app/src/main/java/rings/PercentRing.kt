// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/ring/PercentRing.kt
package com.maxjth.tracememoire.ui.tracejour.components.ring

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import kotlin.math.abs

// ✅ Violet "positif" (ajuste ici si tu veux plus bleuté / plus rose)
private val VIOLET_POSITIVE = Color(0xFF8A5CFF)

@Composable
fun PercentRing(
    ringKey: String,
    percent: Int,
    accent: Color,          // ✅ MATCH avec TraceMoodSliderRow
    isDragging: Boolean
) {
    val pct = percent.coerceIn(0, 100)

    val persona = remember(ringKey) { ringPersonaForKey(ringKey) }
    val infinite = rememberInfiniteTransition(label = "ring_breathe")

    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.breatheMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val scale = 1f + persona.scaleAmp * breathe
    val strokeBoost = persona.strokeAmp * breathe

    val dragBoost = if (isDragging) 1.25f else 1.0f
    val dragStrokeExtra = if (isDragging) 6f else 0f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {

            // (on garde le scale ADN)
            val _radius = size.minDimension / 2f * 0.92f * scale

            val sweep = 360f * (pct / 100f)
            val mainStroke = (12f + strokeBoost) + dragStrokeExtra

            // ✅ glow large derrière — pas de "boucane" au centre
            drawArc(
                color = accent.copy(alpha = 0.26f * dragBoost),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(mainStroke + 18f, cap = StrokeCap.Round)
            )

            // ✅ ring net
            drawArc(
                color = accent.copy(alpha = 0.96f),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(mainStroke, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${pct}%",
            color = accent,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class RingPersona(
    val breatheMs: Int,
    val scaleAmp: Float,
    val strokeAmp: Float
)

private fun ringPersonaForKey(key: String): RingPersona {
    val h = stableHash(key)
    return RingPersona(
        breatheMs = 2000 + abs(h % 1300),
        scaleAmp = 0.24f,
        strokeAmp = 13.5f
    )
}

private fun stableHash(s: String): Int {
    var h = 1125899907
    for (c in s) h = 31 * h + c.code
    return h
}

// ✅ MAUVE (bas) -> TURQUOISE (milieu) -> VIOLET (haut)
fun accentForPercent(percent: Int): Color {
    val t = (percent.coerceIn(0, 100) / 100f)

    // 0..0.55 : mauve -> turquoise
    if (t <= 0.55f) {
        val u = (t / 0.55f).coerceIn(0f, 1f)
        return lerp(MAUVE, TURQUOISE, u)
    }

    // 0.55..1 : turquoise -> violet
    val u = ((t - 0.55f) / 0.45f).coerceIn(0f, 1f)
    return lerp(TURQUOISE, VIOLET_POSITIVE, u)
}