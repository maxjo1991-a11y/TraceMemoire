package com.maxjth.tracememoire.ui.history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.history.components.DayHeader
import com.maxjth.tracememoire.ui.history.components.HistoryEventCard
import com.maxjth.tracememoire.ui.history.logic.buildGroupedHistory
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.model.TraceEvent
/**
 * Écran 3 (Historique) — version stable / neutre:
 * - Pas de Store
 * - Pas de collecte Flow
 * - UI intacte (calme)
 * - Données vides par défaut (placeholder)
 *
 * Plus tard, on reconnectera les vraies traces.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    // ✅ Données temporaires: vide => placeholder s’affiche
    val events = remember { emptyList<TraceEvent>() }

    var onlyChanges by remember { mutableStateOf(false) }

    val bgDeep = BG_DEEP
    val bgSlight = BG_DEEP.copy(alpha = 0.92f)

    val grouped = remember(events, onlyChanges) {
        buildGroupedHistory(events = events, onlyChanges = onlyChanges)
    }

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Historique",
                        color = WHITE_SOFT,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Retour",
                            color = TURQUOISE,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDeep
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgDeep, bgSlight, bgDeep)))
                .padding(padding)
        ) {

            if (grouped.isEmpty()) {

                // ✅ Placeholder “calme”
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = TURQUOISE.copy(alpha = 0.75f),
                        modifier = Modifier.size(44.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Bientôt : tes traces s’afficheront ici.",
                        color = WHITE_SOFT.copy(alpha = 0.78f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Lecture calme. Sans pression.",
                        color = WHITE_SOFT.copy(alpha = 0.50f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Voir seulement les changements",
                                color = WHITE_SOFT.copy(alpha = 0.90f),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Masque les doublons consécutifs (plus lisible).",
                                color = WHITE_SOFT.copy(alpha = 0.45f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = onlyChanges,
                            onCheckedChange = { onlyChanges = it }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // ✅ Pas de items() imbriqué : on fait une boucle normale
                        grouped.forEach { group ->

                            item(key = "day_${group.dayKey}") {
                                Column {
                                    DayHeader(
                                        prettyDay = group.prettyDay,
                                        dayKey = group.dayKey,
                                        count = group.events.size
                                    )

                                    Spacer(Modifier.height(10.dp))

                                    group.events.forEach { e ->
                                        HistoryEventCard(event = e)
                                    }

                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

