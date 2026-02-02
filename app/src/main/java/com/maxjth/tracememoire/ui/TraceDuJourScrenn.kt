file:OptIn(ExperimentalLayoutApi::class)

package com.maxjth.tracememoire.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

// ── Couleurs CalmTrace ───────────────────────────────────────
private val BG_DEEP     = Color(0xFF000000)
private val WHITE_SOFT  = Color(0xFFE9E9E9)
private val TEXT_MUTED  = Color(0xFFAAAAAA)
private val TURQUOISE   = Color(0xFF00A3A3)
private val MAUVE       = Color(0xFF7A63C6)

// ── Utilitaires couleurs, labels, temps ──────────────────────
private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = a.alpha + (b.alpha - a.alpha) * tt
    )
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)

private fun levelLabel(p: Int): String = when {
    p <= 15 -> "Très bas"
    p <= 35 -> "Bas"
    p <= 55 -> "Modéré"
    p <= 75 -> "Élevé"
    p <= 90 -> "Très élevé"
    else    -> "Max"
}

private const val ONE_HOUR_MS = 60L * 60L * 1000L
private const val ONE_DAY_MS  = 24L * ONE_HOUR_MS

private fun isEditable(nowMs: Long, createdAtMs: Long): Boolean =
    nowMs < (createdAtMs + ONE_DAY_MS)

private fun timeLeftLabel(nowMs: Long, createdAtMs: Long): String {
    val lockAt = createdAtMs + ONE_DAY_MS
    val left = (lockAt - nowMs).coerceAtLeast(0L)
    val h = (left / ONE_HOUR_MS).toInt()
    val m = ((left % ONE_HOUR_MS) / 60_000L).toInt()
    return "${h}h ${m}m"
}

// ✅ Corrigé (Kotlin pur)
private fun hhmm(ms: Long): String {
    val totalMin = (ms / 60_000L).toInt()
    val h = (totalMin / 60) % 24
    val m = totalMin % 60
    fun two(x: Int) = if (x < 10) "0$x" else "$x"
    return "${two(h)}:${two(m)}"
}

// ── Bulle de description du pourcentage ──────────────────────
private fun scoreGrid(p: Int): Pair<String, String> {
    return when (p.coerceIn(0, 100)) {
        in 0..10   -> "Très bas"     to "fatigué · lourd · difficile"
        in 11..25  -> "Bas"          to "peu d’énergie · tendu · ralenti"
        in 26..40  -> "Plutôt bas"   to "fragile · irrégulier · demandant"
        in 41..55  -> "Modéré"       to "correct · fonctionnel · neutre"
        in 56..70  -> "Plutôt bon"   to "stable · disponible · posé"
        in 71..85  -> "Bon"          to "énergie présente · fluide · à l’aise"
        in 86..95  -> "Très bon"     to "léger · clair · positif"
        else       -> "Maximum"      to "pleinement présent · aligné · fort"
    }
}

@Composable
private fun PercentMessageBubble(
    p: Int,
    dyn: Color,
    modifier: Modifier = Modifier
) {
    val (title, words) = scoreGrid(p)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = words,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MAUVE.copy(alpha = 0.90f),
                textAlign = TextAlign.Center
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .height(2.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(dyn.copy(alpha = 0.18f))
    )
}

// ── Triangle respirant ───────────────────────────────────────
private fun buildTrianglePath(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    insetRatio: Float = 0.06f,
    baseWidthRatio: Float = 0.92f
): Path {
    val w = right - left
    val h = bottom - top
    val cx = (left + right) / 2f
    val inset = insetRatio.coerceIn(0f, 0.20f)
    val base = baseWidthRatio.coerceIn(0.70f, 1f)
    val halfBase = (w * base) / 2f

    return Path().apply {
        moveTo(cx, top + h * inset)
        lineTo(cx + halfBase, top + h * 0.95f)
        lineTo(cx - halfBase, top + h * 0.95f)
        close()
    }
}

