package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

data class DiamondButtonAnimations(
    val pressScale: Float,
    val pressGlow: Float,
    val textLift: Float,
    val haloPulse: Float,
    val corePulse: Float,
    val starPulse: Float,
    val starGlowPulse: Float,
    val starTwinkle: Float,
    val shimmerShift: Float,
    val slowRotate: Float,
    val textAlpha: Float,
    val enterAlpha: Float
)

@Composable
fun rememberDiamondButtonAnimations(
    pressed: Boolean
): DiamondButtonAnimations {
    val infinite = rememberInfiniteTransition(label = "diamond_anim")

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.972f else 1f,
        animationSpec = tween(
            durationMillis = 180,
            easing = FastOutSlowInEasing
        ),
        label = "pressScale"
    )

    val pressGlow by animateFloatAsState(
        targetValue = if (pressed) 1.16f else 1f,
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "pressGlow"
    )

    val textLift by animateFloatAsState(
        targetValue = if (pressed) -1.6f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "textLift"
    )

    val haloPulse by infinite.animateFloat(
        initialValue = 0.975f,
        targetValue = 1.11f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4300,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    val corePulse by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    // Respiration principale de l’étoile centrale
    val starPulse by infinite.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.62f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1900
                0.82f at 0
                1.02f at 320
                1.46f at 920
                1.62f at 1200
                1.18f at 1540
                0.82f at 1900
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "starPulse"
    )

    // Halo plus ample derrière l’étoile
    val starGlowPulse by infinite.animateFloat(
        initialValue = 0.78f,
        targetValue = 1.58f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2200
                0.78f at 0
                0.96f at 300
                1.28f at 980
                1.58f at 1360
                1.08f at 1800
                0.78f at 2200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "starGlowPulse"
    )

    // Petit scintillement vivant, comme une étoile
    val starTwinkle by infinite.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2600
                0.92f at 0
                0.98f at 320
                1.04f at 700
                1.20f at 1080
                1.00f at 1320
                1.12f at 1680
                0.96f at 2100
                1.22f at 2320
                0.92f at 2600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "starTwinkle"
    )

    val shimmerShift by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerShift"
    )

    val slowRotate by infinite.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slowRotate"
    )

    val textAlpha by infinite.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textAlpha"
    )

    val enterAlpha by infinite.animateFloat(
        initialValue = 0.26f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "enterAlpha"
    )

    return DiamondButtonAnimations(
        pressScale = pressScale,
        pressGlow = pressGlow,
        textLift = textLift,
        haloPulse = haloPulse,
        corePulse = corePulse,
        starPulse = starPulse,
        starGlowPulse = starGlowPulse,
        starTwinkle = starTwinkle,
        shimmerShift = shimmerShift,
        slowRotate = slowRotate,
        textAlpha = textAlpha,
        enterAlpha = enterAlpha
    )
}