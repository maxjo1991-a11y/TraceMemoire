// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/notes/TraceNoteBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.notes

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

/**
 * TraceNoteBlock — NOTE PLIABLE (compact) + STYLE "BLOC-NOTES"
 * - Fermée par défaut (gain de place)
 * - Tap sur l’en-tête => ouvre / referme
 * - Indicateur si texte présent (point + “Ajouté”)
 * - FREE = 200, PREMIUM = 589
 * - Visuel différent du reste : “papier sombre” + bordure mauve douce
 */
@Composable
fun TraceNoteBlock(
    note: String,
    onNoteChange: (String) -> Unit,
    enabled: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,

    // Tier / limites
    userIsPremium: Boolean = false,
    maxCharsFree: Int = 200,
    maxCharsPremium: Int = 589,

    // Texte UI
    title: String = "Trace écrite",
    placeholder: String = "Laisser une empreinte",
    lockedLabel: String = "Espace personnel"
) {
    // ✅ Max chars blindé (évite toute valeur bizarre)
    val freeCap = maxCharsFree.coerceAtLeast(1)
    val premiumCap = maxCharsPremium.coerceAtLeast(freeCap)
    val maxChars = remember(userIsPremium, freeCap, premiumCap) {
        if (userIsPremium) premiumCap else freeCap
    }

    val safeNote = remember(note, maxChars) {
        if (note.length > maxChars) note.take(maxChars) else note
    }
    val hasContent = safeNote.isNotBlank()

    // ✅ Pliable: fermée par défaut
    var isOpen by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()

    val shapeOuter = RoundedCornerShape(18.dp)
    val shapeInner = RoundedCornerShape(16.dp)

    // ✅ Accent note : un peu plus "mauve / sobre" que le slider
    // (on garde ton accent, mais on le calme + on le tire vers un ton plus “encre”)
    val noteAccent = remember(accent) {
        lerp(accent, Color(0xFF8A5CFF), 0.22f) // léger mauve
    }

    // ✅ Fond “bloc-notes” (différent de tes autres pastilles)
    val paperBrush = remember(noteAccent) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.18f),
                Color.Black.copy(alpha = 0.10f),
                Color.Black.copy(alpha = 0.16f)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapeOuter)
            .background(BG_SOFT.copy(alpha = 0.06f))
            .border(1.dp, noteAccent.copy(alpha = 0.20f), shapeOuter)
            .padding(12.dp)
    ) {
        val interaction = remember { MutableInteractionSource() }

        // ── HEADER (compact, classy, pas “pastille dans pastille”)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    enabled = enabled,
                    interactionSource = interaction,
                    indication = null
                ) { isOpen = !isOpen }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    tint = noteAccent.copy(alpha = 0.90f)
                )
                Spacer(Modifier.padding(start = 8.dp))

                Column {
                    Text(
                        text = title,
                        color = WHITE_SOFT.copy(alpha = 0.94f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // ✅ Une seule ligne d’état (simple)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasContent) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(noteAccent.copy(alpha = 0.95f))
                            )
                            Spacer(Modifier.padding(start = 6.dp))
                            Text(
                                text = "Ajouté",
                                color = WHITE_SOFT.copy(alpha = 0.70f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Ajouter",
                                color = WHITE_SOFT.copy(alpha = 0.55f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ✅ Compteur toujours visible mais discret
            Text(
                text = "${safeNote.length}/$maxChars",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // ── CONTENU (seulement si ouvert)
        if (isOpen) {
            Spacer(Modifier.height(10.dp))

            // “Bloc-notes” : grand champ + padding plus élégant
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 116.dp)
                    .height(if (userIsPremium) 176.dp else 156.dp)
                    .clip(shapeInner)
                    .background(paperBrush)
                    .border(
                        width = if (userIsPremium) 1.4.dp else 1.dp,
                        color = noteAccent.copy(alpha = 0.18f),
                        shape = shapeInner
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = safeNote,
                    onValueChange = { raw ->
                        if (!enabled) return@BasicTextField
                        val trimmed = if (raw.length > maxChars) raw.take(maxChars) else raw
                        onNoteChange(trimmed)
                    },
                    enabled = enabled,
                    textStyle = TextStyle(
                        color = WHITE_SOFT.copy(alpha = 0.92f),
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll),
                    decorationBox = { inner ->
                        if (safeNote.isBlank()) {
                            Text(
                                text = placeholder,
                                color = WHITE_SOFT.copy(alpha = 0.28f),
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── LIGNE BAS (sobre)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fermer",
                    color = noteAccent.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            enabled = enabled,
                            interactionSource = interaction,
                            indication = null
                        ) { isOpen = false }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                // ✅ En gratuit : hint premium discret, pas une grosse phrase lourde
                if (!userIsPremium) {
                    Text(
                        text = lockedLabel,
                        color = WHITE_SOFT.copy(alpha = 0.40f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}