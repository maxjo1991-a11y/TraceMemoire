
package com.maxjth.tracememoire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloat

// ✅ Tes écrans UI (déjà dans d'autres fichiers)
import com.maxjth.tracememoire.ui.tracejour.TraceDuJourScreen

// ================================
// CHAMBRE 1 — NAVIGATION MINIMALE
// Rôle : le “switch” entre Écran 1 / 2 / 3
// ================================
private enum class Screen { HOME, TRACE_DU_JOUR, TRACE_DU_TEMPS_VECU }

// ================================
// CHAMBRE 2 — COULEURS + CONSTANTES (globales)
// Rôle : rien d’autre ici que des constantes
// ================================
private val BG_DEEP = Color(0xFF000000)
private val WHITE_SOFT = Color(0xFFE9E9E9)
private val TEXT_MUTED = Color(0xFFAAAAAA)
private val TURQUOISE = Color(0xFF00A3A3)
private val MAUVE = Color(0xFF7A63C6)

private const val WAVE_MS = 720
private const val GLOW_MAX_ALPHA = 0.26f
private const val RING_MAX_ALPHA = 0.36f
private val LIQUID_EASING = CubicBezierEasing(0.22f, 0.98f, 0.28f, 1.00f)

// ================================
// CHAMBRE 3 — MAIN ACTIVITY
// Rôle : point d’entrée Android
// ================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

