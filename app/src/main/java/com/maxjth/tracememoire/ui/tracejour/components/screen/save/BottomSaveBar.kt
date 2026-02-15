package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.maxjth.tracememoire.ui.theme.MAUVE

@Composable
fun BottomSaveBar(
    state: TraceSaveState,
    enabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canSave = state.canSave(enabled)

    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        color = Color.Black.copy(alpha = 0.92f),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onSave,
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MAUVE.copy(alpha = if (canSave) 1f else 0.35f),
                    disabledContainerColor = MAUVE.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(72.dp)
            ) {
                Text(
                    text = "ENREGISTRER",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}