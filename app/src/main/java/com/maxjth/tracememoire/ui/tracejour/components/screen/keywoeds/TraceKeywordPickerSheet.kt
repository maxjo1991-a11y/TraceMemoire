// FILE: TraceKeywordPickerSheet.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.keywords

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TraceKeywordPickerSheet(
    visible: Boolean,
    title: String,
    words: List<String>,
    selectedWord: String?,
    onPick: (String) -> Unit,

    // ✅ NOUVEAU: mini note liée au slider
    note: String,
    onNoteChange: (String) -> Unit,

    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
) {
    if (!visible) return

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

            // ── Header
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

            // ✅ Note courte (optionnelle) — remplace la recherche
            OutlinedTextField(
                value = note,
                onValueChange = { new ->
                    // petit garde-fou: max 120 caractères
                    onNoteChange(new.take(120))
                },
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

            // ── Keywords (1 mot max → fermeture après choix)
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