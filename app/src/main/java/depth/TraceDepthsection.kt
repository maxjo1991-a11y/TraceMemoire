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
        // HEADER — Approfondissement · Premium
        // ✅ FIX: SpaceBetween + ellipsis (plus de texte "vertical")
        // ─────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = indication
                ) { opened = !opened }
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            BG_SOFT.copy(alpha = 0.22f),
                            BG_SOFT.copy(alpha = 0.28f),
                            BG_SOFT.copy(alpha = 0.22f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = MAUVE.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Approfondissement",
                color = MAUVE.copy(alpha = 0.95f),
                fontSize = 13.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = true)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = if (isPremium) "Premium" else "Premium verrouillée",
                color = TURQUOISE.copy(alpha = if (isPremium) 0.85f else 0.55f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ─────────────────────────────
        // CONTENU (ouvert / fermé)
        // ─────────────────────────────
        AnimatedVisibility(
            visible = opened,
            enter = expandVertically(animationSpec = tween(260, easing = FastOutSlowInEasing)),
            exit = shrinkVertically(animationSpec = tween(220, easing = FastOutSlowInEasing))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BG_SOFT.copy(alpha = 0.26f))
                    .border(
                        width = 1.dp,
                        color = MAUVE.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
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