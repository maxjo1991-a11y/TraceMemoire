package com.maxjth.tracememoire.ui.planetes.orion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun OrionScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_DEEP)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            TextButton(onClick = onBack) {
                Text("Retour", color = WHITE_SOFT)
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Orion",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MAUVE
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Écran prêt. Le contenu viendra après.",
                color = WHITE_SOFT
            )
        }
    }
}