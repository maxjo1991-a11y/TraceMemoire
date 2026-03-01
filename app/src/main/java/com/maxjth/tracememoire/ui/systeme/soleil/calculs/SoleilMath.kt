package com.maxjth.tracememoire.ui.systeme.soleil.calculs

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * Helpers math utilisés par le Soleil / animations / géométrie
 * Fichier volontairement simple et stable
 */

fun lerpFloat(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t.coerceIn(0f, 1f)
}

/* Conversion degrés -> radians */
fun degToRad(deg: Float): Float {
    return (deg / 180f) * PI.toFloat()
}

/* Position orbitale X */
fun orbitX(angleRad: Float, radius: Float): Float {
    return cos(angleRad) * radius
}

/* Position orbitale Y */
fun orbitY(angleRad: Float, radius: Float): Float {
    return sin(angleRad) * radius
}