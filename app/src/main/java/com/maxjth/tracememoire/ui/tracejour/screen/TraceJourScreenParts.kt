// BLOC — TraceJourScreenParts.kt (COMPLET) ✅ SANS TraceJourTitleBlock
// Chemin: ui/tracejour/screen/TraceJourScreenParts.kt
package com.maxjth.tracememoire.ui.tracejour.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.tags.TagCategoryBlock
import com.maxjth.tracememoire.ui.tags.Tier
import com.maxjth.tracememoire.ui.tracejour.components.hero.TraceTriangleHeroV1
import com.maxjth.tracememoire.ui.tracejour.timeline.TraceEventRow

@Composable
fun TraceJourHeroBlock(
    percent: Int,
    subtitleTint: Color,
    heroEntry: Float,
    onPercentPreview: (Int) -> Unit,
    onPercentCommit: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = 0.92f + 0.08f * heroEntry
                scaleX = 0.985f + 0.015f * heroEntry
                scaleY = 0.985f + 0.015f * heroEntry
            },
        contentAlignment = Alignment.Center
    ) {
        TraceTriangleHeroV1(
            valuePercent = percent,
            subtitleTint = subtitleTint,
            onValueChange = onPercentPreview,
            onValueCommit = onPercentCommit
        )
    }

    Spacer(Modifier.height(18.dp))
}

@Composable
fun TraceJourBeforeTagsPhrase(subtitleTint: Color) {
    Text(
        text = "Le présent intérieur est noté. À travers quelques repères.",
        color = subtitleTint,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
    Spacer(Modifier.height(18.dp))
}

@Composable
fun TraceJourTagBlock(
    catTitle: String,
    catFoundation: String,
    catTier: Tier,
    tags: List<String>,
    allowed: Boolean,
    selectedTags: Set<String>,
    subtitleTint: Color, // (gardé même si pas utilisé ici, au cas où tu l’emploies après)
    onToggleTag: (String) -> Unit
) {
    TagCategoryBlock(
        title = catTitle,
        foundation = catFoundation,
        locked = !allowed,
        tier = catTier,
        tags = tags,
        selectedTags = selectedTags,
        isOpen = allowed,
        onToggleOpen = {},
        onToggleTag = onToggleTag
    )
    Spacer(Modifier.height(16.dp))
}

@Composable
fun TraceJourTimelineBlock(events: List<TraceEvent>) {
    if (events.isEmpty()) {
        Text(
            text = "Aucun événement pour l’instant.",
            color = Color.White.copy(alpha = 0.38f)
        )
        Spacer(Modifier.height(24.dp))
        return
    }

    events.forEach { e ->
        TraceEventRow(e)
        Spacer(Modifier.height(8.dp))
    }
    Spacer(Modifier.height(26.dp))
}