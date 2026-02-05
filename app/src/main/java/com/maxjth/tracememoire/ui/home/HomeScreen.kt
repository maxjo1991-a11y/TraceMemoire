package com.maxjth.tracememoire.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.HomeMemoryCircle
import com.maxjth.tracememoire.ui.logic.HomeMessages
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val traceCount = 0
    val isEmpty = traceCount == 0

    // ✅ Interaction source pour micro-anim press (safe)
    val btnInteraction = remember { MutableInteractionSource() }
    val pressed by btnInteraction.collectIsPressedAsState()

    val btnScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(durationMillis = 125),
        label = "btnScale"
    )

    val btnAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 125),
        label = "btnAlpha"
    )

    // ✅ Micro fade-in du texte (au chargement)
    var showMsg by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showMsg = true }

    val msgAlpha by animateFloatAsState(
        targetValue = if (showMsg) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "msgAlpha"
    )

    Scaffold(
        containerColor = BG_DEEP,

        // ✅ Historique ANCRÉ en bas (donc visible tout le temps)
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp, top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onOpenHistory) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Historique",
                        tint = TURQUOISE.copy(alpha = if (isEmpty) 0.75f else 0.95f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historique",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TURQUOISE.copy(alpha = if (isEmpty) 0.85f else 1f)
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // ✅ IMPORTANT: ça réserve la place du bottomBar
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(52.dp))

            Text(
                text = "Trace\nMémoire",
                fontSize = 59.sp,
                lineHeight = 57.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Ici, la mémoire se construit avec le temps.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.62f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            HomeMemoryCircle(
                traceCount = traceCount,
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                modifier = Modifier.alpha(msgAlpha),
                text = HomeMessages.messageForTraceCount(traceCount),
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                // ✅ un peu plus lisible que 0.45
                color = Color.White.copy(alpha = 0.58f)
            )

            // ✅ + d'air entre message et bouton
            Spacer(modifier = Modifier.height(42.dp))

            Button(
                onClick = onAddTrace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .graphicsLayer {
                        scaleX = btnScale
                        scaleY = btnScale
                    }
                    .alpha(btnAlpha),
                shape = RoundedCornerShape(32.dp), // ✅ encore + "safe"
                interactionSource = btnInteraction
            ) {
                Text(
                    text = "Ajouter une trace",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ✅ Petit coussin avant le bottomBar (sans le pousser hors écran)
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}