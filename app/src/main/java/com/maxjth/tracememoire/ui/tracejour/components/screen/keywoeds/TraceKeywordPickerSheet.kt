// FILE: TraceKeywordPickerSheet.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.keywords

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TraceKeywordPickerSheet(
    visible: Boolean,
    title: String,
    sliderKey: String,

    // ✅ Mots-clés
    words: List<String>,
    selectedWord: String?,
    onPick: (String) -> Unit,

    // ✅ Phrases
    isPremiumUser: Boolean,
    onPickPhrase: (String) -> Unit,

    // ✅ Note liée au slider
    note: String,
    onNoteChange: (String) -> Unit,

    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
) {
    if (!visible) return

    val freePhrases = TracePhrasesData.phrasesForSlider(sliderKey, isPremium = false)
    val premiumPhrases = TracePhrasesData.phrasesForSlider(sliderKey, isPremium = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = BG_SOFT,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {

            Text(
                text = "Choisir un mot",
                color = WHITE_SOFT.copy(alpha = 0.92f),
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

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { new -> onNoteChange(new.take(120)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Note (optionnelle)…",
                        color = WHITE_SOFT.copy(alpha = 0.38f),
                        fontSize = 14.sp
                    )
                },
                singleLine = false,
                minLines = 2,
                maxLines = 4,
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

            Spacer(Modifier.height(12.dp))

            // ✅ Phrases gratuit
            Text(
                text = "Phrases (gratuit)",
                color = WHITE_SOFT.copy(alpha = 0.72f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (freePhrases.isEmpty()) {
                    Text(
                        text = "Aucune phrase.",
                        color = WHITE_SOFT.copy(alpha = 0.42f),
                        fontSize = 14.sp
                    )
                } else {
                    freePhrases.forEach { p ->
                        TracePhraseChip(
                            label = p,
                            enabled = true,
                            onClick = {
                                onPickPhrase(p)
                                onNoteChange(p.take(120))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(Modifier.height(14.dp))

            // ✅ Phrases premium
            RowHeaderPremium(isPremiumUser = isPremiumUser)

            Spacer(Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (premiumPhrases.isEmpty()) {
                    Text(
                        text = "Aucune phrase.",
                        color = WHITE_SOFT.copy(alpha = 0.42f),
                        fontSize = 14.sp
                    )
                } else {
                    premiumPhrases.forEach { p ->
                        TracePhraseChip(
                            label = p,
                            enabled = isPremiumUser,
                            locked = !isPremiumUser,
                            onClick = {
                                if (!isPremiumUser) return@TracePhraseChip
                                onPickPhrase(p)
                                onNoteChange(p.take(120))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ✅ Mots-clés (toujours en bas)
            Text(
                text = "Mots-clés",
                color = WHITE_SOFT.copy(alpha = 0.72f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (words.isEmpty()) {
                    Text(
                        text = "Aucun mot.",
                        color = WHITE_SOFT.copy(alpha = 0.42f),
                        fontSize = 14.sp
                    )
                } else {
                    words.forEach { word ->
                        TraceKeywordChip(
                            label = word,
                            selected = (word == selectedWord),
                            onClick = {
                                onPick(word)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun RowHeaderPremium(isPremiumUser: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Phrases (premium)",
            color = MAUVE.copy(alpha = 0.92f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = if (isPremiumUser) "Déverrouillées" else "Verrouillées",
            color = WHITE_SOFT.copy(alpha = if (isPremiumUser) 0.55f else 0.42f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TracePhraseChip(
    label: String,
    enabled: Boolean,
    locked: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)

    Column(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.55f)
            .background(BG_SOFT.copy(alpha = 0.16f), shape)
            .border(
                width = 1.dp,
                color = if (locked) Color.White.copy(alpha = 0.10f) else MAUVE.copy(alpha = 0.18f),
                shape = shape
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (locked) "🔒 $label" else label,
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.86f else 0.62f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}