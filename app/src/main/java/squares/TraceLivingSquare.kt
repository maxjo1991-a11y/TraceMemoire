// FILE: app/src/main/java/squares/TraceLivingSquare.kt
package squares

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.runtime.getValue
// ─────────────────────────────────────────
// Carré vivant (signature visuelle)
// - pas de cercle / pas de triangle
// - respiration + micro wobble
// - suit la pastille (% -> offset Y)
// - variations: lignes / points / simple (personnalité)
// ─────────────────────────────────────────

private enum class SquareStyle { SIMPLE, DOTS, LINES }

private data class SquarePersona(
    val breatheMs: Int,
    val wobbleMs: Int,
    val wobbleAmpDp: Float,
    val glowBase: Float,
    val glowAmp: Float,
    val style: SquareStyle,
    val prefersTurquoise: Boolean
)

private fun stableSeed(seedBase: String, sliderKey: String): Int {
    val s = "$seedBase|$sliderKey"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SquarePersona {
    val r = Random(seed)
    val style = when (r.nextInt(0, 3)) {
        0 -> SquareStyle.SIMPLE
        1 -> SquareStyle.DOTS
        else -> SquareStyle.LINES
    }
    return SquarePersona(
        breatheMs = r.nextInt(2600, 5200),
        wobbleMs = r.nextInt(1900, 4200),
        wobbleAmpDp = r.nextFloat() * 2.2f + 0.6f,     // 0.6..2.8 dp
        glowBase = r.nextFloat() * 0.10f + 0.14f,      // 0.14..0.24
        glowAmp = r.nextFloat() * 0.12f + 0.06f,       // 0.06..0.18
        style = style,
        prefersTurquoise = r.nextBoolean()
    )
}

/**
 * @param percent     0..100 (le carré suit ça en Y)
 * @param enabled     si faux, tout devient plus discret
 * @param sliderKey   clé stable (humeur/energie/corps/presence)
 * @param seedBase    graine stable (ex: date + cycleKey etc.)
 * @param size        taille du carré
 * @param cornerDp    coins arrondis (mets 6.dp doux ou 2.dp plus sec)
 * @param strokeDp    épaisseur du trait (plus épais qu’un texte, moins qu’un cercle)
 * @param mauve       couleur identité
 * @param turquoise   couleur alternative
 * @param staleBlink  si true: petit “clignement” (optionnel plus tard)
 */
@Composable
fun TraceLivingSquare(
    percent: Int,
    enabled: Boolean,
    sliderKey: String,
    seedBase: String,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    cornerDp: Dp = 6.dp,
    strokeDp: Dp = 2.dp,
    mauve: Color,
    turquoise: Color,
    staleBlink: Boolean = false
) {
    val pct = percent.coerceIn(0, 100)
    val t = pct / 100f

    // Suit la pastille (0% bas -> descend, 100% haut -> monte)
    // Ajuste l’amplitude si tu veux plus de mouvement.
    val followRangeDp = 8f
    val followYOffsetDp = ((0.5f - t) * 2f) * followRangeDp // -8..+8 dp (sens naturel)
    val followYOffset = followYOffsetDp.dp

    val persona = remember(seedBase, sliderKey) {
        buildPersona(stableSeed(seedBase, sliderKey))
    }

    val infinite = rememberInfiniteTransition(label = "sqAlive_$sliderKey")

    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.breatheMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sqBreathe_$sliderKey"
    )

    val wobblePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.wobbleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sqWobble_$sliderKey"
    )

    // (optionnel) mini “blink” si staleBlink
    val blink by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(680, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sqBlink_$sliderKey"
    )

    val wob = sin(wobblePhase * 2f * PI).toFloat()
    val wobX = (wob * persona.wobbleAmpDp).dp
    val wobY = (sin((wobblePhase * 2f * PI) + (PI / 2f)).toFloat() * (persona.wobbleAmpDp * 0.7f)).dp

    val scale = (0.98f + 0.04f * breathe) * (if (enabled) 1f else 0.97f)

    // Couleur du carré : mauve / turquoise “par jour”
    // Ici: personnalité + un léger basculement selon t
    val base = when {
        persona.prefersTurquoise && t > 0.60f -> turquoise
        !persona.prefersTurquoise && t < 0.40f -> mauve
        else -> if (persona.prefersTurquoise) turquoise else mauve
    }

    val calm = if (enabled) 1f else 0.55f
    val blinkFactor = if (staleBlink) (0.72f + 0.28f * blink) else 1f

    val edgeAlpha = ((persona.glowBase + persona.glowAmp * breathe) * calm * blinkFactor)
        .coerceIn(0.06f, 0.90f)

    val haloAlpha = (edgeAlpha * 0.28f).coerceIn(0.03f, 0.35f)

    val strokePx = with(androidx.compose.ui.platform.LocalDensity.current) { strokeDp.toPx() }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(cornerDp))
            // le carré suit la pastille
            .graphicsLayer(
                translationX = with(androidx.compose.ui.platform.LocalDensity.current) { wobX.toPx() },
                translationY = with(androidx.compose.ui.platform.LocalDensity.current) { (followYOffset + wobY).toPx() }
            )
    ) {
        Canvas(modifier = Modifier.size(size)) {

            // IMPORTANT:
            // Dans Canvas, utilise TOUJOURS `this.size` (px), pas ton paramètre Dp `size`
            val minDim = this.size.width.coerceAtMost(this.size.height)
            val cr = cornerDp.toPx().coerceAtMost(minDim / 2f)

            // Halo externe léger (premium vibe)
            drawRoundRect(
                color = base.copy(alpha = haloAlpha),
                size = this.size,
                cornerRadius = CornerRadius(cr, cr),
                style = Stroke(width = strokePx + 3f)
            )

            // Trait principal
            drawRoundRect(
                color = base.copy(alpha = edgeAlpha),
                size = this.size,
                cornerRadius = CornerRadius(cr, cr),
                style = Stroke(width = strokePx)
            )

            // Personnalité (points / lignes) — ultra discret
            when (persona.style) {
                SquareStyle.SIMPLE -> {
                    // rien de plus
                }

                SquareStyle.DOTS -> {
                    val a = (edgeAlpha * 0.22f).coerceIn(0.03f, 0.18f)
                    val dotColor = base.copy(alpha = a)

                    val pad = minDim * 0.22f
                    val w = this.size.width
                    val h = this.size.height
                    val p1 = Offset(pad, pad)
                    val p2 = Offset(w - pad, pad)
                    val p3 = Offset(pad, h - pad)

                    val r = (minDim * 0.05f) * (0.9f + 0.2f * breathe)
                    drawCircle(dotColor, radius = r, center = p1)
                    drawCircle(dotColor, radius = r * 0.85f, center = p2)
                    drawCircle(dotColor, radius = r * 0.75f, center = p3)
                }

                SquareStyle.LINES -> {
                    val a = (edgeAlpha * 0.18f).coerceIn(0.03f, 0.16f)
                    val lineColor = base.copy(alpha = a)

                    val w = this.size.width
                    val h = this.size.height
                    val pad = minDim * 0.20f
                    val y = (h * 0.50f) + (abs(wob) * (minDim * 0.08f))

                    drawLine(
                        color = lineColor,
                        start = Offset(pad, y),
                        end = Offset(w - pad, y),
                        strokeWidth = (strokePx * 0.75f).coerceAtLeast(1f)
                    )

                    drawLine(
                        color = lineColor.copy(alpha = a * 0.8f),
                        start = Offset(pad, y + (minDim * 0.12f)),
                        end = Offset(w - pad, y + (minDim * 0.12f)),
                        strokeWidth = (strokePx * 0.55f).coerceAtLeast(1f)
                    )
                }
            }
        }
    }
}