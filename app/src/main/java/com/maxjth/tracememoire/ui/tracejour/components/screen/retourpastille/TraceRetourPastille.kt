package com.maxjth.tracememoire.ui.tracejour.components.screen.retourpastille

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerEventPass

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TraceRetourPastille(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .offset {
                IntOffset(
                    x = offsetX.roundToInt(),
                    y = offsetY.roundToInt()
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)

                    var totalDx = 0f
                    var totalDy = 0f
                    var isDragging = false
                    val tapSlop = 10f

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull() ?: break

                        if (change.changedToUpIgnoreConsumed()) {
                            if (!isDragging && abs(totalDx) < tapSlop && abs(totalDy) < tapSlop) {
                                onClick()
                            }
                            break
                        }

                        val delta = change.positionChange()
                        if (delta != Offset.Zero) {
                            totalDx += delta.x
                            totalDy += delta.y

                            if (abs(totalDx) >= tapSlop || abs(totalDy) >= tapSlop) {
                                isDragging = true
                            }

                            if (isDragging) {
                                offsetX += delta.x
                                offsetY += delta.y
                                change.consume()
                            }
                        }
                    }
                }
            }
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        BG_DEEP.copy(alpha = 0.92f),
                        BG_DEEP.copy(alpha = 0.82f)
                    )
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = 0.32f),
                        TURQUOISE.copy(alpha = 0.22f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Retour",
            fontSize = 12.sp,
            color = WHITE_SOFT.copy(alpha = 0.90f)
        )
    }
}