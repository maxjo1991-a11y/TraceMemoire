// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
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

// Spacing constants (facile à tweaker sans casser)
private val ROW_SPACING = 14.dp
private val BLOCK_END_SPACING = 18.dp
private val SECTION_GAP = 16.dp

/**
 * enabled   = cycle actif (éditable) -> true ; cycle verrouillé -> false
 * isPremium = accès Premium
 * cycleKey  = "MATIN" / "JOUR" / "SOIR" / "NUIT"
 * seedBase  = clé stable (ex: "20260208") pour une vibe stable dans la journée
 *
 * showPremiumLockedRows:
 *  - true  = on affiche les sliders premium même sans premium (label "Premium", disabled)
 *  - false = on masque la section premium si l’utilisateur n’est pas premium
 */
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
        // 1) GRATUIT
        // ─────────────────────────────────────────────
        SLIDERS_FREE.forEachIndexed { index, def ->
            key(def.key) {
                val rowEnabled = enabled // gratuit = toujours allowed
                TraceMoodSliderRow(
                    title = def.title,
                    enabled = rowEnabled,
                    lockedLabel = if (!enabled) "Verrouillé" else null,
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
        // 2) PREMIUM (optionnel)
        // ─────────────────────────────────────────────
        if (isPremium || showPremiumLockedRows) {

            Spacer(Modifier.height(SECTION_GAP))

            PremiumDividerChip(
                text = if (isPremium) "Section Premium" else "Section Premium (verrouillée)"
            )

            Spacer(Modifier.height(SECTION_GAP))

            SLIDERS_PREMIUM.forEachIndexed { index, def ->
                key(def.key) {
                    val allowed = isPremium
                    val rowEnabled = enabled && allowed

                    TraceMoodSliderRow(
                        title = def.title,
                        enabled = rowEnabled,
                        lockedLabel = when {
                            !enabled -> "Verrouillé"
                            !allowed -> "Premium"
                            else -> null
                        },
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

@Composable
private fun PremiumDividerChip(
    text: String
) {
    // petite pastille “séparateur” calme, dans ton ADN
    Text(
        text = text,
        color = WHITE_SOFT.copy(alpha = 0.78f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BG_SOFT.copy(alpha = 0.22f))
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.22f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}