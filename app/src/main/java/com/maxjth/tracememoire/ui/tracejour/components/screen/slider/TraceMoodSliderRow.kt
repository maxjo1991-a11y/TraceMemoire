package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TraceMoodSliderRow(
    title: String,
    enabled: Boolean,
    userIsPremium: Boolean,
    isPremiumSlider: Boolean,
    lockedLabel: String?,
    phaseKey: String,
    cycleKey: String,
    seedBase: String,
    sliderKey: String,
    showTitle: Boolean = true
) {
    val locked = isPremiumSlider && !userIsPremium

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0.5f) }

    val pct = (value * 100f).roundToInt().coerceIn(0, 100)

    val borderColor =
        if (isPremiumSlider) TURQUOISE.copy(alpha = 0.28f)
        else MAUVE.copy(alpha = 0.18f)

    val phrase = remember(pct, sliderKey) {
        buildFallbackPhrase(title, pct)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BG_SOFT.copy(alpha = 0.10f))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {

        if (showTitle) {
            Text(
                text = title,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PercentRing(
                ringKey = sliderKey,
                percent = pct
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = phrase,
            color = WHITE_SOFT.copy(alpha = 0.78f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        val sliderEnabled = enabled && !locked

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.14f))
                .border(
                    1.dp,
                    MAUVE.copy(alpha = 0.20f),
                    RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Slider(
                value = value,
                onValueChange = {
                    if (sliderEnabled) value = it.coerceIn(0f, 1f)
                },
                enabled = sliderEnabled,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    activeTrackColor = MAUVE.copy(alpha = 0.55f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.10f),
                    thumbColor = MAUVE.copy(alpha = 0.90f)
                )
            )
        }

        if (locked) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = lockedLabel ?: "Débloqué avec Premium.",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PercentRing(
    ringKey: String,
    percent: Int
) {
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

    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.matchParentSize()) {

            val radius = size.minDimension / 2f * 0.92f * scale

            // glow très doux
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        MAUVE.copy(alpha = 0.10f),
                        MAUVE.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f
            )

            val sweep = 360f * (percent / 100f)

            val mainStroke = 12f + strokeBoost

            // glow large
            drawArc(
                color = MAUVE.copy(alpha = 0.25f),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(mainStroke + 10f, cap = StrokeCap.Round)
            )

            // ring net
            drawArc(
                color = MAUVE.copy(alpha = 0.95f),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(mainStroke, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${percent}%",
            color = TURQUOISE,
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
        breatheMs = 1500 + abs(h % 1200),
        scaleAmp = 0.12f,
        strokeAmp = 7.5f
    )
}

private fun stableHash(s: String): Int {
    var h = 1125899907
    for (c in s) h = 31 * h + c.code
    return h
}

private fun buildFallbackPhrase(title: String, percent: Int): String {
    return when {
        percent < 20 -> "$title : très bas."
        percent < 40 -> "$title : bas."
        percent < 60 -> "$title : neutre."
        percent < 80 -> "$title : bon."
        else -> "$title : très haut."
    }
}