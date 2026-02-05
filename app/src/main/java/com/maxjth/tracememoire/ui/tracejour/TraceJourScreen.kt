package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(onBack: () -> Unit) {

    var percent by remember { mutableStateOf(50) }
    var isInteracting by remember { mutableStateOf(false) }

    // ✅ Entrée triangle (très léger)
    val triEntry = remember { Animatable(0f) }

    // ✅ Entrée % (bounce doux) — arrive après un micro-delay
    val entryScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Triangle d’abord
        triEntry.snapTo(0f)
        triEntry.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.70f,
                stiffness = Spring.StiffnessLow
            )
        )

        // Puis le % (narratif)
        delay(180)

        entryScale.snapTo(0f)
        entryScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.62f,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // ✅ Gradient ultra léger (Trace Mémoire)
    val bgDeep = BG_SOFT
    val bgSlight = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            TopAppBar(
                title = { Text("Trace du jour") },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Text("Retour")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(bgDeep, bgSlight, bgDeep)
                    )
                )
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                TriangleOutlineBreathing(
                    percent = percent,
                    isInteracting = isInteracting,
                    modifier = Modifier
                        .size(260.dp)
                        // ✅ Micro-entrée : 0.92 -> 1.0 (subtil)
                        .scale(0.92f + (0.08f * triEntry.value))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WHITE_SOFT,
                    // ✅ % arrive après (bounce)
                    modifier = Modifier.scale(entryScale.value)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = percent.toFloat(),
                    onValueChange = {
                        percent = it.toInt()
                        isInteracting = true
                    },
                    onValueChangeFinished = { isInteracting = false },
                    valueRange = 0f..100f
                )
            }
        }
    }
}