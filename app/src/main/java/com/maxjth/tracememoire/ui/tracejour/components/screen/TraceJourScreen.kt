// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourScreen.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit
) {
    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = { /* volontairement vide */ },

                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = BG_SOFT.copy(alpha = 0.18f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.5.dp,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .border(
                                width = 1.dp,
                                color = TURQUOISE.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(999.dp)
                            )
                    ) {
                        TextButton(
                            onClick = onBack,
                            interactionSource = remember { MutableInteractionSource() },
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 4.dp // ← pastille fine
                            )
                        ) {
                            Text(
                                text = "← En arrière",
                                color = TURQUOISE.copy(alpha = 0.8f),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bg
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(bg, bgSoft, bg)
                    )
                )
                .padding(padding)
        ) {
            // Titre uniquement (Trace / état d’âme)
            TraceJourTitleBlock()
        }
    }
}