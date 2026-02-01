@file:OptIn(ExperimentalLayoutApi::class)

package com.maxjth.tracememoire.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================
// TRACE MÉMOIRE — ÉCRAN 3 (LECTURE)
// Fichier complet : TraceDuTempsVecuScreen.kt
// =====================================================

// ================================
// COULEURS (CalmTrace)
// ================================
private val BG_DEEP = Color(0xFF000000)
private val PANEL_DEEP = Color(0xFF0A0A0A)
private val WHITE_SOFT = Color(0xFFE9E9E9)
private val TURQUOISE = Color(0xFF00A3A3)
private val MAUVE = Color(0xFF7A63C6)

// ================================
// CONSTANTES UI
// ================================
private val SQUARE_RADIUS = 22.dp
private val SQUARE_BORDER = 2.2.dp
private val SQUARE_HEIGHT = 360.dp // ✅ un peu plus safe (évite coupures)
private val NOTE_CARD_RADIUS = 18.dp
private val NOTE_CARD_MIN_H = 78.dp
private val CHIP_RADIUS = 14.dp
private val CHIP_PAD_H = 12.dp
private val CHIP_PAD_V = 8.dp

// ================================
// TEMPS OFFICIEL (verrouillé)
// ================================
private const val H_NUIT_START = 0
private const val H_NUIT_END = 5
private const val H_MATIN_START = 6
private const val H_MATIN_END = 11
private const val H_JOUR_START = 12
private const val H_JOUR_END = 17
private const val H_SOIR_START = 18
private const val H_SOIR_END = 23

// ================================
// LIMITES INTERNES (non visibles)
// ================================
private const val MAX_MATIN = 4
private const val MAX_JOUR = 6
private const val MAX_SOIR = 5
private const val MAX_NUIT = 3

// ================================
// ZONES DU CARRÉ
// ================================
enum class TimeBlock { MATIN, JOUR, SOIR, NUIT }

// ================================
// CATÉGORIES (Écran 3 — Faits)
// ================================
enum class CategoryTier { FREE, PREMIUM }

@Immutable
data class CategoryDef(
    val id: String,
    val label: String,
    val tier: CategoryTier,
    val keywords: List<String>
)

// ✅ 7 GRATUITES (factuelles)
private val CATEGORIES_FREE = listOf(
    CategoryDef(
        id = "act",
        label = "Activités",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "travail", "ménage", "courses", "cuisine", "repos",
            "marche", "organisation", "entretien", "tâches diverses", "lecture"
        )
    ),
    CategoryDef(
        id = "sport",
        label = "Sport",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "gym", "course", "vélo", "marche sportive", "étirements",
            "musculation", "cardio", "sport d’équipe", "entraînement", "récupération"
        )
    ),
    CategoryDef(
        id = "dep",
        label = "Déplacements",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "voiture", "transport en commun", "marche", "vélo", "taxi / Uber",
            "court trajet", "long trajet", "trajet habituel", "trajet exceptionnel", "aucun déplacement"
        )
    ),
    CategoryDef(
        id = "rdv",
        label = "Rendez-vous",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "médical", "travail", "administratif", "appel prévu", "rencontre planifiée",
            "livraison", "service professionnel", "démarche personnelle", "entrevue", "aucun rendez-vous"
        )
    ),
    CategoryDef(
        id = "quo",
        label = "Vie quotidienne",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "maison", "à l’extérieur", "journée normale", "journée chargée", "journée calme",
            "routine", "imprévu", "organisation personnelle", "gestion du temps", "journée courte"
        )
    ),
    CategoryDef(
        id = "int",
        label = "Interactions",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "famille", "amis", "collègues", "voisins", "service client",
            "personnel médical", "administration", "interaction brève", "interaction prolongée", "aucune interaction"
        )
    ),
    CategoryDef(
        id = "adm",
        label = "Administratif",
        tier = CategoryTier.FREE,
        keywords = listOf(
            "papier", "courriel", "appel", "documents", "rendez-vous administratif",
            "paiement", "banque", "formulaire", "attente", "démarche"
        )
    )
)