@Composable
private fun TriangleOutlineBreathing(
    percent: Int,
    isInteracting: Boolean,
    modifier: Modifier = Modifier
) {
    val pct = percent.coerceIn(0, 100)
    val t = pct / 100f

    val baseColor = remember(pct) { lerpColor(TURQUOISE, MAUVE, t) }
    val triColor  = remember(pct) { baseColor.desaturate(0.22f).brighten(0.10f) }

    val isZero = pct == 0
    val isMax  = pct >= 100

    val excite = when {
        isMax           -> 1.0f
        isInteracting   -> 0.75f
        else            -> 0f
    }

    val calmBoost = if (isZero) 0.15f else 1f

    val baseDuration = lerpFloat(8200f, 4200f, t).roundToInt()
    val duration = when {
        isMax         -> (baseDuration * 0.55f).toInt()
        isInteracting -> (baseDuration * 0.70f).toInt()
        isZero        -> 9800
        else          -> baseDuration
    }

    val infiniteTransition = rememberInfiniteTransition(label = "triangle-breath")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val w1 = ((sin(phase.toDouble()) + 1.0) / 2.0).toFloat()
    val w2 = ((sin((phase * 0.72f).toDouble() + 1.3) + 1.0) / 2.0).toFloat()

    val strokeBase     = lerpFloat(17f, 22f, t) * calmBoost
    val strokeAmpBase  = lerpFloat(0.8f, 6f, t * t) * calmBoost
    val strokeAmp      = strokeAmpBase * (1f + 0.9f * excite)
    val strokeExtra    = 0.8f * excite
    val stroke         = strokeBase + strokeExtra + strokeAmp * (0.65f * w1 + 0.35f * w2)

    val glowOuterBase = lerpFloat(0.05f, 0.30f, t * t) * calmBoost
    val glowInnerBase = lerpFloat(0.04f, 0.22f, t * t) * calmBoost
    val hiBase        = lerpFloat(0.06f, 0.26f, t * t) * calmBoost

    val glowOuter = glowOuterBase * (0.60f + 0.40f * w1) * (1f + 1.25f * excite)
    val glowInner = glowInnerBase * (0.60f + 0.40f * w2) * (1f + 1.10f * excite)
    val hi        = hiBase        * (0.55f + 0.45f * w1) * (1f + 1.35f * excite)

    val jitterX = if (isZero) ((w1 - 0.5f) * 2f) * 2.2f else 0f
    val jitterY = if (isZero) ((w2 - 0.5f) * 2f) * 1.6f else 0f

    val pctFontSize = if (pct >= 100) 56.sp else 64.sp
    val pctOffsetY = when {
        pct >= 100 -> 36.dp
        pct >= 90  -> 30.dp
        else       -> 22.dp
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val d = minOf(size.width, size.height)
            val minScale = 0.78f
            val scale = lerpFloat(minScale, 1.00f, t)

            val triW = d * 0.96f * scale
            val triH = d * 1.00f * scale

            val left = (size.width - triW) / 2f + jitterX
            val top  = (size.height - triH) / 2f + jitterY

            val path = buildTrianglePath(
                left = left,
                top = top,
                right = left + triW,
                bottom = top + triH
            )

            drawPath(
                path,
                triColor.copy(alpha = glowOuter),
                style = Stroke(width = stroke + 18f, cap = StrokeCap.Butt, join = StrokeJoin.Miter, miter = 10f)
            )
            drawPath(
                path,
                triColor.copy(alpha = glowInner),
                style = Stroke(width = stroke + 8f, cap = StrokeCap.Butt, join = StrokeJoin.Miter, miter = 10f)
            )
            drawPath(
                path = path,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        triColor.copy(alpha = 0.92f),
                        Color.White.copy(alpha = hi),
                        triColor.copy(alpha = 0.92f)
                    ),
                    center = center
                ),
                style = Stroke(width = stroke + 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter, miter = 10f)
            )
        }

        Text(
            text = "$pct%",
            fontSize = pctFontSize,
            fontWeight = FontWeight.ExtraBold,
            color = triColor.brighten(0.06f).copy(alpha = 0.96f),
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = pctOffsetY)
        )
    }
}

