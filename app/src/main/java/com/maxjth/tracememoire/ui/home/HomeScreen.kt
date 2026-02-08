package com.maxjth.tracememoire.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.HomeMemoryCircle
import com.maxjth.tracememoire.ui.logic.HomeMessages
import com.maxjth.tracememoire.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val traceCount = 0
    val isEmpty = traceCount == 0

    // ─────────────────────────────
    // Sous-titre annuel (2026 → 2030)
    // ─────────────────────────────
    val year = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val subtitle = remember(year) { homeSubtitleForYear(year) }

    // ─────────────────────────────
    // Bouton principal – interaction
    // ─────────────────────────────
    val btnInteraction = remember { MutableInteractionSource() }
    val pressed by btnInteraction.collectIsPressedAsState()

    val btnScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(125),
        label = "btnScale"
    )

    val btnAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(125),
        label = "btnAlpha"
    )

    // ─────────────────────────────
    // Message narratif (delay)
    // ─────────────────────────────
    var showMsg by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(180)
        showMsg = true
    }

    val msgAlpha by animateFloatAsState(
        targetValue = if (showMsg) 1f else 0f,
        animationSpec = tween(260),
        label = "msgAlpha"
    )

    // ─────────────────────────────
    // Cercle – animation d’entrée
    // ─────────────────────────────
    val circleEntry = remember { Animatable(0f) }
    LaunchedEffect(isEmpty) {
        if (isEmpty) {
            circleEntry.snapTo(0f)
            circleEntry.animateTo(
                1f,
                spring(
                    dampingRatio = 0.62f,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            circleEntry.snapTo(1f)
        }
    }

    val bgDeep = BG_DEEP
    val bgSlight = BG_SOFT.copy(alpha = 0.92f)

    Scaffold(
        containerColor = bgDeep,

        // ─────────────────────────────
        // PASTILLE HISTORIQUE (turquoise)
        // ─────────────────────────────
        bottomBar = {
            val historyInteraction = remember { MutableInteractionSource() }
            val historyPressed by historyInteraction.collectIsPressedAsState()

            val historyScale by animateFloatAsState(
                targetValue = if (historyPressed) 0.98f else 1f,
                animationSpec = tween(durationMillis = 140),
                label = "historyScale"
            )

            val historyGlowAlpha by animateFloatAsState(
                targetValue = if (historyPressed) 0.22f else 0f,
                animationSpec = tween(durationMillis = 160),
                label = "historyGlow"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow turquoise (press seulement)
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = historyScale
                            scaleY = historyScale
                        }
                        .background(
                            color = TURQUOISE.copy(alpha = historyGlowAlpha),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(1.dp)
                ) {
                    TextButton(
                        onClick = onOpenHistory,
                        interactionSource = historyInteraction,
                        modifier = Modifier
                            .height(44.dp)
                            .graphicsLayer {
                                scaleX = historyScale
                                scaleY = historyScale
                            }
                            .border(
                                width = 1.dp,
                                color = MAUVE.copy(alpha = 0.28f),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = TURQUOISE.copy(alpha = if (isEmpty) 0.70f else 0.95f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Historique",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Historique",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(bgDeep, bgSlight, bgDeep)
                    )
                )
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Trace\nMémoire",
                    fontSize = 61.sp,
                    lineHeight = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                // ✅ MAJ: sous-texte du haut MONTE (14.dp -> 6.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // ✅ Phrase annuelle (mystère)
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = WHITE_MAUVE.copy(alpha = 0.82f)
                )

                // ✅ MAJ: on redonne un poil d’air avant le cercle (28.dp -> 30.dp)
                Spacer(modifier = Modifier.height(30.dp))

                HomeMemoryCircle(
                    traceCount = traceCount,
                    modifier = Modifier
                        .size(300.dp)
                        .graphicsLayer {
                            val s = circleEntry.value
                            scaleX = 0.92f + (0.14f * s)
                            scaleY = 0.92f + (0.14f * s)
                        }
                )

                // ✅ MAJ: sous-texte du bas DESCEND (30.dp -> 42.dp)
                Spacer(modifier = Modifier.height(42.dp))

                Text(
                    modifier = Modifier
                        .alpha(msgAlpha)
                        // ✅ MAJ: on enlève la poussée vers le bas du texte (top=2) pour ne pas contredire le “descendre”
                        .padding(top = 0.dp),
                    text = HomeMessages.messageForTraceCount(traceCount),
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = WHITE_MAUVE.copy(alpha = 0.74f)
                )

                Spacer(modifier = Modifier.height(42.dp))

                // Bouton principal – Ajouter
                Button(
                    onClick = onAddTrace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .graphicsLayer {
                            scaleX = btnScale
                            scaleY = btnScale
                        }
                        .alpha(btnAlpha)
                        .border(
                            1.dp,
                            MAUVE.copy(alpha = 0.35f),
                            RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    interactionSource = btnInteraction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MAUVE.copy(alpha = 0.14f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Ajouter une Mémoire",
                        modifier = Modifier.size(21.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ajouter une Mémoire",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

// ─────────────────────────────
// Phrases annuelles (2026 → 2030)
// ─────────────────────────────
private fun homeSubtitleForYear(year: Int): String {
    return when (year) {
        2026 -> "Le temps fait la mémoire."
        2027 -> "Ce qui revient laisse une trace."
        2028 -> "La mémoire révèle ce qui insistait."
        2029 -> "Ce qui a été noté ne disparaît plus."
        2030 -> "Le temps n’oublie pas. Il assemble."
        else -> "Le temps fait la mémoire."
    }
}