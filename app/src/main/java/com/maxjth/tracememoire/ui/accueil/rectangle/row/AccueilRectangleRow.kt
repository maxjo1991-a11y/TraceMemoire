package com.maxjth.tracememoire.ui.accueil.rectangle.row

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun AccueilRectangleRow(
    ui: AccueilCycleUiModel,
    modifier: Modifier = Modifier
) {
    val isSaved = !ui.rightText.trim().startsWith("—")
    val isActive = ui.isActive

    val rawMidText = ui.midText.trim()
    val trajectoryText = ui.trajectoryText?.trim().orEmpty()

    val normalizedMidText = when {
        rawMidText.startsWith("Mémoire créée") ->
            rawMidText.replaceFirst("Mémoire créée", "Empreinte enregistrée")

        rawMidText.startsWith("Mémoire absente") ->
            rawMidText.replaceFirst("Mémoire absente", "Aucune empreinte")

        else -> rawMidText
    }

    val midTextDisplay = if (isSaved && !normalizedMidText.startsWith("✓")) {
        "✓ $normalizedMidText"
    } else {
        normalizedMidText
    }

    val statusText = if (midTextDisplay.contains("•")) {
        midTextDisplay.substringBefore("•").trim()
    } else {
        midTextDisplay
    }

    val hourText = if (midTextDisplay.contains("•")) {
        midTextDisplay.substringAfter("•").trim()
    } else {
        null
    }

    val rightTextDisplay = if (isSaved) {
        "${ui.rightText} Valeur"
    } else {
        ui.rightText
    }

    val labelColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.98f)
    } else {
        WHITE_SOFT.copy(alpha = 0.74f)
    }

    val rightColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.98f)
    } else {
        WHITE_SOFT.copy(alpha = 0.42f)
    }

    val statusColor = if (isActive) {
        TURQUOISE.copy(alpha = 0.95f)
    } else {
        TURQUOISE.copy(alpha = 0.76f)
    }

    val hourColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.82f)
    } else {
        WHITE_SOFT.copy(alpha = 0.56f)
    }

    val trajectoryColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.82f)
    } else {
        WHITE_SOFT.copy(alpha = 0.46f)
    }

    val cardsLabelColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.78f)
    } else {
        WHITE_SOFT.copy(alpha = 0.52f)
    }

    val focusShape = RoundedCornerShape(20.dp)

    val activeBaseBrush = Brush.linearGradient(
        colors = listOf(
            TURQUOISE.copy(alpha = 0.12f),
            Color(0xFF10141C).copy(alpha = 0.94f),
            MAUVE.copy(alpha = 0.10f)
        )
    )

    val inactiveBaseBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.Transparent
        )
    )

    val innerGlowBrush = Brush.radialGradient(
        colors = listOf(
            MAUVE.copy(alpha = if (isActive) 0.10f else 0.03f),
            Color.Transparent
        ),
        radius = 420f
    )

    val outlineColor = if (isActive) {
        Color.White.copy(alpha = 0.07f)
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(focusShape)
            .background(if (isActive) activeBaseBrush else inactiveBaseBrush)
            .background(innerGlowBrush)
            .border(
                width = 1.dp,
                color = outlineColor,
                shape = focusShape
            )
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        // Ligne 1 : label | barre | valeur
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ui.label,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = labelColor,
                modifier = Modifier.width(84.dp)
            )

            Spacer(Modifier.width(12.dp))

            AccueilRectangleBar(
                percent = ui.percentForBar,
                isSaved = isSaved,
                isActive = isActive,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = rightTextDisplay,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = rightColor
            )
        }

        Spacer(Modifier.height(11.dp))

        if (isSaved) {
            // Ligne 2 : statut complet à gauche | heure à droite
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )

                Spacer(Modifier.weight(1f))

                if (hourText != null) {
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = WHITE_SOFT.copy(alpha = 0.42f)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = hourText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = hourColor,
                        modifier = Modifier.widthIn(min = 36.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Ligne 3 : trajectoire | cartes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = trajectoryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = trajectoryColor
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Cartes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cardsLabelColor
                    )

                    Spacer(Modifier.width(10.dp))

                    AccueilRectangleDots(
                        dots = ui.dots,
                        isSaved = isSaved,
                        modifier = Modifier
                    )
                }
            }
        } else {
            Spacer(Modifier.height(2.dp))

            Text(
                text = midTextDisplay,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun AccueilRectangleBar(
    percent: Int,
    isSaved: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val p = percent.coerceIn(0, 100)
    val trackShape = RoundedCornerShape(999.dp)

    val trackBg = if (isActive) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.White.copy(alpha = 0.06f)
    }

    val fillBrush = Brush.horizontalGradient(
        listOf(
            TURQUOISE.copy(alpha = 0.96f),
            MAUVE.copy(alpha = 0.82f)
        )
    )

    val inf = rememberInfiniteTransition(label = "accueil_bar_glow")

    val glowAlpha by inf.animateFloat(
        initialValue = if (isActive) 0.26f else 0.14f,
        targetValue = if (isActive) 0.46f else 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1700,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val glowBrush = Brush.horizontalGradient(
        listOf(
            TURQUOISE.copy(alpha = glowAlpha),
            MAUVE.copy(alpha = glowAlpha * 0.95f),
            TURQUOISE.copy(alpha = glowAlpha * 0.45f)
        )
    )

    Box(
        modifier = modifier
            .height(16.dp)
            .clip(trackShape)
            .background(trackBg)
    ) {
        if (isSaved && p > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = p / 100f)
                    .fillMaxHeight()
                    .clip(trackShape)
                    .background(glowBrush)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = p / 100f)
                    .fillMaxHeight()
                    .clip(trackShape)
                    .background(fillBrush)
            )
        }
    }
}