package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing


import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/* ------------------------------
COULEURS (locales à l’écran 2)
-------------------------------- */

private val BG_DEEP = Color(0xFF0A0A0A)
private val WHITE_SOFT = Color(0xFFF5F5F5)
private val MAUVE = Color(0xFFB388FF)
private val TURQUOISE = Color(0xFF2ED1C3)

/* ------------------------------
ÉCRAN 2 — TRACE DU JOUR
-------------------------------- */

@Composable
fun TraceDuJourScreen(
    onBack: () -> Unit = {}
) {
    // ✅ STORE = source de vérité
    val store = remember { TraceDuJourStore() }

    // ✅ Horloge (pour le 24h / statut)
    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            nowMs = System.currentTimeMillis()
        }
    }

    // ✅ S’assure qu’une trace existe
    LaunchedEffect(Unit) {
        if (store.trace == null) {
            store.createNewTrace()
        }
    }

    val trace = store.trace

    // Fallback safe (si trace encore null 1 frame)
    val createdAtMs = trace?.createdAtMs ?: nowMs
    val updatedAtMs = trace?.updatedAtMs ?: nowMs
    val updateCount = trace?.updateCount ?: 0

    val p = (trace?.percent ?: 50).coerceIn(0, 100)
    val label = levelLabel(p)
    val editable = isEditable(nowMs, createdAtMs)
    val dynColor = lerpColor(TURQUOISE, MAUVE, p / 100f)

    // Slider Float (UI) mais store en Int
    var sliderValue by remember(p) { mutableStateOf(p.toFloat()) }
    LaunchedEffect(p) { sliderValue = p.toFloat() }

    Scaffold(
        containerColor = BG_DEEP,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = WHITE_SOFT
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Trace",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "d’état d’âme",
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            // TRIANGLE (NE PAS TOUCHER)
            TriangleOutlineBreathing(
                percent = p,
                modifier = Modifier.size(260.dp)
            )

            Spacer(Modifier.height(12.dp))
            PercentMessageBubble(p, dynColor)

            Spacer(Modifier.height(8.dp))
            Text(text = "$label • $p%", color = WHITE_SOFT)

            Spacer(Modifier.height(22.dp))

            // ✅ TAGS (Set<String> garanti)
            val selectedTags: Set<String> = (trace?.tags ?: emptySet())


            Spacer(Modifier.height(22.dp))

            // ✅ SLIDER (branché store)
            Slider(
                value = sliderValue,
                onValueChange = { v ->
                    if (!editable) return@Slider
                    sliderValue = v
                    store.updatePercent(v.roundToInt().coerceIn(0, 100))
                },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            TraceStatusBlock(
                createdAtMs = createdAtMs,
                updatedAtMs = updatedAtMs,
                updateCount = updateCount,
                nowMs = nowMs,
                textColor = WHITE_SOFT
            )

            Spacer(Modifier.height(24.dp))

            // ✅ NOTE (branchée store)
            TraceNoteField(
                note = trace?.note ?: "",
                onNoteChange = { txt ->
                    if (editable) store.updateNote(txt)
                },
                enabled = editable,
                textColor = WHITE_SOFT
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}