// ── Helpers couleur ──────────────────────────────────────────
private fun Color.desaturate(amount: Float): Color {
    val a = amount.coerceIn(0f, 1f)
    val gray = red * 0.2126f + green * 0.7152f + blue * 0.0722f
    fun mix(c: Float) = c + (gray - c) * a
    return Color(mix(red), mix(green), mix(blue), alpha)
}

private fun Color.brighten(amount: Float): Color {
    val a = amount.coerceIn(0f, 1f)
    fun up(c: Float) = (c + (1f - c) * a).coerceIn(0f, 1f)
    return Color(up(red), up(green), up(blue), alpha)
}

// ── Tags (Écran 2) ───────────────────────────────────────────
// ✅ IMPORTANT : on enlève "private" sinon MainActivity/écran 3 ne peut pas y accéder
enum class Tier { FREE, PREMIUM }

data class TagCategory(
    val title: String,
    val foundation: String,
    val tier: Tier,
    val tags: List<String>
)

// ✅ IMPORTANT : on enlève "private" sinon erreur "it is private in file"
val TAG_GROUPS_OFFICIAL = listOf(
    TagCategory("Humeur globale",         "Quelle est la tonalité dominante de la journée ?",          Tier.FREE,    listOf("calme", "tendu", "stable", "agité", "lourd", "léger", "équilibré", "joyeux", "sombre")),
    TagCategory("Énergie / Rythme",       "Comment l’énergie a-t-elle circulé dans le temps ?",       Tier.FREE,    listOf("lent", "fluide", "rapide", "irrégulier", "soutenu", "épuisant", "constant", "énergisé", "ralenti")),
    TagCategory("Corps / Sensations",     "Qu’a exprimé le corps aujourd’hui, sans interprétation ?", Tier.FREE,   listOf("reposée", "fatigué", "tendu", "détendu", "inconfort", "à l’aise", "crispé", "relâché")),
    TagCategory("Présence / Attention",   "Où se situait l’attention la plupart du temps ?",          Tier.FREE,    listOf("présent", "distrait", "concentré", "dispersé", "attentif", "absent", "ancré", "flottant")),
    TagCategory("Type de journée",        "Quel était le décor dominant de la journée ?",             Tier.FREE,    listOf("travail", "repos", "social", "maison", "extérieur", "déplacements", "mixte", "solitude choisie")),

    TagCategory("Motifs / Cycles",        "Quel pattern temporel se répète ou se brise ?",            Tier.PREMIUM, listOf("similaire à hier", "répétitif", "changement", "rupture", "cycle connu", "progression", "stagnation", "retour inattendu")),
    TagCategory("Environnement",          "Qu’est-ce qui entourait la journée (bruit, foule, air, espace) ?", Tier.PREMIUM, listOf("bruit", "calme", "météo lourde", "foule", "isolement", "mouvement", "nature", "confiné")),
    TagCategory("Clarté mentale",         "Quel était l’état de lisibilité mentale global ?",         Tier.PREMIUM, listOf("clair", "chargé", "confus", "léger", "saturé", "posé", "fluide", "embrumé")),
    TagCategory("Charge émotionnelle",    "Quelle intensité émotionnelle était présente (sans jugement) ?", Tier.PREMIUM, listOf("faible", "présente", "intense", "contenue", "débordante", "instable", "maîtrisée")),
    TagCategory("Alignement intérieur",   "Le ressenti global était-il aligné, en friction, ou en transition ?", Tier.PREMIUM, listOf("aligné", "désaligné", "en transition", "résistant", "ouvert", "fermé", "en questionnement"))
)

// ✅ IMPORTANT : on enlève "private" si c’est appelé ailleurs
fun tierAllowed(tier: Tier, hasPremium: Boolean, hasPremiumPlus: Boolean): Boolean =
    tier == Tier.FREE || hasPremium || hasPremiumPlus

