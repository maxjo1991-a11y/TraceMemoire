package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tracejour.components.screen.buttons.TracePrimaryPillButton

@Composable
fun BottomSaveBar(
    state: TraceSaveState,
    enabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canSave = state.canSave(enabled)

    // ✅ Pas de Surface = pas d’effet “barre noire”
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        TracePrimaryPillButton(
            text = "ENREGISTRER",
            enabled = canSave,
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        )
    }
}