package com.maxjth.tracememoire.ui.systeme.terre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TerreCenterText(
    pct: Int?,
    modifier: Modifier = Modifier,
    deltaText: String? = null,
    numberScale: Float = 0.78f,
    offsetY: Dp = (-2).dp,
    showPercentSymbol: Boolean = false,
    nullSymbol: String = "·",
    colorOverride: Color? = null,
    glowColor: Color? = null
) {
    val safePct = pct?.coerceIn(0, 100)
    val centerText = safePct?.toString() ?: nullSymbol

    println("DEBUG TERRE_CENTER_TEXT → pct reçu = $pct | centerText = $centerText | deltaText reçu = $deltaText | showDelta = ${!deltaText.isNullOrBlank()}")

    val numberSize = when {
        safePct == null -> 24.sp
        centerText == "0" -> 39.sp
        centerText.length == 1 -> 46.sp
        centerText.length == 2 -> 43.sp
        else -> 38.sp
    }

    val coreWhite = colorOverride ?: WHITE_SOFT
    val accentGlow = glowColor ?: MAUVE.copy(alpha = 0.28f)
    val isNull = (safePct == null)
    val showDelta = !deltaText.isNullOrBlank()
    val safeDeltaText = deltaText?.trim().orEmpty()

    val percentSp = (numberSize.value * 0.46f).sp.let {
        if (it.value < 14f) 14.sp else it
    }

    val percentColor = if (isNull) {
        WHITE_SOFT.copy(alpha = 0.45f)
    } else {
        WHITE_SOFT.copy(alpha = 0.72f)
    }

    val letterSpacingMain = when {
        centerText == "0" -> (-0.82).sp
        centerText.length == 1 -> (-1.02).sp
        centerText.length == 2 -> (-1.08).sp
        else -> (-1.00).sp
    }

    val glowAlpha = when {
        isNull -> 0.10f
        centerText == "0" -> 0.18f
        else -> 0.22f
    }

    val glowBlur = when {
        isNull -> 12f
        centerText == "0" -> 14f
        else -> 18f
    }

    val coreBlur = when {
        isNull -> 6f
        centerText == "0" -> 8f
        else -> 10f
    }

    val numberOffsetX = if (centerText == "0") 1.dp else 0.dp
    val numberOffsetY = if (centerText == "0") (-1).dp else 0.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .offset(x = 2.dp, y = offsetY)
            .graphicsLayer {
                scaleX = numberScale
                scaleY = numberScale
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = centerText,
                modifier = Modifier.offset(x = numberOffsetX, y = numberOffsetY),
                style = TextStyle(
                    color = accentGlow.copy(alpha = glowAlpha),
                    fontSize = numberSize,
                    fontWeight = FontWeight.Black,
                    letterSpacing = letterSpacingMain,
                    shadow = Shadow(
                        color = accentGlow.copy(alpha = if (isNull) 0.14f else 0.26f),
                        offset = Offset.Zero,
                        blurRadius = glowBlur
                    )
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.offset(x = numberOffsetX, y = numberOffsetY)
            ) {
                Text(
                    text = centerText,
                    style = TextStyle(
                        color = if (isNull) coreWhite.copy(alpha = 0.55f) else coreWhite,
                        fontSize = numberSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = letterSpacingMain,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = if (isNull) 0.08f else 0.18f),
                            offset = Offset.Zero,
                            blurRadius = coreBlur
                        )
                    )
                )

                if (showPercentSymbol) {
                    Text(
                        text = "%",
                        fontSize = percentSp,
                        fontWeight = FontWeight.SemiBold,
                        color = percentColor,
                        modifier = Modifier.offset(x = 3.dp, y = 1.dp)
                    )
                }
            }
        }

        if (showDelta) {
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .width(58.dp)
                    .height(2.6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MAUVE.copy(alpha = 0.18f),
                                MAUVE.copy(alpha = 0.84f),
                                TURQUOISE.copy(alpha = 0.84f),
                                TURQUOISE.copy(alpha = 0.18f)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = safeDeltaText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (safeDeltaText.startsWith("-")) {
                    MAUVE.copy(alpha = 0.96f)
                } else {
                    TURQUOISE.copy(alpha = 0.96f)
                },
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.14f),
                        offset = Offset.Zero,
                        blurRadius = 6f
                    )
                )
            )
        }
    }
}