// ── Chip tag ─────────────────────────────────────────────────
@Composable
private fun TagChip(
    text: String,
    active: Boolean,
    enabled: Boolean,
    badge: String? = null,
    onToggle: () -> Unit,
    triggerTime: Long? = null
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(triggerTime) {
        triggerTime?.takeIf { it > 0 }?.let {
            scale.animateTo(1.14f, tween(140, easing = FastOutSlowInEasing))
            scale.animateTo(1f,   tween(220, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = Modifier
            .scale(scale.value)
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) MAUVE.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f))
            .border(
                width = if (active) 1.5.dp else 0.dp,
                color = if (active) MAUVE else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled) { onToggle() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                color = if (active) MAUVE else WHITE_SOFT.copy(alpha = 0.88f)
            )
            badge?.let {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = MAUVE.copy(alpha = if (enabled) 0.9f else 0.35f)
                )
            }
        }
    }
}

@Composable
private fun SelectedTagChip(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MAUVE.copy(alpha = 0.16f))
            .padding(start = 12.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = WHITE_SOFT.copy(alpha = 0.94f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Retirer le tag $text",
            tint = MAUVE,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable { onRemove() }
                .padding(2.dp)
        )
    }
}

@Composable
private fun SelectedTagsSection(
    selectedTags: Set<String>,
    onRemove: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tags sélectionnés",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = WHITE_SOFT.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (selectedTags.isEmpty()) {
            Text(
                text = "Aucun tag pour l’instant",
                fontSize = 13.sp,
                color = Color(0xFF9B8CFF).copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Souvent, 3 à 7 tags suffisent.",
                fontSize = 12.sp,
                color = WHITE_SOFT.copy(alpha = 0.50f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                selectedTags.forEach { tag ->
                    SelectedTagChip(text = tag, onRemove = { onRemove(tag) })
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

// ── Accordéon catégorie ───────────────────────────

@Composable
private fun TagCategoryAccordion(
    category: TagCategory,
    isOpen: Boolean,
    onToggle: () -> Unit,
    allowed: Boolean,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
    animationTriggers: Map<String, Long>
) {
    val locked = !allowed
    val count = category.tags.count { it in selectedTags }
    val titleDisplay = if (count > 0) "${category.title} ($count)" else category.title

    val arrowTint = if (isOpen) TURQUOISE.copy(alpha = 0.88f) else TURQUOISE.copy(alpha = 0.42f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleDisplay,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (count > 0) MAUVE else WHITE_SOFT,
                modifier = Modifier.weight(1f)
            )
            if (locked) {
                Text("Premium", fontSize = 13.sp, color = MAUVE.copy(alpha = 0.75f))
                Spacer(Modifier.width(8.dp))
            }
            Icon(
                imageVector = if (isOpen) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (isOpen) "Réduire catégorie" else "Développer catégorie",
                tint = arrowTint
            )
        }

        if (isOpen) {
            Text(
                text = category.foundation,
                fontSize = 12.5.sp,
                fontStyle = FontStyle.Italic,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            )

            if (locked) {
                Text(
                    "Débloqué avec Premium",
                    fontSize = 13.sp,
                    color = WHITE_SOFT.copy(alpha = 0.45f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    category.tags.forEach { tag ->
                        TagChip(
                            text = tag,
                            active = tag in selectedTags,
                            enabled = true,
                            badge = if (category.tier == Tier.PREMIUM) "Premium" else null,
                            onToggle = { onToggleTag(tag) },
                            triggerTime = animationTriggers[tag]
                        )
                    }
                }
            }
        }
    }
}

// ── Statut de la trace ───────────────────────────────────────
@Composable
private fun TraceStatusBlock(
    createdAtMs: Long,
    updatedAtMs: Long,
    updateCount: Int,
    nowMs: Long
) {
    val editable = isEditable(nowMs, createdAtMs)
    val msg = when {
        updateCount <= 0 -> "Trace créée"
        editable && updateCount > 0 -> "Trace modifiée"
        else -> "Trace enregistrée"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.065f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = msg,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = WHITE_SOFT.copy(alpha = 0.94f),
            textAlign = TextAlign.Center
        )

        if (editable && updateCount > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ajustement enregistré",
                fontSize = 12.sp,
                color = WHITE_SOFT.copy(alpha = 0.80f)
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = hhmm(createdAtMs),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = WHITE_SOFT.copy(alpha = 0.62f)
        )

        if (updateCount > 0) {
            Spacer(Modifier.height(10.dp))
            Text("Modifiée ×$updateCount", fontSize = 12.sp, color = WHITE_SOFT.copy(alpha = 0.62f))
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Dernière modif : ${hhmm(updatedAtMs)}",
                fontSize = 12.sp,
                color = WHITE_SOFT.copy(alpha = 0.54f)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = if (editable) "Modifiable encore : ${timeLeftLabel(nowMs, createdAtMs)}"
            else "Trace figée (24 h écoulées)",
            fontSize = 12.sp,
            color = WHITE_SOFT.copy(alpha = if (editable) 0.56f else 0.50f)
        )
    }
}

@Composable
private fun PremiumSeparatorRow() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(6.dp))
        Divider(thickness = 1.dp, color = WHITE_SOFT.copy(alpha = 0.10f))
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Premium",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = MAUVE.copy(alpha = 0.80f)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "profondeur spatiale",
                fontSize = 12.5.sp,
                fontStyle = FontStyle.Italic,
                color = WHITE_SOFT.copy(alpha = 0.60f)
            )
        }
        Spacer(Modifier.height(6.dp))
    }
}

