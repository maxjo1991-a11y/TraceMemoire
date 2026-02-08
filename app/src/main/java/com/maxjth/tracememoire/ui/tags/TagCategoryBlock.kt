package com.maxjth.tracememoire.ui.tags

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TagCategoryBlock(
    title: String,
    foundation: String,
    locked: Boolean,
    tier: Tier,
    tags: List<String>,
    selectedTags: Set<String>,
    isOpen: Boolean,
    onToggleOpen: () -> Unit,
    onToggleTag: (String) -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    val cardBg = BG_SOFT.copy(alpha = 0.18f)

    val borderC = when {
        locked -> MAUVE.copy(alpha = 0.14f)
        isOpen -> TURQUOISE.copy(alpha = 0.22f)
        else -> MAUVE.copy(alpha = 0.18f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .border(width = 1.dp, color = borderC, shape = cardShape)
            .background(color = cardBg, shape = cardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {

        // ───────────────────────
        // Header (Titre + badge + toggle)
        // ───────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = foundation,
                    color = WHITE_SOFT.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isOpen) 10 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(1.dp))

            if (locked) {
                Text(
                    text = when (tier) {
                        Tier.PREMIUM -> "Premium"
                        Tier.PREMIUM_PLUS -> "Premium+"
                        else -> ""
                    },
                    color = MAUVE.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                TextButton(onClick = onToggleOpen) {
                    Text(
                        text = if (isOpen) "Fermer" else "Ouvrir",
                        color = TURQUOISE,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ───────────────────────
        // Corps (chips) si ouvert
        // ───────────────────────
        if (isOpen) {
            Spacer(Modifier.height(12.dp))

            tags.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { tag ->
                        val selected = selectedTags.contains(tag)

                        AssistChip(
                            onClick = { if (!locked) onToggleTag(tag) },
                            enabled = !locked,
                            label = {
                                Text(
                                    text = tag,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when {
                                    selected -> MAUVE.copy(alpha = 0.22f)
                                    else -> BG_SOFT.copy(alpha = 0.32f)
                                },
                                labelColor = WHITE_SOFT.copy(alpha = if (selected) 0.95f else 0.80f),
                                disabledContainerColor = BG_SOFT.copy(alpha = 0.18f),
                                disabledLabelColor = WHITE_SOFT.copy(alpha = 0.25f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = when {
                                    locked -> MAUVE.copy(alpha = 0.12f)
                                    selected -> MAUVE.copy(alpha = 0.55f)
                                    else -> MAUVE.copy(alpha = 0.22f)
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                }

                Spacer(Modifier.height(10.dp))
            }
        }

        // ───────────────────────
        // Bloc verrouillé (si Premium)
        // ───────────────────────
        if (locked) {
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MAUVE.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(
                        color = BG_SOFT.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Déverrouiller avec Premium.",
                    color = WHITE_SOFT.copy(alpha = 0.35f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}