// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tracejour.components.screen.depth.TraceDepthSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

private data class SliderDef(
    val key: String,
    val title: String,
    val isPremiumOnly: Boolean
)

private val SLIDERS_FREE: List<SliderDef> = listOf(
    SliderDef(key = "humeur",   title = "Humeur globale",        isPremiumOnly = false),
    SliderDef(key = "energie",  title = "Énergie / rythme",      isPremiumOnly = false),
    SliderDef(key = "corps",    title = "Corps / sensations",    isPremiumOnly = false),
    SliderDef(key = "presence", title = "Présence / attention",  isPremiumOnly = false)
)

private val SLIDERS_PREMIUM: List<SliderDef> = listOf(
    SliderDef(key = "typejour", title = "Type de journée",       isPremiumOnly = true),
    SliderDef(key = "motifs",   title = "Motifs psychiques",     isPremiumOnly = true),
    SliderDef(key = "environ",  title = "Environnement",         isPremiumOnly = true),
    SliderDef(key = "clarte",   title = "Clarté mentale",        isPremiumOnly = true),
    SliderDef(key = "charge",   title = "Charge émotionnelle",   isPremiumOnly = true)
)

// Spacing constants
private val ROW_SPACING = 14.dp
private val BLOCK_END_SPACING = 18.dp
private val SECTION_GAP = 16.dp

@Composable
fun TraceJourSlidersBlock(
    enabled: Boolean,
    isPremium: Boolean,
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

        // ─────────────────────────────────────────────
        // 1) GRATUIT — TOUJOURS VISIBLE (4 sliders)
        // ─────────────────────────────────────────────
        SLIDERS_FREE.forEachIndexed { index, def ->
            key(def.key) {
                val rowEnabled = enabled

                TraceMoodSliderRow(
                    title = def.title,
                    enabled = rowEnabled,
                    isPremium = false,     // ✅ gratuit
                    lockedLabel = null,
                    cycleKey = cycleKey,
                    seedBase = seedBase,
                    sliderKey = def.key
                )
            }

            Spacer(
                Modifier.height(
                    if (index != SLIDERS_FREE.lastIndex) ROW_SPACING else BLOCK_END_SPACING
                )
            )
        }

        // ─────────────────────────────────────────────
        // 2) PREMIUM — UNE SEULE SECTION PLIABLE
        // ─────────────────────────────────────────────
        if (isPremium || showPremiumLockedRows) {
            Spacer(Modifier.height(SECTION_GAP))

            TraceDepthSection(
                isPremium = isPremium,
                modifier = Modifier.fillMaxWidth()
            ) { contentEnabled ->

                SLIDERS_PREMIUM.forEachIndexed { index, def ->
                    key(def.key) {
                        val rowEnabled = enabled && contentEnabled

                        TraceMoodSliderRow(
                            title = def.title,
                            enabled = rowEnabled,
                            isPremium = true,   // ✅ sliders premium
                            lockedLabel = null, // ✅ pas de spam "premium" sous chaque slider
                            cycleKey = cycleKey,
                            seedBase = seedBase,
                            sliderKey = def.key
                        )
                    }

                    Spacer(
                        Modifier.height(
                            if (index != SLIDERS_PREMIUM.lastIndex) ROW_SPACING else BLOCK_END_SPACING
                        )
                    )
                }
            }
        }
    }
}