// ── Écran principal ──────────────────────────────────────────
@Composable
fun TraceDuJourScreen(
    percent: Int = 54,
    createdAtMs: Long = System.currentTimeMillis() - 15 * 60 * 1000L,
    updatedAtMs: Long = System.currentTimeMillis() - 5 * 60 * 1000L,
    updateCount: Int = 1,
    hasPremium: Boolean = false,
    hasPremiumPlus: Boolean = false,
    onSave: (Int, String, String?, String?, String?, Set<String>) -> Unit = { _, _, _, _, _, _ -> },
    onOpenTraceDuTempsVecu: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var note by remember { mutableStateOf("") }
    var etat   by remember { mutableStateOf<String?>(null) }
    var mental by remember { mutableStateOf<String?>(null) }
    var energie by remember { mutableStateOf<String?>(null) }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    // ✅ Corrigé : StateMap (sinon l’UI ne voit pas les updates)
    val tagAnimationTriggers = remember { mutableStateMapOf<String, Long>() }

    val toggleTag: (String) -> Unit = { tag ->
        selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
        tagAnimationTriggers[tag] = System.currentTimeMillis()
    }

    var target by remember { mutableStateOf(percent.toFloat().coerceIn(0f, 100f)) }
    val animPercent = remember { Animatable(target) }

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            nowMs = System.currentTimeMillis()
        }
    }

    LaunchedEffect(target) {
        animPercent.animateTo(target, tween(220, easing = FastOutSlowInEasing))
    }

    val p = animPercent.value.roundToInt().coerceIn(0, 100)
    val label = levelLabel(p)
    val dynColor = lerpColor(TURQUOISE, MAUVE, p / 100f)
    val sliderColor = lerpColor(MAUVE, TURQUOISE, p / 100f)

    val editable = isEditable(nowMs, createdAtMs)
    val canSave = editable && (note.isNotBlank() || selectedTags.isNotEmpty())

    var openIndex by remember { mutableStateOf<Int?>(null) }

    val firstPremiumIndex = remember {
        TAG_GROUPS_OFFICIAL.indexOfFirst { it.tier == Tier.PREMIUM }
            .let { if (it == -1) Int.MAX_VALUE else it }
    }

    // ✅ Branché : utilisé par le triangle pendant le slide
    var isInteracting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BG_DEEP,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.semantics { contentDescription = "Retour" }
                ) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = WHITE_SOFT.copy(alpha = 0.85f)
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        },
        bottomBar = {
            if (editable) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BG_DEEP)
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .border(
                                width = 1.5.dp,
                                color = if (canSave) MAUVE.copy(alpha = 0.85f) else MAUVE.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .background(Color.Transparent)
                            .clickable(enabled = canSave) {
                                onSave(p, note, etat, mental, energie, selectedTags)
                                onOpenTraceDuTempsVecu()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Approfondir la trace (optionnel) →",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (canSave) MAUVE.copy(alpha = 0.92f) else MAUVE.copy(alpha = 0.40f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(if (canSave) TURQUOISE else TURQUOISE.copy(alpha = 0.25f))
                            .clickable(enabled = canSave) {
                                onSave(p, note, etat, mental, energie, selectedTags)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Enregistrer",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = if (canSave) 1f else 0.55f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Annuler / Retour",
                        fontSize = 14.sp,
                        color = WHITE_SOFT.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onBack() }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BG_DEEP)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trace figée (24 h écoulées)",
                        fontSize = 14.sp,
                        color = WHITE_SOFT.copy(alpha = 0.55f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Text("Trace", fontSize = 60.sp, lineHeight = 60.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("D’état d’âme", fontSize = 52.sp, lineHeight = 52.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Le présent intérieur est noté.\nÀ travers quelques repères.",
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.72f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            TriangleOutlineBreathing(
                percent = p,
                isInteracting = isInteracting,
                modifier = Modifier.size(300.dp)
            )

            Spacer(Modifier.height(12.dp))
            PercentMessageBubble(p = p, dyn = dynColor)
            Spacer(Modifier.height(12.dp))

            Text(
                text = "$label • $p%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.90f)
            )

            Spacer(Modifier.height(10.dp))

            Slider(
                value = animPercent.value,
                onValueChange = { v ->
                    if (editable) {
                        isInteracting = true
                        target = v.coerceIn(0f, 100f)
                    }
                },
                onValueChangeFinished = {
                    if (editable) {
                        target = target.roundToInt().toFloat()
                        isInteracting = false
                    }
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = if (editable) sliderColor else Color.White.copy(alpha = 0.25f),
                    activeTrackColor = if (editable) sliderColor else Color.White.copy(alpha = 0.25f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.14f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            TraceStatusBlock(createdAtMs, updatedAtMs, updateCount, nowMs)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Écriture d'une trace",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = if (canSave) 1f else 0.55f)
            )

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = note,
                    onValueChange = { if (editable) note = it.take(120) },
                    singleLine = false,
                    enabled = editable,
                    textStyle = TextStyle(color = WHITE_SOFT, fontSize = 16.sp),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "${note.length}/120",
                    fontSize = 12.sp,
                    color = WHITE_SOFT.copy(alpha = 0.40f)
                )
            }

            Spacer(Modifier.height(20.dp))

            SelectedTagsSection(
                selectedTags = selectedTags,
                onRemove = { tag -> selectedTags = selectedTags - tag }
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Tags du jour",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = WHITE_SOFT.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            TAG_GROUPS_OFFICIAL.forEachIndexed { idx, cat ->
                if (idx == firstPremiumIndex) {
                    PremiumSeparatorRow()
                }

                val allowed = tierAllowed(cat.tier, hasPremium, hasPremiumPlus)
                val isOpen = openIndex == idx

                TagCategoryAccordion(
                    category = cat,
                    isOpen = isOpen,
                    onToggle = { openIndex = if (isOpen) null else idx },
                    allowed = allowed,
                    selectedTags = selectedTags,
                    onToggleTag = toggleTag,
                    animationTriggers = tagAnimationTriggers
                )

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Cette trace n'est pas un jugement. C'est une photographie du jour.\n" +
                        "Avec le temps, les traces révèlent des rythmes, sans jamais forcer une conclusion.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = WHITE_SOFT.copy(alpha = 0.50f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(72.dp))
        }
    }
}