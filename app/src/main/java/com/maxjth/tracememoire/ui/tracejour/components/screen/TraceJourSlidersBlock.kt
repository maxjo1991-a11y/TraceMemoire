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
import androidx.compose.foundation.layout.heightIn
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

private val CARD_RADIUS = 24.dp

// ✅ Carte plus “touch-friendly”
private val CARD_MIN_HEIGHT = 86.dp
private val CARD_PADDING = 20.dp

// ✅ Rend la section plus large (moins de marge sur les côtés)
private val OUTER_HORIZONTAL_PADDING = 14.dp

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
            // ✅ plus large (avant: 24.dp)
            .padding(horizontal = OUTER_HORIZONTAL_PADDING)
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

                        // ✅ IMPORTANT : phaseKey DOIT être matin/jour/soir/nuit
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

                                // ✅ IMPORTANT : phaseKey DOIT être matin/jour/soir/nuit
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
    val shape = RoundedCornerShape(CARD_RADIUS)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Glow “côtés” + halo arrière (plus visible qu’avant)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.22f),
                            MAUVE.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = shape
                )
        )

        // ✅ Glow latéral subtil (donne l’impression que les côtés illuminent)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = 0.10f),
                            Color.Transparent,
                            TURQUOISE.copy(alpha = 0.10f)
                        )
                    ),
                    shape = shape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                // ✅ carte un peu plus haute (meilleur pour gros doigts)
                .heightIn(min = CARD_MIN_HEIGHT)
                // ✅ Fond moins pâle
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BG_SOFT.copy(alpha = 0.26f),
                            BG_SOFT.copy(alpha = 0.18f)
                        )
                    ),
                    shape = shape
                )
                // ✅ Bordure plus visible (1ère bordure)
                .border(
                    width = 1.5.dp,
                    color = MAUVE.copy(alpha = 0.28f),
                    shape = shape
                )
                // ✅ Bordure intérieure très subtile (effet “double ligne”)
                .padding(1.dp)
                .border(
                    width = 1.dp,
                    color = TURQUOISE.copy(alpha = 0.12f),
                    shape = shape
                )
                .padding(CARD_PADDING)
        ) {
            val interaction = remember { MutableInteractionSource() }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interaction,
                        indication = null
                    ) { onToggle() }
                    // ✅ header plus haut (hitbox + confort)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.94f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // ✅ indicateur plus visible
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (isOpen) TURQUOISE.copy(alpha = 0.90f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = TURQUOISE.copy(alpha = 0.65f),
                            shape = CircleShape
                        )
                )
            }

            if (isOpen) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}