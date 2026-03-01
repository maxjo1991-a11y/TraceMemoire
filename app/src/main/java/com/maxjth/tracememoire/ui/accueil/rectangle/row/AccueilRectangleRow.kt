// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/row/AccueilRectangleRow.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.row

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    val labelColor = WHITE_SOFT.copy(alpha = 0.90f)
    val rightColor = if (isSaved) TURQUOISE.copy(alpha = 0.92f) else WHITE_SOFT.copy(alpha = 0.42f)
    val midColor = if (isSaved) TURQUOISE.copy(alpha = 0.92f) else WHITE_SOFT.copy(alpha = 0.46f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {

        // ───────── Ligne 1 : label | bar | % ─────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = ui.label,
                fontSize = 22
                    .sp,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                modifier = Modifier.width(74.dp)
            )

            Spacer(Modifier.width(12.dp))

            AccueilRectangleBar(
                percent = ui.percentForBar,
                isSaved = isSaved,
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
            text = ui.midText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = midColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // ───────── Ligne 3 : 4 carrés FREE (nouveau) ─────────
        if (isSaved) {
            Spacer(Modifier.height(6.dp))

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

    Box(
        modifier = modifier
            .height(14.dp)
            .clip(trackShape)
            .background(trackBg)
    ) {

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