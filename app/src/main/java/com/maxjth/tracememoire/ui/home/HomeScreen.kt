package com.maxjth.tracememoire.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Scaffold(
        containerColor = BG_DEEP
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // Titre
            Text(
                text = "Trace\nMémoire",
                fontSize = 48.sp,
                lineHeight = 52.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sous-texte
            Text(
                text = "Ici, la mémoire se construit avec le temps.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Placeholder cercle (temporaire)
            Box(
                modifier = Modifier
                    .size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0",
                    fontSize = 48.sp,
                    color = TURQUOISE
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Bouton Ajouter une trace
            Button(
                onClick = onAddTrace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ajouter une trace")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton Historique
            TextButton(
                onClick = onOpenHistory
            ) {
                Text("Historique")
            }
        }
    }
}
