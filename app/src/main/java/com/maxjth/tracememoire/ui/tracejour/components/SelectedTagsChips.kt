package com.maxjth.tracememoire.ui.tracejour.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectedTagsChips(
    selectedTags: Set<String>,
    selectedTagTimes: Map<String, String>,
    tagLabel: String,
    onRemoveTag: (String) -> Unit
) {
    if (selectedTags.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = BG_SOFT.copy(alpha = 0.22f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tagLabel,
                color = WHITE_SOFT.copy(alpha = 0.82f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f) // ✅ weight OK (Modifier.weight)
            )

            Text(
                text = "${selectedTags.size}",
                color = WHITE_SOFT.copy(alpha = 0.45f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            selectedTags.forEach { tag ->
                val hour = selectedTagTimes[tag]
                val label = if (!hour.isNullOrBlank()) "$hour • $tag" else tag

                AssistChip(
                    onClick = { onRemoveTag(tag) },
                    label = {
                        Text(
                            text = label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MAUVE.copy(alpha = 0.18f),
                        labelColor = WHITE_SOFT.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MAUVE.copy(alpha = 0.45f)
                    )
                )
            }
        }
    }
}