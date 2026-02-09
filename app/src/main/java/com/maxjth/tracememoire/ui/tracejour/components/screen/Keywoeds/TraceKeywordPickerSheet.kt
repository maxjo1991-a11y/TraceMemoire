// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/keywords/TraceKeywordPickerSheet.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.keywords

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

import androidx.compose.foundation.layout.windowInsetsBottomHeight
/**
 * ✅ Sheet mots-clés (calme, premium-friendly, sans jugement)
 *
 * - Affiche des catégories (onglets) + chips cliquables
 * - Recherche locale
 * - Zone "Sélection" (chips)
 * - Boutons: Effacer / Valider / Fermer
 *
 * Tu peux l’appeler depuis un slider:
 * TraceKeywordPickerSheet(
 *   visible = showSheet,
 *   title = "Humeur globale",
 *   categories = TraceKeywordData.defaultForSlider("humeur", isPremium),
 *   selected = selectedKeywords,
 *   onToggle = { kw -> ... },
 *   onClear = { ... },
 *   onDismiss = { showSheet = false },
 *   onValidate = { showSheet = false }
 * )
 */

data class KeywordItem(
    val id: String,
    val label: String,
    val isPremiumOnly: Boolean = false
)

data class KeywordCategory(
    val id: String,
    val title: String,
    val items: List<KeywordItem>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TraceKeywordPickerSheet(
    visible: Boolean,
    title: String,
    categories: List<KeywordCategory>,
    selected: Set<String>,
    onToggle: (KeywordItem) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    isPremium: Boolean = false
) {
    if (!visible) return

    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    var query by rememberSaveable { mutableStateOf("") }
    var activeCatId by rememberSaveable(categories) {
        mutableStateOf(categories.firstOrNull()?.id ?: "")
    }

    val activeCat = remember(activeCatId, categories) {
        categories.firstOrNull { it.id == activeCatId } ?: categories.firstOrNull()
    }

    val filteredItems by remember(query, activeCat, isPremium) {
        derivedStateOf {
            val base = activeCat?.items.orEmpty()
            val allowed = base.filter { !it.isPremiumOnly || isPremium }
            if (query.isBlank()) allowed
            else {
                val q = query.trim().lowercase()
                allowed.filter { it.label.lowercase().contains(q) }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = bg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(bg, bgSoft, bg))
                )
                .imePadding()
                .padding(bottom = 12.dp)
        ) {
            // ─────────────────────────────────────────
            // Header
            // ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mots-clés",
                        color = WHITE_SOFT.copy(alpha = 0.96f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = title,
                        color = WHITE_SOFT.copy(alpha = 0.55f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Fermer",
                        color = TURQUOISE.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ─────────────────────────────────────────
            // Search
            // ─────────────────────────────────────────
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                placeholder = {
                    Text(
                        text = "Rechercher un mot-clé…",
                        color = WHITE_SOFT.copy(alpha = 0.38f),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TURQUOISE.copy(alpha = 0.35f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
                    focusedTextColor = WHITE_SOFT.copy(alpha = 0.94f),
                    unfocusedTextColor = WHITE_SOFT.copy(alpha = 0.88f),
                    cursorColor = TURQUOISE.copy(alpha = 0.8f),
                    focusedContainerColor = BG_SOFT.copy(alpha = 0.18f),
                    unfocusedContainerColor = BG_SOFT.copy(alpha = 0.14f)
                )
            )

            Spacer(Modifier.height(14.dp))

            // ─────────────────────────────────────────
            // Categories tabs (chips)
            // ─────────────────────────────────────────
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { cat ->
                    val active = cat.id == activeCatId
                    CategoryChip(
                        text = cat.title,
                        active = active,
                        onClick = {
                            activeCatId = cat.id
                            query = ""
                        }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(Modifier.height(14.dp))

            // ─────────────────────────────────────────
            // Selected area
            // ─────────────────────────────────────────
            if (selected.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sélection",
                            color = WHITE_SOFT.copy(alpha = 0.72f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = onClear) {
                            Text(
                                text = "Effacer",
                                color = MAUVE.copy(alpha = 0.82f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // on affiche les labels via lookup dans categories
                        val lookup = remember(categories) {
                            categories
                                .flatMap { it.items }
                                .associateBy { it.id }
                        }
                        selected.forEach { id ->
                            val item = lookup[id] ?: KeywordItem(id = id, label = id)
                            KeywordChip(
                                text = item.label,
                                selected = true,
                                onClick = { onToggle(item) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                Spacer(Modifier.height(14.dp))
            }

            // ─────────────────────────────────────────
            // Keywords grid
            // ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Text(
                    text = activeCat?.title ?: "",
                    color = WHITE_SOFT.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredItems.isEmpty()) {
                        Text(
                            text = "Aucun mot-clé.",
                            color = WHITE_SOFT.copy(alpha = 0.42f),
                            fontSize = 14.sp
                        )
                    } else {
                        filteredItems.forEach { item ->
                            val isSelected = selected.contains(item.id)
                            KeywordChip(
                                text = item.label,
                                selected = isSelected,
                                onClick = { onToggle(item) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ─────────────────────────────────────────
            // Footer actions
            // ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selected.size} sélectionné(s)",
                    color = WHITE_SOFT.copy(alpha = 0.55f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = onValidate) {
                    Text(
                        text = "Valider",
                        color = TURQUOISE.copy(alpha = 0.92f),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)
    val border = if (active) TURQUOISE.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.10f)
    val bg = if (active) TURQUOISE.copy(alpha = 0.14f) else BG_SOFT.copy(alpha = 0.14f)
    val fg = if (active) WHITE_SOFT.copy(alpha = 0.92f) else WHITE_SOFT.copy(alpha = 0.70f)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, border, shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun KeywordChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)

    val edge = if (selected) TURQUOISE.copy(alpha = 0.42f) else Color.White.copy(alpha = 0.10f)
    val bg = if (selected) {
        Brush.horizontalGradient(
            listOf(
                TURQUOISE.copy(alpha = 0.14f),
                BG_SOFT.copy(alpha = 0.18f),
                MAUVE.copy(alpha = 0.12f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                MAUVE.copy(alpha = 0.10f),
                BG_SOFT.copy(alpha = 0.16f),
                MAUVE.copy(alpha = 0.10f)
            )
        )
    }

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, edge, shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = WHITE_SOFT.copy(alpha = if (selected) 0.95f else 0.78f),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Données par défaut (tu pourras remplacer par ta vraie source ensuite).
 * Le but: avoir quelque chose de branchable immédiatement.
 */
object TraceKeywordData {

    fun defaultForSlider(sliderKey: String, isPremium: Boolean): List<KeywordCategory> {
        // Tu peux adapter selon sliderKey (humeur/energie/...)
        // Ici: set global simple, cohérent, neutre.
        val base = listOf(
            KeywordCategory(
                id = "general",
                title = "Général",
                items = listOf(
                    KeywordItem("stable", "Stable"),
                    KeywordItem("changeant", "Changeant"),
                    KeywordItem("dense", "Dense"),
                    KeywordItem("leger", "Léger"),
                    KeywordItem("lucide", "Lucide"),
                    KeywordItem("flou", "Flou")
                )
            ),
            KeywordCategory(
                id = "rythme",
                title = "Rythme",
                items = listOf(
                    KeywordItem("lent", "Lent"),
                    KeywordItem("normal", "Normal"),
                    KeywordItem("rapide", "Rapide"),
                    KeywordItem("hache", "Haché"),
                    KeywordItem("continu", "Continu")
                )
            ),
            KeywordCategory(
                id = "corps",
                title = "Corps",
                items = listOf(
                    KeywordItem("tendu", "Tendu"),
                    KeywordItem("relache", "Relâché"),
                    KeywordItem("lourd", "Lourd"),
                    KeywordItem("leger_corps", "Léger"),
                    KeywordItem("sensibile", "Sensible")
                )
            ),
            KeywordCategory(
                id = "premium",
                title = "Premium",
                items = listOf(
                    KeywordItem("typejour_ok", "Type de journée (mot)", isPremiumOnly = true),
                    KeywordItem("motif_ok", "Motif (mot)", isPremiumOnly = true),
                    KeywordItem("charge_ok", "Charge (mot)", isPremiumOnly = true)
                )
            )
        )

        // Si pas premium, on garde la catégorie mais ses items premium seront filtrés à l’affichage.
        // (ça laisse une structure claire)
        return base
    }
}

