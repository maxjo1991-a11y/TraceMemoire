package com.maxjth.tracememoire.ui.tracejour.components.screen.deepen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomDeepenBar(
    isCycleLocked: Boolean,
    onDeepen: () -> Unit,
    modifier: Modifier = Modifier
) {
    DeePenMemoryButton(
        text = "Approfondir la mémoire →",

        // ✅ Logique propre
        // Si tu veux VRAIMENT toujours cliquable → laisse true
        // Si tu veux bloquer physiquement → utilise !isCycleLocked
        enabled = true,

        onClick = {
            // ✅ Défensif / lisible / stable
            onDeepen()
        },

        modifier = modifier
            .fillMaxWidth()
            .padding(
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
            )
    )
}