// ✅ 7 PREMIUM (factuelles)
private val CATEGORIES_PREMIUM = listOf(
    CategoryDef(
        id = "food",
        label = "Bouffe",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "maison", "restaurant", "fast-food", "livraison", "prêt-à-manger",
            "copieux", "léger", "irrégulier", "café / thé", "dessert"
        )
    ),
    CategoryDef(
        id = "leis",
        label = "Loisirs",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "télévision", "films", "séries", "lecture", "jeux",
            "musique", "internet", "sortie", "création", "temps libre"
        )
    ),
    CategoryDef(
        id = "trans",
        label = "Transport détaillé",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "trafic", "retard", "attente", "correspondance", "stationnement",
            "trajet fluide", "trajet compliqué", "horaires serrés", "déplacement annulé", "retour tardif"
        )
    ),
    CategoryDef(
        id = "rel",
        label = "Relations & couple",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "partenaire", "famille proche", "discussion importante", "temps partagé", "échange pratique",
            "organisation commune", "appel personnel", "message important", "rencontre prévue", "relation à distance"
        )
    ),
    CategoryDef(
        id = "evt",
        label = "Événements",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "journée spéciale", "imprévu", "changement", "décision", "début",
            "fin", "transition", "annonce", "incident", "moment marquant"
        )
    ),
    CategoryDef(
        id = "homeplus",
        label = "Maison & espace",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "rangement", "nettoyage", "réparation", "entretien maison", "courses maison",
            "organisation espace", "linge", "cuisine maison", "petits travaux", "repos à la maison"
        )
    ),
    CategoryDef(
        id = "intim",
        label = "Intimité (non explicite)",
        tier = CategoryTier.PREMIUM,
        keywords = listOf(
            "proximité", "câlins", "temps à deux", "moment privé", "présence partagée",
            "rapprochement", "contact affectif", "douceur", "complicité", "calme"
        )
    )
)

private val ALL_CATEGORIES = CATEGORIES_FREE + CATEGORIES_PREMIUM

// ================================
// MODELES DE DONNÉES
// ================================
@Immutable
data class TagEvent(
    val label: String,
    val categoryId: String,
    val timestampMs: Long
)

enum class SquareState { VIDE, CALME, ACTIF, LOURD }

@Immutable
data class TraceTempsVecuUi(
    val dateTexte: String,
    val dayStartMs: Long,
    val dayEndMs: Long,
    val percent: Int,
    val percentLabel: String,
    val phraseSignature: String?,
    val motPivot: String?,
    val note: String?,
    val tagEvents: List<TagEvent>,
    val createdAtMs: Long,
    val savedAtTexte: String,
    val modifiedCount: Int
)

