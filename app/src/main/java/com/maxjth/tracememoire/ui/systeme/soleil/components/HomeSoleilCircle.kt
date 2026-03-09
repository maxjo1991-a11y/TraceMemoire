package com.maxjth.tracememoire.ui.systeme.soleil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.systeme.soleil.adn.rememberHomeSoleilParams
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.systeme.soleil.visual.HomeSoleilCanvas
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import java.time.LocalDateTime

enum class SoleilVariant { PRIMARY, SECONDARY }

@Composable
fun HomeSoleilCircle(
    traceCount: Int,
    progressPercent: Int? = null,
    modifier: Modifier = Modifier,
    numberEntryScale: Float = 1f,
    nowOverride: LocalDateTime? = null,
    sizeDp: Dp = 240.dp,
    variant: SoleilVariant = SoleilVariant.PRIMARY,
    deltaText: String? = null
) {
    val now = nowOverride ?: rememberHomeNow()

    val p = rememberHomeSoleilParams(
        now = now,
        progressPercent = progressPercent
    )

    val baseSize = 240f
    val sizeScale = (sizeDp.value / baseSize).coerceIn(0.70f, 1.22f)

    val isSecondary = (variant == SoleilVariant.SECONDARY)
    val secondaryScale = if (isSecondary) 0.965f else 1f

    val showDelta = !deltaText.isNullOrBlank()

    Box(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer(
                scaleX = p.finalScaleX * secondaryScale,
                scaleY = p.finalScaleY * secondaryScale,
                rotationZ = p.tiltDeg
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fond sombre principal
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(BG_DEEP, CircleShape)
        )

        // Canvas principal du Soleil
        HomeSoleilCanvas(
            displayPct = p.displayPct,
            strokeDp = p.strokeDp,
            ringBase = p.ringBase,
            ringBright = p.ringBright,
            neonGlowColor = p.neonGlowColor,
            haloColorMain = p.haloColorMain,
            haloColorSoft = p.haloColorSoft,
            irisDeep = p.irisDeep,
            irisMauve = p.irisMauve,
            irisAccent = p.irisAccent,
            pupilDeep = p.pupilDeep,
            starPoints = p.starPoints,
            modifier = Modifier.matchParentSize(),
            sizeDp = sizeDp,
            isSecondary = isSecondary
        )

        // Reflet interne discret : Soleil plus noble / plus net
        Box(
            modifier = Modifier
                .size(sizeDp * 0.84f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = sizeDp.value * 0.18f
                    ),
                    shape = CircleShape
                )
                .graphicsLayer(
                    translationX = -sizeDp.value * 0.035f,
                    translationY = -sizeDp.value * 0.045f,
                    alpha = 0.95f,
                    blendMode = BlendMode.SrcOver
                )
        )

        Box(
            modifier = Modifier.offset(y = (-2).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                val centerText = p.centerText.trim()
                val digitCount = centerText.length

                val adjustedSp = when {
                    centerText == "0" -> p.numberSizeSp * 0.74f
                    digitCount == 1 -> p.numberSizeSp * 0.96f
                    digitCount == 2 -> p.numberSizeSp * 0.60f
                    digitCount >= 3 -> p.numberSizeSp * 0.68f
                    else -> p.numberSizeSp * 0.68f
                }

                val size = (adjustedSp * sizeScale * numberEntryScale).sp

                Text(
                    text = centerText,
                    style = TextStyle(
                        color = WHITE_SOFT.copy(alpha = 0.98f),
                        fontSize = size,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.00).sp,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.18f),
                            offset = Offset(0f, 0.6f * sizeScale),
                            blurRadius = 8f * sizeScale
                        )
                    )
                )

                if (showDelta) {
                    Spacer(modifier = Modifier.height((5f * sizeScale).dp))

                    Box(
                        modifier = Modifier
                            .width((56f * sizeScale).dp)
                            .height((2.4f * sizeScale).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MAUVE.copy(alpha = 0.22f),
                                        MAUVE.copy(alpha = 0.82f),
                                        TURQUOISE.copy(alpha = 0.82f),
                                        TURQUOISE.copy(alpha = 0.22f)
                                    )
                                ),
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.height((5f * sizeScale).dp))

                    Text(
                        text = deltaText!!.trim(),
                        fontSize = (24f * sizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deltaText.trim().startsWith("-")) {
                            MAUVE.copy(alpha = 0.96f)
                        } else {
                            TURQUOISE.copy(alpha = 0.96f)
                        },
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.14f),
                                offset = Offset.Zero,
                                blurRadius = 6f * sizeScale
                            )
                        )
                    )
                }
            }
        }
    }
}