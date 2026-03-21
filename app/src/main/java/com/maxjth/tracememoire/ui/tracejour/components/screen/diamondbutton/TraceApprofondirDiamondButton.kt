package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.*
import com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.animation.rememberDiamondButtonAnimations
import com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw.*
import com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.style.DiamondButtonStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TraceApprofondirDiamondButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    var pressed by remember { mutableStateOf(false) }

    val anim = rememberDiamondButtonAnimations(pressed = pressed)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(DiamondButtonStyle.BUTTON_SIZE_DP.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            tryAwaitRelease()
                            pressed = false
                        },
                        onTap = {
                            scope.launch {
                                pressed = true
                                delay(90)
                                pressed = false
                            }
                            onClick()
                        }
                    )
                }
        ) {
            Canvas(
                modifier = Modifier
                    .size(DiamondButtonStyle.CANVAS_SIZE_DP.dp)
                    .graphicsLayer {
                        scaleX = anim.pressScale
                        scaleY = anim.pressScale
                    }
            ) {
                val c = center

                val outerDiamondSize =
                    size.minDimension * DiamondButtonStyle.OUTER_DIAMOND_RATIO

                val innerDiamondSize =
                    outerDiamondSize * DiamondButtonStyle.INNER_DIAMOND_RATIO

                val haloOuterRadius =
                    size.minDimension *
                            DiamondButtonStyle.HALO_OUTER_RATIO *
                            anim.haloPulse *
                            anim.pressGlow

                val haloInnerRadius =
                    size.minDimension *
                            DiamondButtonStyle.HALO_INNER_RATIO *
                            anim.haloPulse *
                            anim.pressGlow

                val stageY =
                    c.y + outerDiamondSize * DiamondButtonStyle.STAGE_Y_MULTIPLIER

                val enterY =
                    stageY + DiamondButtonStyle.ENTER_Y_OFFSET_DP.dp.toPx()

                drawDiamondButtonHalo(
                    center = c,
                    outerDiamondSize = outerDiamondSize,
                    haloOuterRadius = haloOuterRadius,
                    haloInnerRadius = haloInnerRadius
                )

                drawDiamondButtonStage(
                    center = c,
                    sizeWidth = size.width,
                    stageY = stageY,
                    shimmerShift = anim.shimmerShift
                )

                drawDiamondButtonDiamond(
                    center = c,
                    outerDiamondSize = outerDiamondSize,
                    innerDiamondSize = innerDiamondSize,
                    slowRotate = anim.slowRotate,
                    runnerProgress = anim.runnerProgress,
                    innerRunnerProgress = anim.innerRunnerProgress,
                    bridgeRunnerProgress = anim.bridgeRunnerProgress,
                    driftRunnerProgress = anim.driftRunnerProgress,
                    rippleProgress = anim.rippleProgress
                )

                drawDiamondButtonCenterStar(
                    center = c,
                    starPulse = anim.starPulse,
                    starGlowPulse = anim.starGlowPulse,
                    starTwinkle = anim.starTwinkle,
                    pressGlow = anim.pressGlow,
                    rippleProgress = anim.rippleProgress
                )

                // 🔽 TEXTE BAS UNIQUEMENT (Entrer)

                val enterLayout = textMeasurer.measure(
                    text = "Entrer",
                    style = TextStyle(
                        color = WHITE_SOFT.copy(alpha = anim.enterAlpha),
                        fontSize = DiamondButtonStyle.ENTER_FONT_SP.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = DiamondButtonStyle.ENTER_LETTER_SPACING.sp,
                        shadow = Shadow(
                            color = TURQUOISE.copy(alpha = 0.12f),
                            blurRadius = 5f
                        )
                    )
                )

                drawText(
                    textLayoutResult = enterLayout,
                    topLeft = Offset(
                        x = c.x - enterLayout.size.width / 2f,
                        y = enterY
                    )
                )
            }
        }
    }
}