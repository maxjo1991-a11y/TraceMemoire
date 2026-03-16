package com.maxjth.tracememoire.ui.accueil.rectangle

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRow
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRowDivider
import com.maxjth.tracememoire.ui.accueil.rectangle.style.AccueilRectangleColors
import com.maxjth.tracememoire.ui.accueil.rectangle.style.AccueilRectangleSizes
import com.maxjth.tracememoire.ui.systeme.cyclecolors.CycleColors

@SuppressLint("Range")
@Composable
fun AccueilRectangleGrid(
    cycles: List<AccueilCycleUiModel>,
    activeCycleKey: String? = null,
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    wrapperHorizontalPadding: Dp = 0.dp,
    rowSpacing: Dp = 2.dp,
    contentMaxWidth: Dp = 520.dp
) {
    val corner = RoundedCornerShape(AccueilRectangleSizes.corner)

    val borderBrush = AccueilRectangleColors.borderBrush(activeCycleKey)
    val haloColors = cycleHaloColors(activeCycleKey)

    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction.coerceIn(0f, 1f))
            .padding(
                horizontal = wrapperHorizontalPadding,
                vertical = AccueilRectangleSizes.wrapperPaddingVertical
            )
    ) {
        // HALO
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(AccueilRectangleSizes.haloPadding)
                .drawBehind {
                    val cornerPx = AccueilRectangleSizes.haloCorner.toPx()
                    val blurPx = AccueilRectangleSizes.haloBlur.toPx()
                    val glowStrokePx = 2.50.dp.toPx()

                    val inset = glowStrokePx / 2f
                    val w = size.width - inset * 2f
                    val h = size.height - inset * 2f

                    fun drawGlow(color: Color, dx: Float, dy: Float) {
                        val paint = AndroidPaint().apply {
                            isAntiAlias = true
                            style = AndroidPaint.Style.STROKE
                            strokeWidth = glowStrokePx
                            this.color = color.toArgb()
                            maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                        }

                        drawContext.canvas.nativeCanvas.drawRoundRect(
                            inset + dx,
                            inset + dy,
                            inset + dx + w,
                            inset + dy + h,
                            cornerPx,
                            cornerPx,
                            paint
                        )
                    }

                    drawGlow(haloColors.first, dx = -2.0f, dy = -2.0f)
                    drawGlow(haloColors.second, dx = 2.0f, dy = 2.0f)

                    drawGlow(
                        haloColors.first.copy(alpha = haloColors.first.alpha * 0.55f),
                        dx = -1.0f,
                        dy = -1.0f
                    )
                    drawGlow(
                        haloColors.second.copy(alpha = haloColors.second.alpha * 0.55f),
                        dx = 1.0f,
                        dy = 1.0f
                    )
                }
        )

        // CONTENU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = contentMaxWidth)
                .heightIn(
                    min = AccueilRectangleSizes.contentMinHeight,
                    max = AccueilRectangleSizes.contentMaxHeight
                )
                .background(AccueilRectangleColors.background, corner)
                .border(
                    width = AccueilRectangleSizes.borderWidth,
                    brush = borderBrush,
                    shape = corner
                )
                .padding(
                    horizontal = (AccueilRectangleSizes.innerPadding - 4.dp).coerceAtLeast(0.dp)
                )
                .padding(
                    vertical = (AccueilRectangleSizes.innerPadding - 6.dp).coerceAtLeast(0.dp)
                )
        ) {
            Column {
                cycles.forEachIndexed { index, ui ->
                    Box(modifier = Modifier.padding(vertical = rowSpacing)) {
                        AccueilRectangleRow(ui = ui)
                    }

                    if (index != cycles.lastIndex) {
                        AccueilRectangleRowDivider()
                    }
                }
            }
        }
    }
}

private fun cycleHaloColors(cycleKey: String?): Pair<Color, Color> {
    return when (cycleKey?.trim()?.uppercase()) {
        "MATIN" -> Pair(
            CycleColors.MatinStart.copy(alpha = 0.48f),
            CycleColors.MatinEnd.copy(alpha = 0.40f)
        )

        "JOUR" -> Pair(
            CycleColors.JourStart.copy(alpha = 0.48f),
            CycleColors.JourEnd.copy(alpha = 0.40f)
        )

        "SOIR" -> Pair(
            CycleColors.SoirStart.copy(alpha = 0.48f),
            CycleColors.SoirEnd.copy(alpha = 0.40f)
        )

        "NUIT" -> Pair(
            CycleColors.NuitStart.copy(alpha = 0.48f),
            CycleColors.NuitEnd.copy(alpha = 0.40f)
        )

        else -> Pair(
            CycleColors.JourStart.copy(alpha = 0.48f),
            CycleColors.JourEnd.copy(alpha = 0.40f)
        )
    }
}