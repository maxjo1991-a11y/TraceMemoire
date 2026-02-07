package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.SelectedTagsChips
import com.maxjth.tracememoire.ui.tracejour.components.TraceTriangleHero
import com.maxjth.tracememoire.ui.tracejour.helpers.TagEventParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* ---------- DATA ---------- */

data class RebuiltSelection(
    val tags: Set<String>,
    val times: Map<String, String>
)

/* ---------- TIME ---------- */

@Composable
fun rememberNowHour(): () -> String {
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val state = rememberUpdatedState(formatter)
    return remember { { state.value.format(Date()) } }
}

/* ---------- TAGS ---------- */

fun rebuildSelectedTagsFromEvents(events: List<TraceEvent>): RebuiltSelection {
    val tags = linkedSetOf<String>()
    val times = linkedMapOf<String, String>()

    events.forEach { e ->
        if (e.type == TraceEventType.TAG_UPDATE) {
            TagEventParser.parse(e.value, e.hourLabel)?.let { parsed ->
                when (parsed.action) {
                    TagEventParser.TagAction.ON -> {
                        tags.add(parsed.tag)
                        times[parsed.tag] = parsed.hour
                    }
                    TagEventParser.TagAction.OFF -> {
                        tags.remove(parsed.tag)
                        times.remove(parsed.tag)
                    }
                }
            }
        }
    }

    return RebuiltSelection(tags, times)
}

/* ---------- HERO ---------- */

@Composable
fun TraceHeroSection(
    percent: Int,
    isInteracting: Boolean,
    triEntryValue: Float,
    percentEntryValue: Float,
    onPercentChange: (Int) -> Unit,
    onPercentDone: () -> Unit
) {
    Spacer(Modifier.height(24.dp))

    // ✅ Triangle officiel Écran 2
    TraceTriangleHero(
        percent = percent,
        isInteracting = isInteracting,
        modifier = Modifier
            .size(280.dp)
            .scale(0.92f + 0.08f * triEntryValue)
    )

    Spacer(Modifier.height(22.dp))

    // ✅ Petit sous-texte optionnel (pas de % en double)
    Text(
        text = "Le présent intérieur est noté.",
        style = MaterialTheme.typography.bodyMedium,
        color = WHITE_SOFT.copy(alpha = 0.70f),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.scale(percentEntryValue)
    )

    Spacer(Modifier.height(14.dp))

    Slider(
        value = percent.toFloat(),
        onValueChange = { onPercentChange(it.toInt()) },
        onValueChangeFinished = onPercentDone,
        valueRange = 0f..100f
    )

    Spacer(Modifier.height(26.dp))
}

/* ---------- HEADER ---------- */

@Composable
fun SelectionAndEventsHeader(
    selectedTags: Set<String>,
    selectedTagTimes: Map<String, String>,
    onRemoveTag: (String) -> Unit
) {
    SelectedTagsChips(
        selectedTags = selectedTags,
        selectedTagTimes = selectedTagTimes,
        tagLabel = "Sélection",
        onRemoveTag = onRemoveTag
    )

    Spacer(Modifier.height(18.dp))

    Text(
        text = "Événements",
        color = WHITE_SOFT.copy(alpha = 0.75f),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(10.dp))
}