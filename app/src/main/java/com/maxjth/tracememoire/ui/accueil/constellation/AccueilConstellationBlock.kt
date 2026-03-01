// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/constellation/AccueilConstellationBlock.kt
package com.maxjth.tracememoire.ui.accueil.constellation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maxjth.tracememoire.ui.systeme.lune.motion.LuneAttachedMotion
import com.maxjth.tracememoire.ui.systeme.lune.visuel.LuneCountCircle
import com.maxjth.tracememoire.ui.systeme.soleil.components.HomeSoleilCircle
import com.maxjth.tracememoire.ui.systeme.soleil.components.SoleilVariant
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.systeme.terre.components.TerrePercentCircle
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import java.time.LocalDateTime

@Composable
fun AccueilConstellationBlock(
    // ✅ valeurs (obligatoires)
    luneCount: Int,
    luneDeltaToday: Int,
    yearlyPercent: Int?,
    soleilPercent: Int?,

    // ✅ (Compose) modifier en premier optionnel
    modifier: Modifier = Modifier,

    // ✅ override optionnel (doit matcher HomeSoleilCircle)
    nowOverride: LocalDateTime? = null,

    // ✅ tailles (par défaut = celles que tu avais)
    clusterSize: Dp = 304.dp,
    moonContainerSize: Dp = 142.dp,
    moonVisualSizeDp: Int = 102,
    sideContainerSize: Dp = 130.dp,
    sideVisualSizeDp: Int = 108,

    // ✅ placements (par défaut = tes offsets)
    topOffsetY: Dp = (-92).dp,
    bottomOffsetY: Dp = 10.dp,
    sideOffsetX: Dp = 120.dp,

    // ✅ NOUVEAU : rotation très douce du glow
    glowRotationPeriodMs: Int = 980_000 // 120s = super doux
) {
    // ✅ UN SEUL "NOW" PARTAGÉ (LocalDateTime?)
    val homeNow: LocalDateTime? = nowOverride ?: rememberHomeNow()

    // ✅ Animations (cluster + glow)
    val inf = rememberInfiniteTransition(label = "accueil_cluster_float")

    // Flottement doux (cluster)
    val floatY by inf.animateFloat(
        initialValue = -1.8f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val floatX by inf.animateFloat(
        initialValue = 1.2f,
        targetValue = -1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatX"
    )

    val floatScale by inf.animateFloat(
        initialValue = 0.996f,
        targetValue = 1.004f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatScale"
    )

    // ✅ Rotation ultra douce du glow (indépendante des planètes)
    val glowRot by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = glowRotationPeriodMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowRot"
    )

    Box(
        modifier = modifier
            .size(clusterSize)
            .offset(x = floatX.dp, y = floatY.dp)
            .graphicsLayer(scaleX = floatScale, scaleY = floatScale),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Glow derrière (profondeur) — tourne doucement
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(rotationZ = glowRot) // <- ICI la rotation
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.18f),
                            TURQUOISE.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        radius = 520f
                    )
                )
                .zIndex(0f)
        )

        // ── Bas gauche : TERRE = % ANNUEL
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -sideOffsetX, y = bottomOffsetY)
                .size(sideContainerSize)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            TerrePercentCircle(
                yearlyPercent,
                modifier = Modifier.matchParentSize(),
                sizeDp = sideVisualSizeDp,
                numberScale = 0.90f,
                marsBreathSlowFactor = 1.25f
            )
        }

        // ── Bas droite : SOLEIL
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = sideOffsetX, y = bottomOffsetY)
                .size(sideContainerSize)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            HomeSoleilCircle(
                traceCount = 0,
                progressPercent = soleilPercent,
                nowOverride = homeNow,
                modifier = Modifier.matchParentSize(),
                sizeDp = sideContainerSize,
                variant = SoleilVariant.SECONDARY,
                numberEntryScale = 0.72f
            )
        }

        // ── Haut : LUNE
        LuneAttachedMotion(
            swingX = 6.5f,
            swingY = 4.5f,
            periodMs = 5200,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = topOffsetY)
                .size(moonContainerSize)
                .zIndex(2f)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(BG_DEEP, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                LuneCountCircle(
                    value = luneCount,
                    deltaToday = luneDeltaToday,
                    sizeDp = moonVisualSizeDp,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}