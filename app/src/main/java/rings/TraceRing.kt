package rings

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun TraceRing(
    ringKey: String,
    percentText: String,
    sizeDp: Dp,
    ringColor: Color,
    percentColor: Color,
    spec: RingBreathSpec,
    modifier: Modifier = Modifier,
    showInnerPercent: Boolean = true,
) {
    val density = LocalDensity.current
    val sizePx = with(density) { sizeDp.toPx() }

    // Transition infinie (label = ringKey pour “stabilité” + debug)
    val infinite = rememberInfiniteTransition(label = "ring-$ringKey")

    // 0..1 aller/retour (respiration)
    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = spec.breatheMs, easing = spec.breatheEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // 0..1 boucle (wobble)
    val wobble by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = spec.wobbleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wobble"
    )

    // ✅ Mauve plus présent: bornes d’alpha plus hautes
    val alpha = (spec.baseAlpha + spec.ampAlpha * breathe)
        .coerceIn(0.18f, 0.98f)

    val scale = (1f + spec.ampScale * breathe).coerceIn(0.94f, 1.12f)

    val w = sin((wobble.toDouble() * 2.0 * PI)).toFloat()
    val wobbleFactor = (1f + spec.wobbleAmp * w).coerceIn(0.96f, 1.06f)

    // ✅ Anneau plus épais (sans devenir “gros gros”)
    fun strokePx(diameterPx: Float): Float {
        val ratio = (spec.strokeRatioBase + spec.strokeRatioAmp * breathe)
            .coerceIn(0.010f, 0.095f)

        val px = diameterPx * ratio

        return px.coerceIn(
            5.0f,
            (sizePx * 0.14f).coerceAtLeast(5f)
        )
    }

    // ✅ Texte: pourcentage plus gros, et qui suit la taille du ring (sizeDp)
    val pctInt = percentText.removeSuffix("%").toIntOrNull() ?: -1
    val isExtreme = (pctInt == 0 || pctInt == 100)

    // Base = 26% du diamètre, extrêmes un peu + gros
    val base = (sizeDp.value * 0.26f).sp
    val textSize = if (isExtreme) (sizeDp.value * 0.28f).sp else base

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val d = size.minDimension
            val radius = (d * 0.5f) * scale * wobbleFactor
            val stroke = strokePx(d)

            drawCircle(
                color = ringColor.copy(alpha = alpha),
                radius = radius,
                style = Stroke(width = stroke)
            )
        }

        if (showInnerPercent) {
            Text(
                text = percentText,
                color = percentColor,
                fontSize = textSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}