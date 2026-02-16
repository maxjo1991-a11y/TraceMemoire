package com.maxjth.tracememoire.ui.deepen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun TraceDeepenScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {

        // CONTENU (laisse de la place pour la pastille flottante)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 92.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Trace Deepen Screen",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // PASTILLE FLOTTANTE (overlay propre, pas de “bloc noir” autour)
        BottomDeepenBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            onClick = { /* TODO */ },
            enabled = true
        )
    }
}