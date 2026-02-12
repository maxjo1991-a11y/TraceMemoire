// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.tracejour.components.screen.depth.TraceDepthSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

private data class SliderDef(
    val key: String,
    val title: String,
    val isPremiumOnly: Boolean
)

private val SLIDERS_FREE = listOf(
    SliderDef("humeur", "Humeur globale", false),
    SliderDef("energie", "Énergie / rythme", false),
    SliderDef("corps", "Corps / sensations", false),
    SliderDef("presence", "Présence / attention", false)
)

private val SLIDERS_PREMIUM = listOf(
    SliderDef("typejour", "Type de journée", true),
    SliderDef("motifs", "Motifs psychiques", true),
    SliderDef("environ", "Environnement", true),
    SliderDef("clarte", "Clarté mentale", true),
    SliderDef("charge", "Charge émotionnelle", true)
)

private val ROW_SPACING = 18.dp
private val SECTION_GAP = 26.dp

private val CARD_RADIUS = 22.dp
private val CARD_PADDING = 18.dp

@Composable
fun TraceJourSlidersBlock(
    enabled: Boolean,
    isPremium: Boolean,          // ✅ premium réel de l'utilisateur
    cycleKey: String,
    seedBase: String,
    modifier: Modifier = Modifier,
    showPremiumLockedRows: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {

        // ✅ GRATUIT
        SLIDERS_FREE.forEachIndexed { index, def ->
            key(def.key) {
                TraceSliderCard {
                    TraceMoodSliderRow(
                        title = def.title,
                        enabled = enabled,

                        // ✅ NOUVEAU API
                        userIsPremium = isPremium,
                        isPremiumSlider = false,

                        lockedLabel = null,
                        cycleKey = cycleKey,
                        seedBase = seedBase,
                        sliderKey = def.key
                    )
                }
            }

            if (index != SLIDERS_FREE.lastIndex) {
                Spacer(modifier = Modifier.height(ROW_SPACING))
            }
        }

        // ✅ PREMIUM SECTION
        if (isPremium || showPremiumLockedRows) {

            Spacer(modifier = Modifier.height(SECTION_GAP))

            TraceDepthSection(
                isPremium = isPremium,
                modifier = Modifier.fillMaxWidth()
            ) { contentEnabled ->

                SLIDERS_PREMIUM.forEachIndexed { index, def ->
                    key(def.key) {
                        TraceSliderCard {
                            TraceMoodSliderRow(
                                title = def.title,
                                enabled = enabled && contentEnabled,

                                // ✅ NOUVEAU API
                                userIsPremium = isPremium,
                                isPremiumSlider = true,

                                lockedLabel = null,
                                cycleKey = cycleKey,
                                seedBase = seedBase,
                                sliderKey = def.key
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
private fun TraceSliderCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // ✅ GLOW MAUVE ULTRA SUBTIL (arrière)
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

        // ✅ CARTE
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
            content()
        }
    }
}