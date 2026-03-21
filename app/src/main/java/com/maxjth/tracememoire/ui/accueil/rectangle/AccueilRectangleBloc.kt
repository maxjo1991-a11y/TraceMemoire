package com.maxjth.tracememoire.ui.accueil.rectangle

import android.graphics.BlurMaskFilter
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel
import com.maxjth.tracememoire.ui.accueil.rectangle.style.AccueilRectangleSizes
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.systeme.cyclecolors.CycleColors

@Composable
fun AccueilRectangleBloc(
    pMatin: Int?,
    pJour: Int?,
    pSoir: Int?,
    pNuit: Int?,
    dotsForCycleKey: (String) -> List<Boolean>,
    modifier: Modifier = Modifier,
    activeCycleKey: String? = null,
    createdAtMillisForCycleKey: ((String) -> Long?)? = null,
) {

    fun isActive(key: String): Boolean =
        activeCycleKey?.trim()?.equals(key.trim(), ignoreCase = true) == true

    fun createdAt(key: String): Long? =
        createdAtMillisForCycleKey?.invoke(key.trim().uppercase())

    fun safeDots(
        cycleKey: String,
        percent: Int?,
        createdAtMillis: Long?,
        originalDots: List<Boolean>
    ): List<Boolean> {
        return originalDots
    }

    fun safeCycleValue(value: Int?): Int? =
        value?.coerceIn(0, 400)

    fun displayRightText(value: Int?): String =
        safeCycleValue(value)?.toString() ?: "—"

    fun trajectoryText(previous: Int?, current: Int?): String? {
        val curr = safeCycleValue(current) ?: return null
        val prev = safeCycleValue(previous)

        return when {
            prev == null -> "↗ Début du cycle"
            curr > prev -> "↗ Montée"
            curr < prev -> "↘ Descente"
            else -> "→ Stable"
        }
    }

    val matinTrajectory = trajectoryText(
        previous = null,
        current = pMatin
    )

    val jourTrajectory = trajectoryText(
        previous = pMatin,
        current = pJour
    )

    val soirTrajectory = trajectoryText(
        previous = pJour,
        current = pSoir
    )

    val nuitTrajectory = trajectoryText(
        previous = pSoir,
        current = pNuit
    )

    val cycles = buildList {
        if (TypeCycleHome.values().contains(TypeCycleHome.NUIT)) {
            val nuitCreatedAt = createdAt("NUIT")
            val nuitDots = safeDots(
                cycleKey = "NUIT",
                percent = pNuit,
                createdAtMillis = nuitCreatedAt,
                originalDots = dotsForCycleKey("NUIT")
            )
            add(
                AccueilCycleUiModel.from(
                    label = "Nuit",
                    cycleKey = "NUIT",
                    percent = pNuit,
                    dots = nuitDots,
                    createdAtMillis = nuitCreatedAt,
                    isActive = isActive("NUIT"),
                    trajectoryText = nuitTrajectory
                ).copy(
                    rightText = displayRightText(pNuit)
                )
            )
        }

        val matinCreatedAt = createdAt("MATIN")
        val matinDots = safeDots(
            cycleKey = "MATIN",
            percent = pMatin,
            createdAtMillis = matinCreatedAt,
            originalDots = dotsForCycleKey("MATIN")
        )
        add(
            AccueilCycleUiModel.from(
                label = "Matin",
                cycleKey = "MATIN",
                percent = pMatin,
                dots = matinDots,
                createdAtMillis = matinCreatedAt,
                isActive = isActive("MATIN"),
                trajectoryText = matinTrajectory
            ).copy(
                rightText = displayRightText(pMatin)
            )
        )

        val jourCreatedAt = createdAt("JOUR")
        val jourDots = safeDots(
            cycleKey = "JOUR",
            percent = pJour,
            createdAtMillis = jourCreatedAt,
            originalDots = dotsForCycleKey("JOUR")
        )
        add(
            AccueilCycleUiModel.from(
                label = "Jour",
                cycleKey = "JOUR",
                percent = pJour,
                dots = jourDots,
                createdAtMillis = jourCreatedAt,
                isActive = isActive("JOUR"),
                trajectoryText = jourTrajectory
            ).copy(
                rightText = displayRightText(pJour)
            )
        )

        val soirCreatedAt = createdAt("SOIR")
        val soirDots = safeDots(
            cycleKey = "SOIR",
            percent = pSoir,
            createdAtMillis = soirCreatedAt,
            originalDots = dotsForCycleKey("SOIR")
        )
        add(
            AccueilCycleUiModel.from(
                label = "Soir",
                cycleKey = "SOIR",
                percent = pSoir,
                dots = soirDots,
                createdAtMillis = soirCreatedAt,
                isActive = isActive("SOIR"),
                trajectoryText = soirTrajectory
            ).copy(
                rightText = displayRightText(pSoir)
            )
        )
    }

    val wrapperPaddingVertical = AccueilRectangleSizes.wrapperPaddingVertical
    val haloOuterPad = AccueilRectangleSizes.haloPadding
    val haloCorner = AccueilRectangleSizes.haloCorner
    val haloBlur = AccueilRectangleSizes.haloBlur
    val haloStroke = AccueilRectangleSizes.borderWidth

    val haloColors = cycleHaloColors(activeCycleKey)

    val outlineColor = Color.White.copy(alpha = 0.10f)
    val outlineStrokePx = 1.2f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AccueilRectangleSizes.wrapperMinHeight)
            .padding(vertical = wrapperPaddingVertical)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(haloOuterPad)
                .drawBehind {
                    val cornerPx = haloCorner.toPx()
                    val strokePx = haloStroke.toPx()
                    val blurPx = haloBlur.toPx()

                    val inset = strokePx / 2f
                    val w = size.width - inset * 2f
                    val h = size.height - inset * 2f

                    fun drawGlow(color: Color, dx: Float, dy: Float) {
                        val paint = AndroidPaint().apply {
                            isAntiAlias = true
                            style = AndroidPaint.Style.STROKE
                            strokeWidth = strokePx
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

                    drawGlow(haloColors.first, -1.5f, -1.5f)
                    drawGlow(haloColors.second, 1.5f, 1.5f)

                    val outline = AndroidPaint().apply {
                        isAntiAlias = true
                        style = AndroidPaint.Style.STROKE
                        strokeWidth = outlineStrokePx
                        color = outlineColor.toArgb()
                    }

                    drawContext.canvas.nativeCanvas.drawRoundRect(
                        inset,
                        inset,
                        inset + w,
                        inset + h,
                        cornerPx,
                        cornerPx,
                        outline
                    )
                }
        )

        AccueilRectangleGrid(
            cycles = cycles,
            activeCycleKey = activeCycleKey,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun cycleHaloColors(cycleKey: String?): Pair<Color, Color> {
    return when (cycleKey?.trim()?.uppercase()) {
        "MATIN" -> Pair(
            CycleColors.MatinStart.copy(alpha = 0.22f),
            CycleColors.MatinEnd.copy(alpha = 0.18f)
        )

        "JOUR" -> Pair(
            CycleColors.JourStart.copy(alpha = 0.22f),
            CycleColors.JourEnd.copy(alpha = 0.18f)
        )

        "SOIR" -> Pair(
            CycleColors.SoirStart.copy(alpha = 0.22f),
            CycleColors.SoirEnd.copy(alpha = 0.18f)
        )

        "NUIT" -> Pair(
            CycleColors.NuitStart.copy(alpha = 0.22f),
            CycleColors.NuitEnd.copy(alpha = 0.18f)
        )

        else -> Pair(
            CycleColors.JourStart.copy(alpha = 0.22f),
            CycleColors.JourEnd.copy(alpha = 0.18f)
        )
    }
}