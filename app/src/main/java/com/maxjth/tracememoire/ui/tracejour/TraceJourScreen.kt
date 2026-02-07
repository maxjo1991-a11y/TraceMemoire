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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.tags.TAG_GROUPS_OFFICIAL
import com.maxjth.tracememoire.ui.tags.tierAllowed
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.TagCategoryBlock
import com.maxjth.tracememoire.ui.tracejour.components.TraceTriangleHero
import com.maxjth.tracememoire.ui.tracejour.timeline.TraceEventRow
import kotlinx.coroutines.delay

private val HERO_SIZE = 280.dp
private const val MAX_TIMELINE_EVENTS = 12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit,
    isPremium: Boolean = false,
    isPremiumPlus: Boolean = false
) {
    // ───── ÉTATS ─────
    var percent by remember { mutableStateOf(50) }
    var isInteracting by remember { mutableStateOf(false) }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var selectedTagTimes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val events by TraceEventStore.events.collectAsState()

    // ───── HEURE ─────
    val nowHour = rememberNowHour()

    // ───── REBUILD TAGS ─────
    LaunchedEffect(events) {
        val rebuilt = rebuildSelectedTagsFromEvents(events)
        selectedTags = rebuilt.tags
        selectedTagTimes = rebuilt.times
    }

    // ───── ANIMATIONS ─────
    val triEntry = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        triEntry.animateTo(
            1f,
            spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
        )
        delay(120)
    }

    // ───── UI ─────
    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = { /* vide */ },
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

                // ───── TITRE (2 lignes) + SOUS-TITRE ─────
                item(key = "hero_title") {
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Trace",
                        color = WHITE_SOFT,
                        fontSize = 67.sp,
                        lineHeight = 59.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .offset(y = (-18).dp)
                    )


                    Text(
                        text = "d’état d’âme",
                        color = WHITE_SOFT.copy(alpha = 0.94f),
                        fontSize = 59.sp,
                        lineHeight = 57.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-20).dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Où en es-tu, là, maintenant ?",
                        color = WHITE_SOFT.copy(alpha = 0.62f),
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-17).dp)
                    )

                    Spacer(Modifier.height(16.dp))
                }

                // ✅ TRIANGLE (RETOUR OFFICIEL)
                item(key = "hero_triangle") {
                    TraceTriangleHero(
                        percent = percent,
                        isInteracting = isInteracting,
                        modifier = Modifier
                            .size(HERO_SIZE)
                            .scale(0.96f + 0.04f * triEntry.value)
                    )

                    Spacer(Modifier.height(14.dp))
                }

                // ───── SLIDER (SOUS LE TRIANGLE) ─────
                item(key = "hero_slider") {
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

                    Spacer(Modifier.height(12.dp))
                }

                // ───── 3e TEXTE (AVANT LES TAGS) ─────
                item(key = "before_tags_phrase") {
                    Text(
                        text = "Le présent intérieur est noté. À travers quelques repères.",
                        color = WHITE_SOFT.copy(alpha = 0.62f),
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

                // ───── TAGS ─────
                items(TAG_GROUPS_OFFICIAL, key = { it.title }) { cat ->
                    val allowed = tierAllowed(cat.tier, isPremium, isPremiumPlus)

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

                // ───── SÉLECTION + TITRE ─────
                item(key = "selection_header") {
                    SelectionAndEventsHeader(
                        selectedTags = selectedTags,
                        selectedTagTimes = selectedTagTimes,
                        onRemoveTag = { tag ->
                            val hour = nowHour()
                            selectedTags -= tag
                            selectedTagTimes -= tag
                            TraceEventStore.recordTag("TAG_OFF|$hour|$tag", tag)
                        }
                    )
                }

                // ───── TIMELINE ─────
                val lastEvents = events.asReversed().take(MAX_TIMELINE_EVENTS)

                if (lastEvents.isEmpty()) {
                    item(key = "no_events") {
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
                    item(key = "timeline_bottom_space") { Spacer(Modifier.height(26.dp)) }
                }
            }
        }
    }
}