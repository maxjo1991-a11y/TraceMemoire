// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.depth.TraceDepthSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

private data class SliderDef(
    val key: String,
    val title: String
)

private val SLIDERS_FREE = listOf(
    SliderDef("humeur", "Humeur globale"),
    SliderDef("energie", "Énergie / rythme"),
    SliderDef("corps", "Corps / sensations"),
    SliderDef("presence", "Présence / attention")
)

private val SLIDERS_PREMIUM = listOf(
    SliderDef("emotion", "Architecture émotionnelle"),
    SliderDef("sommeil", "Qualité du repos vécu"),
    SliderDef("typejour", "Type de journée"),
    SliderDef("motifs", "Motifs psychiques"),
    SliderDef("environ", "Environnement"),
    SliderDef("clarte", "Clarté mentale"),
    SliderDef("charge", "Charge émotionnelle")
)

private val ROW_SPACING = 18.dp
private val SECTION_GAP = 26.dp

private val CARD_RADIUS = 22.dp
private val CARD_PADDING = 18.dp

// ✅ TEMP : force visuel Premium (remets false avant release)
private const val DEBUG_FORCE_PREMIUM_VISUALS = true

@Composable
fun TraceJourSlidersBlock(
    enabled: Boolean,
    isPremium: Boolean,
    cycleKey: String,
    seedBase: String,
    modifier: Modifier = Modifier,
    showPremiumLockedRows: Boolean = true
) {
    val premiumVisualUnlocked = isPremium || DEBUG_FORCE_PREMIUM_VISUALS

    // ✅ Une seule carte ouverte à la fois
    var openKey by remember { mutableStateOf("humeur") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {

        // ─────────────────────────────
        // ✅ GRATUIT
        // ─────────────────────────────
        SLIDERS_FREE.forEachIndexed { index, def ->
            key(def.key) {
                CollapsibleSliderCard(
                    title = def.title,
                    isOpen = openKey == def.key,
                    onToggle = { openKey = if (openKey == def.key) "" else def.key }
                ) {
                    TraceMoodSliderRow(
                        title = def.title,
                        enabled = enabled,
                        userIsPremium = isPremium,
                        isPremiumSlider = false,
                        lockedLabel = null,
                        phaseKey = cycleKey,
                        cycleKey = cycleKey,
                        seedBase = seedBase,
                        sliderKey = def.key,

                        // ✅ évite le double titre (car la carte affiche déjà le titre)
                        showTitle = false
                    )
                }
            }

            if (index != SLIDERS_FREE.lastIndex) {
                Spacer(modifier = Modifier.height(ROW_SPACING))
            }
        }

        // ─────────────────────────────
        // ✅ PREMIUM
        // ─────────────────────────────
        if (isPremium || showPremiumLockedRows) {
            Spacer(modifier = Modifier.height(SECTION_GAP))

            TraceDepthSection(
                isPremium = premiumVisualUnlocked,
                modifier = Modifier.fillMaxWidth()
            ) { contentEnabled ->

                // ✅ si debug => contenu autorisé
                val contentOk = if (premiumVisualUnlocked) true else contentEnabled

                SLIDERS_PREMIUM.forEachIndexed { index, def ->
                    key(def.key) {
                        CollapsibleSliderCard(
                            title = def.title,
                            isOpen = openKey == def.key,
                            onToggle = { openKey = if (openKey == def.key) "" else def.key }
                        ) {
                            TraceMoodSliderRow(
                                title = def.title,
                                enabled = enabled && contentOk,
                                userIsPremium = isPremium,
                                isPremiumSlider = true,
                                lockedLabel = null,
                                phaseKey = cycleKey,
                                cycleKey = cycleKey,
                                seedBase = seedBase,
                                sliderKey = def.key,

                                // ✅ évite le double titre
                                showTitle = false
                            )
                        }
                    }

                    if (index != SLIDERS_PREMIUM.lastIndex) {
                        Spacer(modifier = Modifier.height(ROW_SPACING))
                    }
                }
            }
        }
    }
}

@Composable
private fun CollapsibleSliderCard(
    title: String,
    isOpen: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Glow arrière ultra subtil
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.14f),
                            MAUVE.copy(alpha = 0.06f),
                            MAUVE.copy(alpha = 0.00f)
                        )
                    ),
                    shape = RoundedCornerShape(CARD_RADIUS)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BG_SOFT.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(CARD_RADIUS)
                )
                .border(
                    width = 1.dp,
                    color = MAUVE.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(CARD_RADIUS)
                )
                .padding(CARD_PADDING)
        ) {
            // ✅ HEADER CLIQUABLE (safe : indication=null)
            val interaction = remember { MutableInteractionSource() }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interaction,
                        indication = null
                    ) { onToggle() }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.92f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // ✅ indicateur (pastille)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOpen) TURQUOISE.copy(alpha = 0.85f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = TURQUOISE.copy(alpha = 0.55f),
                            shape = CircleShape
                        )
                )
            }

            if (isOpen) {
                Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }
}