package com.maxjth.tracememoire.ui.tracejour.components.screen
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tags.TAG_GROUPS_OFFICIAL
import com.maxjth.tracememoire.ui.tags.tierAllowed
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.store.TraceEventStore
import com.maxjth.tracememoire.ui.tracejour.helpers.rebuildSelectedTagsFromEvents
import com.maxjth.tracememoire.ui.tracejour.helpers.rememberNowHour
import kotlinx.coroutines.delay

private const val MAX_TIMELINE_EVENTS = 12

// ✅ IMPORTANT : pour matcher Écran 1, idéalement on met cette couleur dans ui/theme/Color.kt
// et on l’importe ici (ex: SUBTITLE_TINT = WHITE_SOFT.copy(alpha = 0.55f))
// Pour l’instant je laisse ta valeur, mais on va la remplacer par celle de l’Écran 1 ensuite.
private val SUBTITLE_TINT = Color(0xFFAFAFAF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit,
    isPremium: Boolean = false,
    isPremiumPlus: Boolean = false
) {
    // ───── ÉTATS ─────
    var percent by remember { mutableStateOf(60) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var selectedTagTimes by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val events by TraceEventStore.events.collectAsState()
    val nowHour = rememberNowHour()

    // rebuild tags depuis events
    LaunchedEffect(events) {
        val rebuilt = rebuildSelectedTagsFromEvents(events)
        selectedTags = rebuilt.tags
        selectedTagTimes = rebuilt.times
    }

    // petite entrée du hero
    val heroEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        heroEntry.animateTo(
            1f,
            spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow)
        )
        delay(120)
    }

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

                item(key = "hero_title") {
                    TraceJourTitleBlock(subtitleTint = SUBTITLE_TINT)
                }

                item(key = "hero_triangle") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = 0.92f + 0.08f * heroEntry.value
                                scaleX = 0.985f + 0.015f * heroEntry.value
                                scaleY = 0.985f + 0.015f * heroEntry.value
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        TraceJourHeroBlock(
                            percent = percent,
                            subtitleTint = SUBTITLE_TINT,
                            heroEntry = heroEntry.value,
                            onPercentPreview = { percent = it },
                            onPercentCommit = {
                                percent = it
                                TraceEventStore.recordPercent(it)
                            }
                        )
                    }
                }

                item(key = "phrase") {
                    TraceJourBeforeTagsPhrase(subtitleTint = SUBTITLE_TINT)
                }

                items(TAG_GROUPS_OFFICIAL, key = { it.title }) { cat ->
                    val allowed = tierAllowed(cat.tier, isPremium, isPremiumPlus)

                    TraceJourTagBlock(
                        catTitle = cat.title,
                        catFoundation = cat.foundation,
                        catTier = cat.tier,
                        tags = cat.tags,
                        allowed = allowed,
                        selectedTags = selectedTags,
                        subtitleTint = SUBTITLE_TINT,
                        onToggleTag = { tag ->
                            val hour = nowHour()

                            if (selectedTags.contains(tag)) {
                                selectedTags = selectedTags - tag
                                selectedTagTimes = selectedTagTimes - tag
                                TraceEventStore.recordTag("TAG_OFF|$hour|$tag", tag)
                            } else {
                                selectedTags = selectedTags + tag
                                selectedTagTimes = selectedTagTimes + (tag to hour)
                                TraceEventStore.recordTag("TAG_ON|$hour|$tag", tag)
                            }
                        }
                    )
                }

                item(key = "timeline") {
                    val lastEvents = events.asReversed().take(MAX_TIMELINE_EVENTS)
                    TraceJourTimelineBlock(events = lastEvents)
                }

                item(key = "bottom_space") { Spacer(Modifier.height(18.dp)) }
            }
        }
    }
}