// ================================
// CHAMBRE 4 — APP ROOT (pilote)
// Rôle : affiche le bon écran (1/2/3)
// ================================
@Composable
private fun AppRoot() {
    var screen by remember { mutableStateOf(Screen.HOME) }

    when (screen) {

        Screen.HOME -> {
            HomeScreen(
                traceCount = 0,
                lastTraceText = "Il y a 2 jours",
                onOpenTraceDuJour = { screen = Screen.TRACE_DU_JOUR }
            )
        }

        Screen.TRACE_DU_JOUR -> {
            // ✅ Appel minimal pour compiler (on enlève onSave / onBack / onOpen...)
            com.maxjth.tracememoire.ui.tracejour.TraceDuJourScreen(
                percent = 54
            )
        }

        Screen.TRACE_DU_TEMPS_VECU -> {
            // ✅ Temporaire : on ne branche pas Écran 3 tant que l’import est rouge
            Text("Trace du temps vécu (bientôt)")
        }
    }
}
@Composable
private fun HomeScreen(
    traceCount: Int,
    lastTraceText: String,
    onOpenTraceDuJour: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val showLastToast = (traceCount > 0)

    Scaffold(containerColor = BG_DEEP) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titre
            Text(
                text = "Trace\nMémoire",
                fontSize = 60.sp,
                lineHeight = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // Sous-titre
            Text(
                text = "Ici, la mémoire se construit avec le temps.",
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.lerp(
                    Color.White,
                    Color(0xFF8E7CFF),
                    0.12f
                ).copy(alpha = 0.72f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(22.dp))

            // Cercle vivant
            HomeMemoryCircle(traceCount = traceCount)

            if (traceCount == 0) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Quelque chose est en train de commencer",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = androidx.compose.ui.graphics.lerp(
                        Color.White,
                        Color(0xFF8E7CFF),
                        0.12f
                    ).copy(alpha = 0.72f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (traceCount > 0 && traceCount % 11 == 0) {
                Spacer(Modifier.height(12.dp))
                MessageSymbolique11Moelleux(traceCount = traceCount)
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = buildAnnotatedString {
                    append("Chaque trace fait grandir ce ")
                    withStyle(style = SpanStyle(color = MAUVE)) { append("Cercle") }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = WHITE_SOFT.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(26.dp))

            // Bouton (ouvre Écran 2)
            AddTraceButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenTraceDuJour()
                }
            )

            Spacer(Modifier.height(18.dp))

            // Historique (placeholder)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { /* TODO Historique plus tard */ }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Historique",
                    tint = TURQUOISE.copy(alpha = 0.65f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Historique",
                    fontSize = 20.sp,
                    color = TEXT_MUTED,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

            // Toast dernière entrée
            LastTraceToast(
                lastTraceText = lastTraceText,
                show = showLastToast,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

// ✅ CUBE 2 à coller juste après ce cube 1 (voir plus bas)


// ================================
// CHAMBRE 6 — MESSAGE SYMBOLIQUE 11
// Rôle : petit encart doux quand traceCount % 11 == 0
// ================================
@Composable
private fun MessageSymbolique11Moelleux(traceCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .padding(vertical = 10.dp, horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "11",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MAUVE.copy(alpha = 0.78f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "C’est ici que tout commence.",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MAUVE.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ================================
// CHAMBRE 7 — CERCLE VIVANT (HOME)
// Rôle : cercle mauve + chiffre turquoise au centre
// ================================
@Composable
private fun HomeMemoryCircle(traceCount: Int) {
    var showExact by remember { mutableStateOf(false) }

    LaunchedEffect(showExact) {
        if (showExact) {
            delay(2500)
            showExact = false
        }
    }

    val displayText = when {
        traceCount <= 99 -> traceCount.toString()
        showExact -> traceCount.toString()
        else -> "99+"
    }

    val numberSize = when (displayText.length) {
        1 -> 120.sp
        2 -> 106.sp
        3 -> 96.sp
        else -> 86.sp
    }

    val numberOffsetY = when (displayText.length) {
        1 -> (-6).dp
        2 -> (-5).dp
        3 -> (-4).dp
        else -> (-4).dp
    }

    val pulse = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            pulse.animateTo(
                targetValue = 0.96f,
                animationSpec = tween(durationMillis = 650, easing = LinearEasing)
            )
            pulse.animateTo(
                targetValue = 1.08f,
                animationSpec = tween(durationMillis = 2950, easing = FastOutSlowInEasing)
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "homeCircle")

    val strokeBreath by transition.animateFloat(
        initialValue = 17f,
        targetValue = 23f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strokeBreath"
    )

    val haloOuter by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloOuter"
    )

    val haloInner by transition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.36f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloInner"
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .graphicsLayer(scaleX = pulse.value, scaleY = pulse.value)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (traceCount > 99) showExact = true
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val d = size.minDimension
            val topLeft = Offset(
                (size.width - d) / 2f,
                (size.height - d) / 2f
            )

            drawArc(
                color = MAUVE.copy(alpha = haloOuter),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(d, d),
                style = Stroke(width = strokeBreath + 12f)
            )

            drawArc(
                color = MAUVE.copy(alpha = haloInner),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(d, d),
                style = Stroke(width = strokeBreath + 6f)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        MAUVE,
                        Color.White.copy(alpha = 0.18f),
                        MAUVE
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(d, d),
                style = Stroke(width = strokeBreath)
            )
        }

        Text(
            text = displayText,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            color = TURQUOISE,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = numberOffsetY)
        )
    }
}

// ================================
// CHAMBRE 8 — BOUTON “AJOUTER UNE TRACE”
// Rôle : bouton turquoise + vague mauve (press)
// ================================
@Composable
private fun AddTraceButton(
    onClick: () -> Unit,
    text: String = "Ajouter une trace"
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val wave by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(durationMillis = WAVE_MS, easing = LIQUID_EASING),
        label = "wave"
    )

    var peakSent by remember { mutableStateOf(false) }
    LaunchedEffect(pressed) {
        if (!pressed) {
            peakSent = false
        } else if (!peakSent) {
            peakSent = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val glowAlpha = GLOW_MAX_ALPHA * wave
    val ringAlpha = RING_MAX_ALPHA * wave

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(TURQUOISE)
            .drawWithContent {
                drawContent()
                if (wave > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(MAUVE.copy(alpha = glowAlpha), Color.Transparent),
                            center = center,
                            radius = size.minDimension * (0.55f + 0.80f * wave)
                        ),
                        radius = size.minDimension * (0.55f + 0.80f * wave),
                        center = center
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(MAUVE.copy(alpha = ringAlpha), Color.Transparent),
                            center = center,
                            radius = size.minDimension * (0.25f + 0.60f * wave)
                        ),
                        radius = size.minDimension * (0.25f + 0.60f * wave),
                        center = center
                    )
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

// ================================
// CHAMBRE 9 — TOAST “DERNIÈRE ENTRÉE”
// Rôle : petite info qui disparaît après 6 sec
// ================================
@Composable
private fun LastTraceToast(
    lastTraceText: String,
    show: Boolean,
    modifier: Modifier = Modifier
) {
    if (!show) return

    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(6000); visible = false }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(420),
        label = "lastTraceToastAlpha"
    )

    Text(
        text = "Dernière entrée :\n$lastTraceText",
        fontSize = 13.sp,
        color = WHITE_SOFT.copy(alpha = 0.62f * alpha),
        textAlign = TextAlign.Center,
        modifier = modifier.graphicsLayer(alpha = alpha)
    )
}