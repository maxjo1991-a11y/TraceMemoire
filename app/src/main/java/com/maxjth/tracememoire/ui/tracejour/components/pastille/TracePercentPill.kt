package com.maxjth.tracememoire.ui.tracejour.components.pastille

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


private val MAUVE = Color(0xFF7A63C6)
private val TURQUOISE = Color(0xFF00A3A3)

private val BAR_HEIGHT = 10.dp
private val KNOB_RADIUS = 16.dp

@Composable
fun TracePercentPill(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onCommit: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragging by remember { mutableStateOf(false) }
    val p = progress.coerceIn(0f, 1f)

    BoxWithConstraints(modifier = modifier) {

        val w = maxWidth.coerceAtMost(360.dp)
        val h = BAR_HEIGHT

        Canvas(
            modifier = Modifier
                .size(
                    width = w,
                    height = maxOf(h, KNOB_RADIUS * 2)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { dragging = false; onCommit(p) },
                        onDragCancel = { dragging = false; onCommit(p) },
                        onDrag = { change, _ ->
                            val x = change.position.x
                            onProgressChange((x / size.width).coerceIn(0f, 1f))
                        }
                    )
                }
        ) {

            val barTop = (size.height - h.toPx()) / 2f
            val barRadius = h.toPx() / 2f

            // BARRE
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(MAUVE, TURQUOISE)
                ),
                topLeft = Offset(0f, barTop),
                size = androidx.compose.ui.geometry.Size(
                    size.width * p,
                    h.toPx()
                ),
                cornerRadius = CornerRadius(barRadius, barRadius)
            )

            // PASTILLE
            drawCircle(
                color = lerp(MAUVE, TURQUOISE, p),
                radius = KNOB_RADIUS.toPx(),
                center = Offset(size.width * p, size.height / 2f)
            )
        }
    }
}