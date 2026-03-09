package com.maxjth.tracememoire.ui.comprendre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.comprendre.blocs.ComprendreHeaderBloc
import com.maxjth.tracememoire.ui.comprendre.blocs.ComprendreSectionBloc
import com.maxjth.tracememoire.ui.comprendre.blocs.ComprendreButtonBloc
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun AstraScreen(
    onBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_DEEP)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextButton(onClick = onBack) {
                androidx.compose.material3.Text(
                    text = "Retour",
                    color = WHITE_SOFT
                )
            }

            Spacer(Modifier.height(12.dp))

            // HEADER
            ComprendreHeaderBloc()

            Spacer(Modifier.height(24.dp))

            // SECTION 1
            ComprendreSectionBloc(
                title = "Les cycles",
                description = "Trace Mémoire fonctionne avec quatre cycles : matin, jour, soir et nuit. Chaque cycle peut être enregistré pour construire la mémoire de ta journée."
            )

            // SECTION 2
            ComprendreSectionBloc(
                title = "Le soleil",
                description = "Le soleil représente ton score global de la journée. Il évolue selon les traces que tu ajoutes."
            )

            // SECTION 3
            ComprendreSectionBloc(
                title = "La lune",
                description = "La lune représente le nombre total de traces enregistrées dans ton historique."
            )

            Spacer(Modifier.height(24.dp))

            // BOUTON INFO
            ComprendreButtonBloc(
                onClick = { }
            )

            Spacer(Modifier.height(40.dp))

        }
    }
}