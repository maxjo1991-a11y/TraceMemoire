// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/AccueilRectangleGrid.kt
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRow
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRowDivider
import com.maxjth.tracememoire.ui.accueil.rectangle.style.AccueilRectangleColors
import com.maxjth.tracememoire.ui.accueil.rectangle.style.AccueilRectangleSizes

@SuppressLint("Range")
@Composable
fun AccueilRectangleGrid(
    cycles: List<AccueilCycleUiModel>,
    modifier: Modifier = Modifier,

    // ✅ 1f = 100% du parent. (Avant: 50f -> bug)
    widthFraction: Float = 1f,

    // ✅ Padding horizontal global si tu veux
    wrapperHorizontalPadding: Dp = 0.dp,

    // ✅ AJOUT: spacing global entre les lignes (moins “poignées tassées”)
    rowSpacing: Dp = 2.dp,

    // ✅ AJOUT: limite de largeur (évite l’effet “bloc trop serré” sur grands écrans)
    contentMaxWidth: Dp = 520.dp
) {
    val corner = RoundedCornerShape(AccueilRectangleSizes.corner)

    // ✅ Wrapper global
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction.coerceIn(0f, 1f))
            .padding(
                horizontal = wrapperHorizontalPadding,
                vertical = AccueilRectangleSizes.wrapperPaddingVertical
            )
    ) {

        // 1) ✅ HALO (couche arrière)
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

                    fun drawGlow(colorArgb: Int, dx: Float, dy: Float) {
                        val paint = AndroidPaint().apply {
                            isAntiAlias = true
                            style = AndroidPaint.Style.STROKE
                            strokeWidth = glowStrokePx
                            color = colorArgb
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

                    drawGlow(AccueilRectangleColors.haloStart.toArgb(), dx = -2.0f, dy = -2.0f)
                    drawGlow(AccueilRectangleColors.haloEnd.toArgb(), dx = 2.0f, dy = 2.0f)

                    drawGlow(
                        AccueilRectangleColors.haloStart.copy(alpha = 0.55f).toArgb(),
                        dx = -1.0f,
                        dy = -1.0f
                    )
                    drawGlow(
                        AccueilRectangleColors.haloEnd.copy(alpha = 0.55f).toArgb(),
                        dx = 1.0f,
                        dy = 1.0f
                    )
                }
        )

        // 2) ✅ CONTENU (couche avant)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = contentMaxWidth) // ✅ AJOUT: limite max
                .heightIn(
                    min = AccueilRectangleSizes.contentMinHeight,
                    max = AccueilRectangleSizes.contentMaxHeight
                )
                .background(AccueilRectangleColors.background, corner)
                .border(
                    width = AccueilRectangleSizes.borderWidth,
                    brush = AccueilRectangleColors.borderBrush(),
                    shape = corner
                )
                // ✅ AJOUT: padding interne un poil réduit pour respirer
                // (si tu veux, remets AccueilRectangleSizes.innerPadding direct)
                .padding(horizontal = (AccueilRectangleSizes.innerPadding - 4.dp).coerceAtLeast(0.dp))
                .padding(vertical = (AccueilRectangleSizes.innerPadding - 6.dp).coerceAtLeast(0.dp))
        ) {
            Column {
                cycles.forEachIndexed { index, ui ->
                    // ✅ AJOUT: espace léger au-dessus / en-dessous de chaque row
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