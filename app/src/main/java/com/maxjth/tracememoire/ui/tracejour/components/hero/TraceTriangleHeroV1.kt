package com.maxjth.tracememoire.ui.tracejour.components.hero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun TraceTriangleHeroV1(
    valuePercent: Int,
    subtitleTint: Color,
    onValueChange: (Int) -> Unit,
    onValueCommit: (Int) -> Unit
) {
    val clamped = valuePercent.coerceIn(0, 100)

    Box(
        modifier = Modifier
            .size(320.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onValueCommit(clamped) }
                ) { change, drag ->
                    val delta = (-drag.y / 3f)
                    val newValue = (clamped + delta).roundToInt().coerceIn(0, 100)
                    onValueChange(newValue)
                    change.consume()
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Triangle plein écran du Canvas
            val triangle = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(0f, h)
                lineTo(w, h)
                close()
            }

            // Fond léger du triangle (la "brume" de base)
            drawPath(
                path = triangle,
                color = subtitleTint.copy(alpha = 0.15f)
            )

            // ✅ Remplissage (IMPORTANT) : ON CLIP DANS LE TRIANGLE
            val fillHeight = h * (clamped / 100f)

            clipPath(triangle) {
                drawRect(
                    color = subtitleTint.copy(alpha = 0.55f),
                    topLeft = Offset(0f, h - fillHeight),
                    size = Size(w, fillHeight)
                )
            }
        }
    }
}