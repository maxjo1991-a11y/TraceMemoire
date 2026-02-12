// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/depth/TraceDepthSection.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.depth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

private val CARD_RADIUS = 22.dp

private fun cardBgBrush() = Brush.verticalGradient(
    colors = listOf(
        BG_SOFT.copy(alpha = 0.18f),
        BG_SOFT.copy(alpha = 0.26f),
        BG_SOFT.copy(alpha = 0.18f)
    )
)

@Composable
fun TraceDepthSection(
    isPremium: Boolean,
    modifier: Modifier = Modifier,
    // ✅ contenu injecté (tes sliders premium)
    content: @Composable (contentEnabled: Boolean) -> Unit
) {
    var opened by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {

        // ─────────────────────────────
        // HEADER — "Approfondissement" (carte)
        // ─────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ✅ glow mauve ultra subtil (derrière la carte)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(CARD_RADIUS))
                    .background(MAUVE.copy(alpha = 0.10f))
                    .blur(20.dp)
                    .alpha(0.55f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CARD_RADIUS))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = indication
                    ) { opened = !opened }
                    .background(cardBgBrush())
                    .border(
                        width = 1.dp,
                        color = MAUVE.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(CARD_RADIUS)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = true)
                ) {
                    Text(
                        text = "Approfondissement",
                        color = MAUVE.copy(alpha = 0.95f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = if (opened) "▴" else "▾",
                        color = WHITE_SOFT.copy(alpha = 0.35f),
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = if (isPremium) "Premium" else "Premium verrouillée",
                    color = TURQUOISE.copy(alpha = if (isPremium) 0.85f else 0.55f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ─────────────────────────────
        // CONTENU (ouvert / fermé) — carte identique
        // ─────────────────────────────
        AnimatedVisibility(
            visible = opened,
            enter = expandVertically(animationSpec = tween(260, easing = FastOutSlowInEasing)),
            exit = shrinkVertically(animationSpec = tween(220, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // ✅ glow mauve ultra subtil (derrière la carte)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(CARD_RADIUS))
                        .background(MAUVE.copy(alpha = 0.08f))
                        .blur(22.dp)
                        .alpha(0.55f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CARD_RADIUS))
                        .background(cardBgBrush())
                        .border(
                            width = 1.dp,
                            color = MAUVE.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(CARD_RADIUS)
                        )
                        .padding(16.dp)
                ) {

                    // ✅ Tes sliders premium ici (floutés + désactivés si pas premium)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .blur(if (isPremium) 0.dp else 7.dp)
                            .alpha(if (isPremium) 1f else 0.60f)
                    ) {
                        content(isPremium)
                    }

                    Spacer(Modifier.height(14.dp))

                    // Phrase ADN (toujours visible)
                    Text(
                        text = "Le socle suffit. L’approfondissement est un choix.",
                        color = WHITE_SOFT.copy(alpha = 0.55f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}