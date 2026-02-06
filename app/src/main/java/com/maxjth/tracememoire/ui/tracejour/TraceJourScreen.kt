package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing
import com.maxjth.tracememoire.ui.tags.TAG_GROUPS_OFFICIAL
import com.maxjth.tracememoire.ui.tags.tierAllowed
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.SelectedTagsChips
import com.maxjth.tracememoire.ui.tracejour.components.TagCategoryBlock
import com.maxjth.tracememoire.ui.tracejour.helpers.TagEventParser
import com.maxjth.tracememoire.ui.tracejour.timeline.TraceEventRow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(onBack: () -> Unit) {

    // ───────── ÉTATS ─────────
    var percent by remember { mutableStateOf(50) }
    var isInteracting by remember { mutableStateOf(false) }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var selectedTagTimes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val events by TraceEventStore.events.collectAsState()

    // ───────── FORMAT HEURE ─────────
    val hourFormatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }
    fun nowHour(): String = hourFormatter.format(Date())

    // ───────── REBUILD TAGS DEPUIS EVENTS ─────────
    LaunchedEffect(events) {
        val tags = linkedSetOf<String>()
        val times = linkedMapOf<String, String>()

        events.forEach { e ->
            if (e.type.name == "TAG_UPDATE") {
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

        selectedTags = tags
        selectedTagTimes = times
    }

    // ───────── ANIMATIONS ─────────
    val triEntry = remember { Animatable(0f) }
    val percentEntry = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        triEntry.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow))
        delay(160)
        percentEntry.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow))
    }

    // ───────── UI ─────────
    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = { Text("Trace du jour") },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Text("Retour", color = TURQUOISE, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bg, bgSoft, bg)))
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ───────── HERO ─────────
                item {
                    Spacer(Modifier.height(24.dp))

                    TriangleOutlineBreathing(
                        percent = percent,
                        isInteracting = isInteracting,
                        modifier = Modifier
                            .size(260.dp)
                            .scale(0.92f + 0.08f * triEntry.value)
                    )

                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = WHITE_SOFT,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.scale(percentEntry.value)
                    )

                    Spacer(Modifier.height(14.dp))

                    Slider(
                        value = percent.toFloat(),
                        onValueChange = {
                            percent = it.toInt()
                            isInteracting = true
                        },
                        onValueChangeFinished = {
                            isInteracting = false
                            TraceEventStore.recordPercent(percent)
                        },
                        valueRange = 0f..100f
                    )

                    Spacer(Modifier.height(26.dp))
                }

                // ───────── TAGS ─────────
                items(TAG_GROUPS_OFFICIAL, key = { it.title }) { cat ->
                    val allowed = tierAllowed(cat.tier, false, false)

                    TagCategoryBlock(
                        title = cat.title,
                        foundation = cat.foundation,
                        locked = !allowed,
                        tier = cat.tier,
                        tags = cat.tags,
                        selectedTags = selectedTags,
                        isOpen = allowed,
                        onToggleOpen = {},
                        onToggleTag = { tag ->
                            val hour = nowHour()

                            if (selectedTags.contains(tag)) {
                                selectedTags -= tag
                                selectedTagTimes -= tag
                                TraceEventStore.recordTag("TAG_OFF|$hour|$tag", tag)
                            } else {
                                selectedTags += tag
                                selectedTagTimes += tag to hour
                                TraceEventStore.recordTag("TAG_ON|$hour|$tag", tag)
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                }

                // ───────── SÉLECTION ─────────
                item {
                    SelectedTagsChips(
                        selectedTags = selectedTags,
                        selectedTagTimes = selectedTagTimes,
                        tagLabel = "Sélection",
                        onRemoveTag = { tag ->
                            val hour = nowHour()
                            selectedTags -= tag
                            selectedTagTimes -= tag
                            TraceEventStore.recordTag("TAG_OFF|$hour|$tag", tag)
                        }
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

                // ───────── TIMELINE ─────────
                val lastEvents = events.asReversed().take(12)

                if (lastEvents.isEmpty()) {
                    item {
                        Text(
                            text = "Aucun événement pour l’instant.",
                            color = WHITE_SOFT.copy(alpha = 0.38f)
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                } else {
                    items(lastEvents, key = { it.id }) { e ->
                        TraceEventRow(e)
                        Spacer(Modifier.height(8.dp))
                    }

                    item { Spacer(Modifier.height(26.dp)) }
                }
            }
        }
    }
}