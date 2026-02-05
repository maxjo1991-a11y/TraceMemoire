package com.maxjth.tracememoire.ui.tracejour.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tags.Tier
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TagCategoryBlock(
    title: String,
    foundation: String,
    locked: Boolean,
    tier: Tier,
    tags: List<String>,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = WHITE_SOFT.copy(alpha = 0.88f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

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
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = foundation,
            color = WHITE_SOFT.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(10.dp))

        tags.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { tag ->
                    val selected = selectedTags.contains(tag)

                    AssistChip(
                        onClick = { if (!locked) onToggleTag(tag) },
                        label = {
                            Text(
                                text = tag,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        enabled = !locked,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected)
                                MAUVE.copy(alpha = 0.20f)
                            else
                                BG_SOFT.copy(alpha = 0.35f),
                            labelColor = WHITE_SOFT.copy(alpha = if (selected) 0.95f else 0.80f),
                            disabledContainerColor = BG_SOFT.copy(alpha = 0.18f),
                            disabledLabelColor = WHITE_SOFT.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = when {
                                locked -> MAUVE.copy(alpha = 0.10f)
                                selected -> MAUVE.copy(alpha = 0.55f)
                                else -> MAUVE.copy(alpha = 0.22f)
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(10.dp))
        }

        if (locked) {
            Spacer(Modifier.height(6.dp))
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

