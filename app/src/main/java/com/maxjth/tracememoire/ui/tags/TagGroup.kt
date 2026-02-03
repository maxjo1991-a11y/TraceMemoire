package com.maxjth.tracememoire.ui.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagGroup(
    tags: List<String>,
    selectedTags: Set<String>,
    enabled: Boolean = true,
    onTagToggle: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            TagChip(
                label = tag,
                isSelected = selectedTags.contains(tag),
                onClick = { if (enabled) onTagToggle(tag) }
            )
        }
    }
}