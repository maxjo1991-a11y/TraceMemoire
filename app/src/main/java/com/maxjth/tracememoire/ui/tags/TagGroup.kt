package com.maxjth.tracememoire.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow

// Tu peux brancher ça sur tes vraies structures ensuite
data class SimpleTagCategory(
    val title: String,
    val tags: List<String>,
    val premium: Boolean = false
)

@Composable
fun TagGroup(
    category: SimpleTagCategory,
    selectedTags: Set<String>,
    enabled: Boolean,
    mauve: Color,
    textColor: Color,
    cardBg: Color,
    onToggleTag: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
            .clickable { expanded = !expanded }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.title,
                color = textColor.copy(alpha = 0.95f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.weight(1f))

            if (category.premium) {
                Text(
                    text = "Premium",
                    color = mauve.copy(alpha = 0.90f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(10.dp))
            }

            // ✅ Flèche MAUVE (comme tu veux)
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = mauve.copy(alpha = 0.95f)
            )
        }

        if (expanded) {
            Spacer(Modifier.height(14.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                category.tags.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    TagChip(
                        text = tag,
                        selected = isSelected,
                        enabled = enabled,
                        mauve = mauve,
                        textColor = textColor,
                        onClick = { onToggleTag(tag) }
                    )
                }
            }
        }
    }
}