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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing
import com.maxjth.tracememoire.ui.tags.SimpleTagCategory
import com.maxjth.tracememoire.ui.tags.TagGroup
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

    // ✅ IMPORTANT : on utilise TES helpers déjà présents dans le projet (pas de doublons ici)
    val label = levelLabel(p)
    val editable = isEditable(nowMs, createdAtMs)

    // Slider Float (UI) mais store en Int
    var sliderValue by remember(p) { mutableStateOf(p.toFloat()) }
    LaunchedEffect(p) { sliderValue = p.toFloat() }

    // ✅ TAGS (Set<String> garanti)
    val selectedTags: Set<String> = (trace?.tags ?: emptySet())

    // ✅ Catégories démo (tu remplaceras par tes tags officiels ensuite)
    val categories = remember {
        listOf(
            SimpleTagCategory("Humeur globale", listOf("calme", "tendu", "stable", "agité", "lourd", "léger")),
            SimpleTagCategory("Énergie / Rythme", listOf("plein", "moyen", "vide", "régulier", "irrégulier")),
            SimpleTagCategory("Corps / Sensations", listOf("détendu", "raide", "fatigué", "ok", "douleur")),
            SimpleTagCategory("Présence / Attention", listOf("présent", "absent", "concentré", "dispersé")),
            SimpleTagCategory("Type de journée", listOf("normal", "intense", "repos", "social", "travail")),
            SimpleTagCategory("Motifs / Cycles", listOf("répétition", "changement", "pic", "creux"), premium = true),
            SimpleTagCategory("Environnement", listOf("bruit", "calme", "extérieur", "intérieur"), premium = true),
        )
    }

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
        },
        // ✅ Bouton Enregistrer collé en bas (comme tes images)
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // ✅ Pas de store.save() (sinon erreur)
                        // Ici tu pourras brancher TON action finale plus tard si tu en as une.
                    },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TURQUOISE.copy(alpha = 0.55f),
                        contentColor = WHITE_SOFT
                    )
                ) {
                    Text(
                        text = "Enregistrer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
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

            // ✅ TITRES (comme l'image)
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

            // ✅ SOUS-TEXTE (centré + gris + lineHeight)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Le présent intérieur est noté.\nÀ travers quelques repères.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.55f)
            )

            Spacer(Modifier.height(22.dp))

            // ✅ TRIANGLE (NE PAS TOUCHER)
            TriangleOutlineBreathing(
                percent = p,
                modifier = Modifier.size(260.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "$label • $p%",
                color = WHITE_SOFT.copy(alpha = 0.90f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(22.dp))

            // ✅ TAGS SÉLECTIONNÉS (en premier)
            Text(
                text = "Tags sélectionnés",
                modifier = Modifier.fillMaxWidth(),
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            if (selectedTags.isEmpty()) {
                Text(
                    text = "Aucun tag pour l’instant",
                    modifier = Modifier.fillMaxWidth(),
                    color = MAUVE.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "En général, 3 à 7 tags suffisent à capter la journée.",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            } else {
                // Si tu as déjà un composant “chips” officiel, tu peux le mettre ici.
                // Là, on laisse simple pour ne rien casser.
                Text(
                    text = selectedTags.joinToString("   "),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            // ✅ TAGS DU JOUR (accordéons + flèches mauves via TagGroup)
            Text(
                text = "Tags du jour",
                modifier = Modifier.fillMaxWidth(),
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            categories.forEach { cat ->
                TagGroup(
                    category = cat,
                    selectedTags = selectedTags,
                    enabled = editable,
                    mauve = MAUVE, // ✅ mauve pour flèches/accents (selon ton TagGroup)
                    textColor = WHITE_SOFT,
                    cardBg = Color.White.copy(alpha = 0.06f),
                    onToggleTag = { tag ->
                        if (editable) store.toggleTag(tag)
                    }
                )
                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(18.dp))

            // ✅ SLIDER (tu peux le laisser ici; si tu le veux plus haut, on le remonte après)
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

            // ✅ STATUS + NOTE (si tu veux les garder)
            TraceStatusBlock(
                createdAtMs = createdAtMs,
                updatedAtMs = updatedAtMs,
                updateCount = updateCount,
                nowMs = nowMs,
                textColor = WHITE_SOFT
            )

            Spacer(Modifier.height(24.dp))

            TraceNoteField(
                note = trace?.note ?: "",
                onNoteChange = { txt ->
                    if (editable) store.updateNote(txt)
                },
                enabled = editable,
                textColor = WHITE_SOFT
            )

            // ✅ Espace pour que le bottomBar ne cache rien
            Spacer(Modifier.height(120.dp))
        }
    }
}