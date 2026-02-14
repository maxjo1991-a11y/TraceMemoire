// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/slider/TraceMoodSliderRow.kt
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TraceMoodSliderRow(
    title: String,
    enabled: Boolean,

    // API
    userIsPremium: Boolean,
    isPremiumSlider: Boolean,
    lockedLabel: String?,

    // clés
    phaseKey: String,   // ✅ DOIT être "matin/jour/soir/nuit" (ou proche)
    cycleKey: String,
    seedBase: String,
    sliderKey: String,

    // ✅ pour enlever le double titre dans tes cartes repliables
    showTitle: Boolean = true
) {
    val locked = isPremiumSlider && !userIsPremium

    // Valeur stable par slider (et par cycleKey si tu veux)
    val stateKey = remember(seedBase, cycleKey, sliderKey) { "$seedBase|$cycleKey|$sliderKey" }
    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0.5f) } // 0..1

    val pct = (value * 100f).roundToInt().coerceIn(0, 100)

    // Couleurs : Premium = bord turquoise un peu plus présent
    val borderColor = if (isPremiumSlider) TURQUOISE.copy(alpha = 0.28f) else MAUVE.copy(alpha = 0.18f)

    // ✅ PHRASES OFFICIELLES (tes mots d’avant)
    val phrase = remember(sliderKey, pct, phaseKey) {
        TracePhrasesData.phraseForSlider(
            sliderKey = sliderKey,
            phaseKey = phaseKey,
            percent = pct
        )
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

        // ───────────────────────────────
        // Ring + % (respiration du ROND)
        // ───────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PercentRing(
                ringKey = sliderKey,     // personnalité unique par slider
                percent = pct,
                isPremium = isPremiumSlider
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

        // ───────────────────────────────
        // Slider
        // ───────────────────────────────
        val sliderEnabled = enabled && !locked

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.14f))
                .border(
                    width = 1.dp,
                    color = (if (isPremiumSlider) TURQUOISE else MAUVE).copy(alpha = 0.20f),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Slider(
                value = value,
                onValueChange = { newValue ->
                    if (!sliderEnabled) return@Slider
                    value = newValue.coerceIn(0f, 1f)
                },
                enabled = sliderEnabled,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    activeTrackColor = MAUVE.copy(alpha = if (sliderEnabled) 0.55f else 0.20f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.10f),
                    thumbColor = MAUVE.copy(alpha = if (sliderEnabled) 0.90f else 0.35f)
                )
            )
        }

        if (locked) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = lockedLabel ?: "Débloqué avec Premium.",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PercentRing(
    ringKey: String,
    percent: Int,
    isPremium: Boolean
) {
    val sizeDp = 140.dp
    val strokeDp = 10.dp

    // ✅ Persona unique (durées/amplitudes) basée sur ringKey
    val persona = remember(ringKey) { ringPersonaForKey(ringKey) }

    // ✅ Animation TOUJOURS active
    val infinite = rememberInfiniteTransition(label = "ringAlive_$ringKey")

    // Respiration (0..1..0)
    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = persona.breatheMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_$ringKey"
    )

    // Micro-rotation très légère (donne du vivant sans brume)
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = persona.driftMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift_$ringKey"
    )

    val ringColor = if (isPremium) TURQUOISE else MAUVE

    // ✅ C’EST ICI LA “RESPIRATION DU ROND”
    // Visible à l’œil : 1.00 -> 1.00 + ringScaleAmp
    val ringScale = 1f + persona.ringScaleAmp * (0.35f + 0.65f * breathe)

    // ✅ Brume très réduite (juste une aura)
    val glowAlpha = (persona.glowBase + persona.glowAmp * (0.35f + 0.65f * breathe))
        .coerceIn(0.00f, 0.10f)

    Box(
        modifier = Modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // Aura ultra subtile (pas le focus)
        Canvas(modifier = Modifier.matchParentSize()) {
            if (glowAlpha > 0.001f) {
                val r = (size.minDimension / 2f) * 0.92f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ringColor.copy(alpha = glowAlpha),
                            ringColor.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = r * 1.28f
                    ),
                    radius = r * 1.28f,
                    center = center
                )
            }
        }

        // Track + Progress (scalés ensemble = respiration du trait)
        Canvas(modifier = Modifier.matchParentSize()) {
            val sw = strokeDp.toPx()
            val p = percent.coerceIn(0, 100) / 100f
            val sweep = 360f * p

            // rotation minuscule (vivant) -> -1.2°..+1.2°
            val microRot = (drift - 0.5f) * persona.microRotateDeg

            withTransform({
                // ✅ SCALE du cercle (c’est ça que tu voulais)
                scale(scale = ringScale, pivot = center)
                // Micro-rotation, vraiment subtile
                rotate(degrees = microRot, pivot = center)
            }) {
                // Track
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )

                // Progress (NEON, pas “brume”)
                val grad = Brush.linearGradient(
                    colors = listOf(
                        ringColor.copy(alpha = 0.98f),
                        ringColor.copy(alpha = 0.70f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )

                drawArc(
                    brush = grad,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
        }

        // Texte : reste stable (pas besoin de respirer)
        Text(
            text = "${percent.coerceIn(0, 100)}%",
            color = TURQUOISE.copy(alpha = 0.92f),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class RingPersona(
    val breatheMs: Int,
    val driftMs: Int,
    val ringScaleAmp: Float,     // ✅ amplitude de respiration du cercle
    val glowBase: Float,
    val glowAmp: Float,
    val microRotateDeg: Float
)

private fun ringPersonaForKey(key: String): RingPersona {
    val h = stableHash(key)

    // Durées différentes (évite que tout respire ensemble)
    val breatheMs = 1350 + (abs(h) % 1200)          // 1350..2549
    val driftMs = 2400 + (abs(h / 7) % 2200)        // 2400..4599

    // ✅ Respiration VISIBLE (à l’œil) du rond
    // 0.028..0.050 environ
    val ringScaleAmp = 0.028f + (abs(h % 100) / 100f) * 0.022f

    // ✅ Brume très faible (juste un halo)
    val glowBase = 0.010f + (abs(h % 50) / 50f) * 0.020f   // 0.01..0.03
    val glowAmp  = 0.010f + (abs(h % 60) / 60f) * 0.030f   // 0.01..0.04

    // Micro-rotation (0.8..1.6 degrés)
    val microRotateDeg = 0.8f + (abs(h % 100) / 100f) * 0.8f

    return RingPersona(
        breatheMs = breatheMs,
        driftMs = driftMs,
        ringScaleAmp = ringScaleAmp,
        glowBase = glowBase,
        glowAmp = glowAmp,
        microRotateDeg = microRotateDeg
    )
}

private fun stableHash(s: String): Int {
    var h = 1125899907
    for (c in s) h = 31 * h + c.code
    return h
}