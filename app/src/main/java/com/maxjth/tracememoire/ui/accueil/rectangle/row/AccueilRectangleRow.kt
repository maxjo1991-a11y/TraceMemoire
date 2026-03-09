// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/row/AccueilRectangleRow.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.row

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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

    // ✅ IMPORTANT:
    // Home (Écran 1) ne doit JAMAIS “inventer” une heure live.
    // ui.midText vient déjà de AccueilCycleUiModel.from(...):
    // - "Mémoire absente"
    // - "Mémoire créée"
    // - "Mémoire créée • HH:mm" (si createdAtMillis fourni)
    val rawMidText = ui.midText

    // ✅ AJOUT 1 : ✓ (check discret) quand c’est sauvegardé
    // On évite de doubler si jamais tu l’ajoutes déjà ailleurs.
    val midTextDisplay = if (isSaved && !rawMidText.trim().startsWith("✓")) {
        "✓ $rawMidText"
    } else {
        rawMidText
    }

    // ✅ couleurs (hiérarchie)
    val baseLabel = WHITE_SOFT.copy(alpha = 0.90f)
    val dimLabel = WHITE_SOFT.copy(alpha = 0.62f)

    val baseRight = if (isSaved) TURQUOISE.copy(alpha = 0.92f) else WHITE_SOFT.copy(alpha = 0.42f)
    val dimRight = if (isSaved) TURQUOISE.copy(alpha = 0.70f) else WHITE_SOFT.copy(alpha = 0.34f)

    val baseMid = if (isSaved) TURQUOISE.copy(alpha = 0.92f) else WHITE_SOFT.copy(alpha = 0.46f)
    val dimMid = if (isSaved) TURQUOISE.copy(alpha = 0.72f) else WHITE_SOFT.copy(alpha = 0.38f)

    val labelColor = if (isActive) baseLabel else dimLabel
    val rightColor = if (isActive) baseRight else dimRight
    val midColor = if (isActive) baseMid else dimMid

    // ✅ AJOUT 2 : “focus” du cycle actif (fond très subtil, sans changer la grille)
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
            // ✅ AJOUT 3 : plus de “respiration” visuelle (row moins tassée)
            .padding(vertical = 8.dp)
            .clip(focusShape)
            .background(focusBrush)
            .background(focusOutline) // mini voile clair (quasi invisible)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        // ───────── Ligne 1 : label | bar | % ─────────
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
                text = ui.rightText,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = rightColor
            )
        }

        Spacer(Modifier.height(8.dp))

        // ───────── Ligne 2 : statut ─────────
        Text(
            text = midTextDisplay,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = midColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // ───────── Ligne 3 : dots ─────────
        if (isSaved) {
            Spacer(Modifier.height(7.dp))
            AccueilRectangleDots(
                dots = ui.dots,
                isSaved = isSaved,
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

    // ✅ Glow “respirant” (seulement si mémoire existe)
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
        // 0) halo derrière
        if (isSaved && p > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(trackShape)
                    .background(glowBrush)
            )
        }

        // 1) fill réel
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