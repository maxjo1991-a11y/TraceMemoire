package com.maxjth.tracememoire.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WHITE_SOFT = Color(0xFFE9E9E9)
private val TURQUOISE  = Color(0xFF00A3A3)
private val MAUVE      = Color(0xFF7A63C6)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagGroupsAccordion(
    groups: List<TagCategory>,
    selectedTags: Set<String>,
    enabled: Boolean,
    hasPremium: Boolean,
    hasPremiumPlus: Boolean,
    onToggleTag: (String) -> Unit
) {
    var openIndex by remember { mutableStateOf<Int?>(null) }

    val firstPremiumIndex = remember(groups) {
        groups.indexOfFirst { it.tier == Tier.PREMIUM }
            .let { if (it == -1) Int.MAX_VALUE else it }
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        groups.forEachIndexed { index, category ->

            // ─── Séparateur Premium ─────────────────────────────
            if (index == firstPremiumIndex) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Premium",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MAUVE.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "profondeur spatiale",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = WHITE_SOFT.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            val allowed = tierAllowed(category.tier, hasPremium, hasPremiumPlus)
            val isOpen = openIndex == index
            val count = category.tags.count { it in selectedTags }
            val title = if (count > 0) "${category.title} ($count)" else category.title

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {

                // ─── Header ──────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openIndex = if (isOpen) null else index }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (count > 0) MAUVE else WHITE_SOFT
                    )

                    if (!allowed) {
                        Text(
                            text = "Premium",
                            fontSize = 13.sp,
                            color = MAUVE.copy(alpha = 0.75f)
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Icon(
                        imageVector = if (isOpen)
                            Icons.Outlined.KeyboardArrowUp
                        else
                            Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TURQUOISE.copy(alpha = if (isOpen) 0.9f else 0.45f)
                    )
                }

                // ─── Contenu ─────────────────────────────────────
                if (isOpen) {

                    Text(
                        text = category.foundation,
                        fontSize = 12.5.sp,
                        fontStyle = FontStyle.Italic,
                        color = WHITE_SOFT.copy(alpha = 0.55f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    if (!allowed) {

                        Text(
                            text = "Débloqué avec Premium",
                            fontSize = 13.sp,
                            color = WHITE_SOFT.copy(alpha = 0.45f),
                            modifier = Modifier.padding(16.dp)
                        )

                    } else {

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            category.tags.forEach { tag ->
                                TagChip(
                                    label = tag,
                                    isSelected = tag in selectedTags,
                                    onClick = {
                                        if (enabled) {
                                            onToggleTag(tag)
                                        }
                                    }
                                )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
        }
    }}