// ================================
// ÉCRAN 3 — Trace du temps vécu
// ================================
@Composable
fun TraceDuTempsVecuScreen(
    ui: TraceTempsVecuUi? = null,
    onBack: () -> Unit
) {
    // ---------- DEMO ----------
    val demo = remember {
        val zoneId = ZoneId.systemDefault()
        val now = System.currentTimeMillis()
        val start = startOfDayMs(now, zoneId)
        val end = endOfDayMs(now, zoneId)
        TraceTempsVecuUi(
            dateTexte = formatFullDate(now, zoneId),
            dayStartMs = start,
            dayEndMs = end,
            percent = 54,
            percentLabel = "Modéré",
            phraseSignature = "Journée stable et routinière, centrée sur tâches, déplacements et repos.",
            motPivot = "routine",
            note = "Aujourd’hui, j’ai gardé un rythme simple. Pas parfait, mais stable.",
            tagEvents = listOf(
                TagEvent("café / thé", "food", tsAtHour(start, zoneId, 8, 20)),
                TagEvent("trajet habituel", "dep", tsAtHour(start, zoneId, 9, 10)),
                TagEvent("travail", "act", tsAtHour(start, zoneId, 10, 0)),
                TagEvent("transport en commun", "dep", tsAtHour(start, zoneId, 12, 30)),
                TagEvent("courses", "act", tsAtHour(start, zoneId, 15, 40)),
                TagEvent("gym", "sport", tsAtHour(start, zoneId, 18, 15)),
                TagEvent("dîner", "quo", tsAtHour(start, zoneId, 19, 10)),
                TagEvent("films", "leis", tsAtHour(start, zoneId, 21, 0)),
                TagEvent("sommeil", "quo", tsAtHour(start, zoneId, 1, 30))
            ),
            createdAtMs = now - 15 * 60 * 60 * 1000L,
            savedAtTexte = "Sauvegardée aujourd’hui à 20h45",
            modifiedCount = 1
        )
    }

    val data = ui ?: demo

    val nowMs = System.currentTimeMillis()
    val editable = nowMs < (data.createdAtMs + 24L * 60L * 60L * 1000L)

    val zoneId = ZoneId.systemDefault()

    val blocks = remember(data.tagEvents) {
        groupTagsIntoBlocks(data.tagEvents, zoneId)
    }

    val squareState = remember(data.tagEvents) {
        computeSquareState(blocks)
    }

    val t = rememberInfiniteTransition(label = "squareHalo")
    val haloPulse by t.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    val expand = remember {
        mutableStateMapOf<String, Boolean>().apply {
            this["FREE_SECTION"] = true
            this["PREMIUM_SECTION"] = false
            ALL_CATEGORIES.forEach { this[it.id] = false }
        }
    }

    Scaffold(
        containerColor = BG_DEEP,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Retour",
                        tint = WHITE_SOFT.copy(alpha = 0.85f)
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))

            Text("Trace", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
            Text("du temps vécu", fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)

            Spacer(Modifier.height(12.dp))
            Text(
                "Ce qui s’est passé est conservé. Tel que vécu.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = WHITE_SOFT.copy(alpha = 0.72f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))
            Text(
                data.dateTexte,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = WHITE_SOFT.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            data.motPivot?.takeIf { it.isNotBlank() }?.let { pivot ->
                Text(
                    pivot,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TURQUOISE.copy(alpha = 0.60f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
            }

            data.phraseSignature?.takeIf { it.isNotBlank() }?.let { phrase ->
                Text(
                    phrase,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = WHITE_SOFT.copy(alpha = 0.80f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(24.dp))
            } ?: Spacer(Modifier.height(24.dp))

            SquareWithBlocks(
                haloAlpha = haloPulse,
                state = squareState,
                matin = blocks[TimeBlock.MATIN].orEmpty(),
                jour = blocks[TimeBlock.JOUR].orEmpty(),
                soir = blocks[TimeBlock.SOIR].orEmpty(),
                nuit = blocks[TimeBlock.NUIT].orEmpty()
            )

            Spacer(Modifier.height(10.dp))

            TemporalLine(
                dateTexte = data.dateTexte,
                dayStartMs = data.dayStartMs,
                dayEndMs = data.dayEndMs,
                zoneId = zoneId
            )

            Spacer(Modifier.height(28.dp))

            val tagsByCategory = remember(data.tagEvents) {
                data.tagEvents.groupBy { it.categoryId }
                    .mapValues { entry -> entry.value.map { it.label }.distinct() }
            }

            Text(
                "Tags par catégories",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            CollapsibleHeader(
                title = "Gratuit",
                titleColor = WHITE_SOFT.copy(alpha = 0.92f),
                expanded = expand["FREE_SECTION"] == true,
                onToggle = { expand["FREE_SECTION"] = !(expand["FREE_SECTION"] == true) }
            )

            if (expand["FREE_SECTION"] == true) {
                Spacer(Modifier.height(8.dp))
                CategoryList(
                    categories = CATEGORIES_FREE,
                    tagsByCategory = tagsByCategory,
                    expandMap = expand,
                    accentColor = TURQUOISE,
                    premiumColor = MAUVE
                )
            }

            Spacer(Modifier.height(12.dp))

            CollapsibleHeader(
                title = "Premium",
                titleColor = MAUVE.copy(alpha = 0.92f),
                expanded = expand["PREMIUM_SECTION"] == true,
                onToggle = { expand["PREMIUM_SECTION"] = !(expand["PREMIUM_SECTION"] == true) }
            )

            if (expand["PREMIUM_SECTION"] == true) {
                Spacer(Modifier.height(8.dp))
                CategoryList(
                    categories = CATEGORIES_PREMIUM,
                    tagsByCategory = tagsByCategory,
                    expandMap = expand,
                    accentColor = TURQUOISE,
                    premiumColor = MAUVE
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Note de la journée",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))
            NoteCard(note = data.note)

            Spacer(Modifier.height(14.dp))
            Text(
                "${data.percent} % – ${data.percentLabel}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TURQUOISE.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))

            Text(
                if (editable) "Modifiable encore : 24 h" else "Trace figée (24 h écoulées)",
                fontSize = 12.sp,
                color = WHITE_SOFT.copy(alpha = 0.56f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // ✅ FIX : interpolation Kotlin normale
            Text(
                text = "${data.savedAtTexte} · Modifiée ×${data.modifiedCount}",
                fontSize = 12.sp,
                color = WHITE_SOFT.copy(alpha = 0.45f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(72.dp))
        }
    }
}

// =====================================================
// COMPOSANTS UI — CARRÉ
// =====================================================
@Composable
private fun SquareWithBlocks(
    haloAlpha: Float,
    state: SquareState,
    matin: List<String>,
    jour: List<String>,
    soir: List<String>,
    nuit: List<String>
) {
    val style = squareStyleFor(state)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .wrapContentHeight()
            .drawBehind {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension * 0.62f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = style.haloAlphaBase * haloAlpha * 6.5f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SQUARE_HEIGHT)
                .clip(RoundedCornerShape(SQUARE_RADIUS))
                .background(PANEL_DEEP)
                .border(SQUARE_BORDER, style.borderColor, RoundedCornerShape(SQUARE_RADIUS))
                .drawBehind {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension * 0.60f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = style.innerGlowAlpha),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SquareBlock("MATIN", matin, MAX_MATIN)
                SquareBlock("JOUR", jour, MAX_JOUR)
                SquareBlock("SOIR", soir, MAX_SOIR)
                SquareBlock("NUIT", nuit, MAX_NUIT)
            }
        }
    }
}

@Composable
private fun SquareBlock(
    label: String,
    tags: List<String>,
    maxVisible: Int
) {
    val trimmed = tags.distinct().map { it.trim() }.filter { it.isNotBlank() }
    val visible = trimmed.take(maxVisible)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TURQUOISE.copy(alpha = 0.90f)
            )
            Spacer(Modifier.weight(1f))
            TracePill()
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            visible.forEach { tag ->
                ChipReadOnly(text = tag)
            }
        }
    }
}

@Composable
private fun TracePill() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(TURQUOISE.copy(alpha = 0.80f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "trace",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.82f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Immutable
private data class SquareStyle(
    val borderColor: Color,
    val innerGlowAlpha: Float,
    val haloAlphaBase: Float
)

private fun squareStyleFor(state: SquareState): SquareStyle {
    return when (state) {
        SquareState.VIDE -> SquareStyle(
            borderColor = MAUVE.copy(alpha = 0.55f),
            innerGlowAlpha = 0.06f,
            haloAlphaBase = 0.08f
        )
        SquareState.CALME -> SquareStyle(
            borderColor = MAUVE.copy(alpha = 0.78f),
            innerGlowAlpha = 0.10f,
            haloAlphaBase = 0.11f
        )
        SquareState.ACTIF -> SquareStyle(
            borderColor = MAUVE.copy(alpha = 0.92f),
            innerGlowAlpha = 0.12f,
            haloAlphaBase = 0.13f
        )
        SquareState.LOURD -> SquareStyle(
            borderColor = MAUVE.copy(alpha = 0.90f),
            innerGlowAlpha = 0.08f,
            haloAlphaBase = 0.09f
        )
    }
}

// =====================================================
// COMPOSANTS UI — TAGS / CATÉGORIES
// =====================================================
@Composable
private fun CollapsibleHeader(
    title: String,
    titleColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = if (expanded) "—" else "+",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun CategoryList(
    categories: List<CategoryDef>,
    tagsByCategory: Map<String, List<String>>,
    expandMap: MutableMap<String, Boolean>,
    accentColor: Color,
    premiumColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { cat ->
            val usedTags = tagsByCategory[cat.id].orEmpty().distinct()
            if (usedTags.isEmpty()) return@forEach

            val isExpanded = expandMap[cat.id] == true

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { expandMap[cat.id] = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cat.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (cat.tier == CategoryTier.PREMIUM)
                            premiumColor.copy(alpha = 0.92f)
                        else
                            WHITE_SOFT.copy(alpha = 0.92f)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (isExpanded) "—" else "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = (if (cat.tier == CategoryTier.PREMIUM) premiumColor else accentColor)
                            .copy(alpha = 0.80f)
                    )
                }

                if (isExpanded) {
                    Spacer(Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        usedTags.forEach { tag ->
                            ChipReadOnly(text = tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipReadOnly(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CHIP_RADIUS))
            .background(TURQUOISE.copy(alpha = 0.85f))
            .padding(horizontal = CHIP_PAD_H, vertical = CHIP_PAD_V),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoteCard(note: String?) {
    val n = (note ?: "").trim()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = NOTE_CARD_MIN_H)
            .clip(RoundedCornerShape(NOTE_CARD_RADIUS))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = if (n.isEmpty()) "Aucune note écrite ce jour." else n,
            fontSize = 16.sp,
            color = if (n.isEmpty()) WHITE_SOFT.copy(alpha = 0.55f) else WHITE_SOFT,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

// =====================================================
// LIGNE TEMPORELLE
// =====================================================
@Composable
private fun TemporalLine(
    dateTexte: String,
    dayStartMs: Long,
    dayEndMs: Long,
    zoneId: ZoneId
) {
    val startTxt = formatHour(dayStartMs, zoneId)
    val endTxt = formatHour(dayEndMs, zoneId)

    Column(
        modifier = Modifier.fillMaxWidth(0.92f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = Color.White.copy(alpha = 0.10f), thickness = 1.dp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$dateTexte · $startTxt–$endTxt",
            fontSize = 12.sp,
            color = WHITE_SOFT.copy(alpha = 0.45f),
            textAlign = TextAlign.Center
        )
    }
}

// =====================================================
// LOGIQUE — CLASSEMENT HORAIRE
// =====================================================
private fun groupTagsIntoBlocks(
    tagEvents: List<TagEvent>,
    zoneId: ZoneId
): Map<TimeBlock, List<String>> {
    val map = mutableMapOf(
        TimeBlock.MATIN to mutableListOf<String>(),
        TimeBlock.JOUR to mutableListOf<String>(),
        TimeBlock.SOIR to mutableListOf<String>(),
        TimeBlock.NUIT to mutableListOf<String>()
    )

    tagEvents.forEach { ev ->
        val block = timeBlockOf(ev.timestampMs, zoneId)
        map[block]?.add(ev.label)
    }

    return map.mapValues { (_, list) ->
        list.map { it.trim() }.filter { it.isNotBlank() }.distinct()
    }
}

private fun timeBlockOf(timestampMs: Long, zoneId: ZoneId): TimeBlock {
    val hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMs), zoneId).hour
    return when (hour) {
        in H_NUIT_START..H_NUIT_END -> TimeBlock.NUIT
        in H_MATIN_START..H_MATIN_END -> TimeBlock.MATIN
        in H_JOUR_START..H_JOUR_END -> TimeBlock.JOUR
        else -> TimeBlock.SOIR
    }
}

// =====================================================
// LOGIQUE — ÉTAT VISUEL DU CARRÉ
// =====================================================
private fun computeSquareState(
    blocks: Map<TimeBlock, List<String>>
): SquareState {
    val m = blocks[TimeBlock.MATIN].orEmpty().size
    val j = blocks[TimeBlock.JOUR].orEmpty().size
    val s = blocks[TimeBlock.SOIR].orEmpty().size
    val n = blocks[TimeBlock.NUIT].orEmpty().size
    val total = m + j + s + n
    val nonEmpty = listOf(m, j, s, n).count { it > 0 }

    return when {
        total == 0 -> SquareState.VIDE
        total <= 5 && nonEmpty <= 2 -> SquareState.CALME
        total <= 12 -> SquareState.ACTIF
        else -> SquareState.LOURD
    }
}

// =====================================================
// HELPERS DATE / HEURE
// =====================================================
private fun formatFullDate(nowMs: Long, zoneId: ZoneId): String {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(nowMs), zoneId)
    val fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)
    return dt.format(fmt).replaceFirstChar { it.uppercase() }
}

private fun formatHour(ms: Long, zoneId: ZoneId): String {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), zoneId)
    val fmt = DateTimeFormatter.ofPattern("HH'h'mm", Locale.FRENCH)
    return dt.format(fmt)
}

private fun startOfDayMs(anyMs: Long, zoneId: ZoneId): Long {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(anyMs), zoneId)
        .withHour(0).withMinute(0).withSecond(0).withNano(0)
    return dt.atZone(zoneId).toInstant().toEpochMilli()
}

private fun endOfDayMs(anyMs: Long, zoneId: ZoneId): Long {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(anyMs), zoneId)
        .withHour(23).withMinute(59).withSecond(0).withNano(0)
    return dt.atZone(zoneId).toInstant().toEpochMilli()
}

private fun tsAtHour(dayStartMs: Long, zoneId: ZoneId, hour: Int, minute: Int): Long {
    val start = LocalDateTime.ofInstant(Instant.ofEpochMilli(dayStartMs), zoneId)
    val dt = start.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    return dt.atZone(zoneId).toInstant().toEpochMilli()
}

