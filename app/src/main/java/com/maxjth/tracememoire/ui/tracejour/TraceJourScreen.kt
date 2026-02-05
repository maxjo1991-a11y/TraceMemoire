package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.tags.TAG_GROUPS_OFFICIAL
import com.maxjth.tracememoire.ui.tags.Tier
import com.maxjth.tracememoire.ui.tags.tierAllowed
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
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

    // ✅ heure collée à chaque tag sélectionné
    var selectedTagTimes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // ✅ (temp) accès premium
    val hasPremium = false
    val hasPremiumPlus = false

    // ✅ Events (source de vérité)
    val events by TraceEventStore.events.collectAsState()

    // ✅ Formatter heure (24h)
    val hourFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    fun nowHourLabel(): String = hourFormatter.format(Date(System.currentTimeMillis()))

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

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 22.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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
                        // ✅ 1 event seulement quand tu relâches
                        TraceEventStore.recordPercent(percent)
                    },
                    valueRange = 0f..100f
                )

                Spacer(Modifier.height(22.dp))

                // ─────────────────────────────
                // ✅ TAGS — + heure collée sur chaque tag sélectionné
                // + LOG PROPRE: TAG_ON|xxx / TAG_OFF|xxx
                // ─────────────────────────────
                TAG_GROUPS_OFFICIAL.forEach { cat ->
                    val allowed = tierAllowed(cat.tier, hasPremium, hasPremiumPlus)

                    TagCategoryBlock(
                        title = cat.title,
                        foundation = cat.foundation,
                        locked = !allowed,
                        tier = cat.tier,
                        tags = cat.tags,
                        selectedTags = selectedTags,
                        onToggleTag = { tag ->
                            val wasSelected = selectedTags.contains(tag)

                            if (wasSelected) {
                                // OFF
                                selectedTags = selectedTags - tag
                                selectedTagTimes = selectedTagTimes - tag
                                TraceEventStore.recordTag("TAG_OFF", tag)
                            } else {
                                // ON + heure collée
                                val hour = nowHourLabel()
                                selectedTags = selectedTags + tag
                                selectedTagTimes = selectedTagTimes + (tag to hour)
                                TraceEventStore.recordTag("TAG_ON", tag)
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                }

                // ✅ Pastilles "heure • tag"
                if (selectedTags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Sélection",
                        color = WHITE_SOFT.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(10.dp))

                    // 2 par ligne = stable
                    val ordered = selectedTags.toList()
                    ordered.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { tag ->
                                val hour = selectedTagTimes[tag] ?: "--:--"
                                val label = "$hour • $tag"

                                AssistChip(
                                    onClick = {
                                        // toggle OFF direct si tu retouches la pastille
                                        selectedTags = selectedTags - tag
                                        selectedTagTimes = selectedTagTimes - tag
                                        TraceEventStore.recordTag("TAG_OFF", tag)
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MAUVE.copy(alpha = 0.20f),
                                        labelColor = WHITE_SOFT.copy(alpha = 0.92f)
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MAUVE.copy(alpha = 0.45f)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            repeat(2 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }

                // ─────────────────────────────
                // ✅ TIMELINE (preuves)
                // ─────────────────────────────
                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Événements",
                    color = WHITE_SOFT.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(10.dp))

                val lastEvents = remember(events) { events.asReversed().take(12) }

                if (lastEvents.isEmpty()) {
                    Text(
                        text = "Aucun événement pour l’instant.",
                        color = WHITE_SOFT.copy(alpha = 0.38f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    lastEvents.forEach { e ->
                        TraceEventRow(e)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceEventRow(e: TraceEvent) {
    val label = when (e.type.name) {
        "PERCENT_UPDATE" -> "Pourcentage"
        "TAG_UPDATE" -> "Tag"
        "TIME_ADJUST" -> "Heure"
        else -> "Event"
    }

    // ✅ rendu lisible des tags: TAG_ON|Calme -> ON • Calme
    val prettyValue: String = if (e.type.name == "TAG_UPDATE") {
        val parts = e.value.split("|", limit = 2)
        if (parts.size == 2) {
            val action = parts[0]
            val tag = parts[1]
            when (action) {
                "TAG_ON" -> "ON • $tag"
                "TAG_OFF" -> "OFF • $tag"
                else -> e.value
            }
        } else e.value
    } else {
        e.value
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = BG_SOFT.copy(alpha = 0.22f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = e.hourLabel,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = label,
                color = WHITE_SOFT.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = "— $prettyValue",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TagCategoryBlock(
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
                            containerColor = if (selected) MAUVE.copy(alpha = 0.20f) else BG_SOFT.copy(alpha = 0.35f),
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