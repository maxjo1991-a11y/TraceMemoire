package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

fun quadraticBezierPoint(
    start: Offset,
    end: Offset,
    control: Offset,
    t: Float
): Offset {

    val oneMinusT = 1f - t

    val x =
        oneMinusT * oneMinusT * start.x +
                2f * oneMinusT * t * control.x +
                t * t * end.x

    val y =
        oneMinusT * oneMinusT * start.y +
                2f * oneMinusT * t * control.y +
                t * t * end.y

    return Offset(x, y)
}

fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {

    val path = Path()

    val points = 4
    val step = Math.PI / points
    val startAngle = -Math.PI / 2.0

    repeat(points * 2) { i ->

        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + step * i

        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()

    drawPath(
        path = path,
        color = color
    )
}

