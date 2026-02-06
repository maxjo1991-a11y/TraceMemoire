package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(onBack: () -> Unit) {

    var percent by remember { mutableStateOf(50) }
    var isInteracting by remember { mutableStateOf(false) }

    // ✅ sélection tags
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    // ✅ heure collée (tag -> "HH:mm") pour l'UI
    var selectedTagTimes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // ✅ (temp) accès premium
    val hasPremium = false
    val hasPremiumPlus = false

    // ✅ Events (source de vérité)
    val events by TraceEventStore.events.collectAsState()

    // ✅ Formatter heure (24h)
    val hourFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    fun nowHourLabel(): String = hourFormatter.format(Date(System.currentTimeMillis()))

    // ✅ Reconstruire tags/heures depuis events
    LaunchedEffect(events) {
        val rebuiltTags = linkedSetOf<String>()
        val rebuiltTimes = linkedMapOf<String, String>()

        events.forEach { e ->
            if (e.type.name == "TAG_UPDATE") {
                val parsed = TagEventParser.parse(
                    raw = e.value,
                    fallbackHour = e.hourLabel
                )

                if (parsed != null) {
                    when (parsed.action) {
                        TagEventParser.TagAction.ON -> {
                            rebuiltTags.add(parsed.tag)
                            rebuiltTimes[parsed.tag] = parsed.hour
                        }
                        TagEventParser.TagAction.OFF -> {
                            rebuiltTags.remove(parsed.tag)
                            rebuiltTimes.remove(parsed.tag)
                        }
                    }
                }
            }
        }

        selectedTags = rebuiltTags.toSet()
        selectedTagTimes = rebuiltTimes.toMap()
    }

    // ✅ Entrée triangle
    val triEntry = remember { Animatable(0f) }
    // ✅ Entrée %
    val entryScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        triEntry.snapTo(0f)
        triEntry.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.70f, stiffness = Spring.StiffnessLow)
        )

        delay(180)

        entryScale.snapTo(0f)
        entryScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow)
        )
    }

    // ✅ état d’ouverture des catégories (par titre)
    val openMap = remember { mutableStateMapOf<String, Boolean>() }
    fun isOpenFor(title: String): Boolean = openMap[title] ?: true
    fun setOpenFor(title: String, value: Boolean) { openMap[title] = value }

    val bgDeep = BG_SOFT
    val bgSlight = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            TopAppBar(
                title = { Text("Trace du jour") },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Text(
                            text = "Retour",
                            color = TURQUOISE,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgDeep)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgDeep, bgSlight, bgDeep)))
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
                item(key = "hero_triangle") {
                    Spacer(Modifier.height(22.dp))

                    TriangleOutlineBreathing(
                        percent = percent,
                        isInteracting = isInteracting,
                        modifier = Modifier
                            .size(260.dp)
                            .scale(0.92f + (0.08f * triEntry.value))
                    )

                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = WHITE_SOFT,
                        modifier = Modifier.scale(entryScale.value),
                        fontWeight = FontWeight.ExtraBold
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

                    Spacer(Modifier.height(22.dp))
                }

                // ───────── TAGS ─────────
                items(
                    items = TAG_GROUPS_OFFICIAL,
                    key = { it.title }
                ) { cat ->

                    val allowed = tierAllowed(cat.tier, hasPremium, hasPremiumPlus)

                    // ✅ règle : si pas autorisé -> reste fermé
                    val forcedClosed = !allowed
                    val open = if (forcedClosed) false else isOpenFor(cat.title)

                    TagCategoryBlock(
                        title = cat.title,
                        foundation = cat.foundation,
                        locked = !allowed,
                        tier = cat.tier,
                        tags = cat.tags,
                        selectedTags = selectedTags,
                        isOpen = open,
                        onToggleOpen = {
                            if (allowed) setOpenFor(cat.title, !open)
                        },
                        onToggleTag = { tag ->
                            val wasSelected = selectedTags.contains(tag)

                            if (wasSelected) {
                                val hour = nowHourLabel()
                                selectedTags = selectedTags - tag
                                selectedTagTimes = selectedTagTimes - tag

                                TraceEventStore.recordTag(
                                    action = "TAG_OFF|$hour|$tag",
                                    tagLabel = tag
                                )
                            } else {
                                val hour = nowHourLabel()
                                selectedTags = selectedTags + tag
                                selectedTagTimes = selectedTagTimes + (tag to hour)

                                TraceEventStore.recordTag(
                                    action = "TAG_ON|$hour|$tag",
                                    tagLabel = tag
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                }

                // ───────── SÉLECTION ─────────
                item(key = "selected_chips") {
                    SelectedTagsChips(
                        selectedTags = selectedTags,
                        selectedTagTimes = selectedTagTimes,
                        tagLabel = "Sélection",
                        onRemoveTag = { tag ->
                            val offHour = nowHourLabel()
                            selectedTags = selectedTags - tag
                            selectedTagTimes = selectedTagTimes - tag

                            TraceEventStore.recordTag(
                                action = "TAG_OFF|$offHour|$tag",
                                tagLabel = tag
                            )
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Événements",
                        color = WHITE_SOFT.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(10.dp))
                }

                // ───────── TIMELINE ─────────
                val lastEvents = events.asReversed().take(12)

                if (lastEvents.isEmpty()) {
                    item(key = "no_events") {
                        Text(
                            text = "Aucun événement pour l’instant.",
                            color = WHITE_SOFT.copy(alpha = 0.38f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(22.dp))
                    }
                } else {
                    items(
                        items = lastEvents,
                        key = { it.id }
                    ) { e ->
                        TraceEventRow(e)
                        Spacer(Modifier.height(8.dp))
                    }

                    item(key = "timeline_bottom_space") {
                        Spacer(Modifier.height(22.dp))
                    }
                }
            }
        }
    }
}