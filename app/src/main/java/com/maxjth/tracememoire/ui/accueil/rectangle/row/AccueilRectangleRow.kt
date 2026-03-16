package com.maxjth.tracememoire.ui.accueil.rectangle.row

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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

    val baseLabel = WHITE_SOFT.copy(alpha = 0.90f)
    val dimLabel = WHITE_SOFT.copy(alpha = 0.62f)

    val baseRight = WHITE_SOFT.copy(alpha = 0.96f)
    val dimRight = WHITE_SOFT.copy(alpha = 0.42f)

    val baseMid = TURQUOISE.copy(alpha = 0.92f)
    val dimMid = TURQUOISE.copy(alpha = 0.72f)

    val baseTrajectory = WHITE_SOFT.copy(alpha = 0.78f)
    val dimTrajectory = WHITE_SOFT.copy(alpha = 0.46f)

    val cardsLabelColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.76f)
    } else {
        WHITE_SOFT.copy(alpha = 0.52f)
    }

    val labelColor = if (isActive) baseLabel else dimLabel
    val rightColor = if (isActive) baseRight else dimRight
    val statusColor = if (isActive) baseMid else dimMid

    val hourColor = if (isActive) {
        WHITE_SOFT.copy(alpha = 0.78f)
    } else {
        WHITE_SOFT.copy(alpha = 0.56f)
    }

    val trajectoryColor = if (isActive) baseTrajectory else dimTrajectory

    val focusShape = RoundedCornerShape(18.dp)
    val focusBrush = Brush.horizontalGradient(
        listOf(
            TURQUOISE.copy(alpha = if (isActive) 0.07f else 0.00f),
            MAUVE.copy(alpha = if (isActive) 0.06f else 0.00f)
        )
    )
    val focusOutline = Color.White.copy(alpha = if (isActive) 0.06f else 0.00f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(focusShape)
            .background(focusBrush)
            .background(focusOutline)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        // Ligne 1 : label | bar | valeur
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ui.label,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                modifier = Modifier.width(78.dp)
            )

            Spacer(Modifier.width(12.dp))

            AccueilRectangleBar(
                percent = ui.percentForBar,
                isSaved = isSaved,
                isActive = isActive,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = rightTextDisplay,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = rightColor
            )
        }

        Spacer(Modifier.height(10.dp))

        // Ligne 2 : statut + heure alignés à gauche
        if (isSaved) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )

                if (hourText != null) {
                    Spacer(Modifier.width(6.dp))

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
                        color = hourColor
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Ligne 3 : trajectoire à gauche | cartes à droite
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

    val trackBg = Color.White.copy(alpha = 0.06f)

    val fillBrush = Brush.horizontalGradient(
        listOf(
            TURQUOISE.copy(alpha = 0.95f),
            MAUVE.copy(alpha = 0.82f)
        )
    )

    val inf = rememberInfiniteTransition(label = "accueil_bar_glow")
    val glowAlpha by inf.animateFloat(
        initialValue = if (isActive) 0.22f else 0.14f,
        targetValue = if (isActive) 0.40f else 0.26f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val glowBrush = Brush.horizontalGradient(
        listOf(
            TURQUOISE.copy(alpha = glowAlpha),
            MAUVE.copy(alpha = glowAlpha * 0.92f),
            TURQUOISE.copy(alpha = glowAlpha * 0.55f)
        )
    )

    Box(
        modifier = modifier
            .height(14.dp)
            .clip(trackShape)
            .background(trackBg)
    ) {
        if (isSaved && p > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(trackShape)
                    .background(glowBrush)
            )
        }

        if (isSaved && p > 0) {
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