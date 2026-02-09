// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/keywords/TraceKeywordPickerSheet.kt
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    // ✅ stable
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var query by rememberSaveable { mutableStateOf("") }

    val filtered = remember(words, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) words
        else words.filter { it.lowercase().contains(q) }
    }

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
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Rechercher…",
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

            Spacer(Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filtered.isEmpty()) {
                    Text(
                        text = "Aucun mot.",
                        color = WHITE_SOFT.copy(alpha = 0.42f),
                        fontSize = 14.sp
                    )
                } else {
                    filtered.forEach { word ->
                        TraceKeywordChip(
                            label = word,
                            selected = (word == selectedWord),
                            enabled = true,
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