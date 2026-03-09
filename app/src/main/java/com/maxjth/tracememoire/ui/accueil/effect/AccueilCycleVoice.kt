// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/cycle/AccueilCycleVoice.kt
package com.maxjth.tracememoire.ui.accueil.cycle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import java.time.LocalDateTime

/**
 * AccueilCycleVoice
 *
 * Objectif: donner la "voix" entre la pastille (empreinte) et le rectangle.
 *
 * États verrouillés:
 * 1) "Nouveau cycle disponible"
 * 2) "Cycle en cours"
 * 3) "Cycle complété"
 *
 * Couleurs verrouillées:
 * État 1 → blanc doux
 * État 2 → blanc doux + très léger reflet turquoise
 * État 3 → blanc doux + très léger reflet mauve
 */

/* ---------------------------
   MODELE
---------------------------- */

enum class AccueilCycleVoiceState {
    ETAT_1_NOUVEAU_CYCLE,
    ETAT_2_EN_COURS,
    ETAT_3_COMPLETE
}

@Immutable
data class AccueilCycleVoiceUi(
    val state: AccueilCycleVoiceState,
    val text: String
)

/* ---------------------------
   LOGIQUE (PURE)
---------------------------- */

fun computeAccueilCycleVoiceState(
    now: LocalDateTime = LocalDateTime.now(),
    doneMatin: Boolean,
    doneJour: Boolean,
    doneSoir: Boolean,
    doneNuit: Boolean
): AccueilCycleVoiceUi {

    val allDone = doneMatin && doneJour && doneSoir && doneNuit
    if (allDone) {
        return AccueilCycleVoiceUi(
            state = AccueilCycleVoiceState.ETAT_3_COMPLETE,
            text = "Cycle complété"
        )
    }

    val currentCycle = currentCycleKey(now)

    val currentDone = when (currentCycle) {
        "MATIN" -> doneMatin
        "JOUR" -> doneJour
        "SOIR" -> doneSoir
        "NUIT" -> doneNuit
        else -> false
    }

    // Règle simple et stable:
    // - si le cycle courant n'est pas fait -> "Nouveau cycle disponible"
    // - sinon -> "Cycle en cours" (la journée continue, mais ton cycle actuel est déjà verrouillé)
    return if (!currentDone) {
        AccueilCycleVoiceUi(
            state = AccueilCycleVoiceState.ETAT_1_NOUVEAU_CYCLE,
            text = "Nouveau cycle disponible"
        )
    } else {
        AccueilCycleVoiceUi(
            state = AccueilCycleVoiceState.ETAT_2_EN_COURS,
            text = "Cycle en cours"
        )
    }
}

/**
 * Découpage journée (simple, modifiable si tu veux):
 * 06:00-11:59 = MATIN
 * 12:00-17:59 = JOUR
 * 18:00-23:59 = SOIR
 * 00:00-05:59 = NUIT
 */
private fun currentCycleKey(now: LocalDateTime): String {
    val h = now.hour
    return when (h) {
        in 6..11 -> "MATIN"
        in 12..17 -> "JOUR"
        in 18..23 -> "SOIR"
        else -> "NUIT"
    }
}

/* ---------------------------
   UI (COMPOSABLE)
---------------------------- */

@Composable
fun AccueilCycleVoice(
    doneMatin: Boolean,
    doneJour: Boolean,
    doneSoir: Boolean,
    doneNuit: Boolean,
    modifier: Modifier = Modifier,
    nowOverride: LocalDateTime? = null
) {
    val ui = computeAccueilCycleVoiceState(
        now = nowOverride ?: LocalDateTime.now(),
        doneMatin = doneMatin,
        doneJour = doneJour,
        doneSoir = doneSoir,
        doneNuit = doneNuit
    )

    val base = WHITE_SOFT.copy(alpha = 0.92f)

    val style = when (ui.state) {
        AccueilCycleVoiceState.ETAT_1_NOUVEAU_CYCLE -> {
            TextStyle(
                color = base,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        AccueilCycleVoiceState.ETAT_2_EN_COURS -> {
            // blanc doux + reflet turquoise (léger)
            TextStyle(
                brush = Brush.linearGradient(
                    listOf(
                        base,
                        TURQUOISE.copy(alpha = 0.55f),
                        base
                    )
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        AccueilCycleVoiceState.ETAT_3_COMPLETE -> {
            // blanc doux + reflet mauve (léger)
            TextStyle(
                brush = Brush.linearGradient(
                    listOf(
                        base,
                        MAUVE.copy(alpha = 0.55f),
                        base
                    )
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    Box(modifier = modifier.padding(vertical = 6.dp)) {
        Text(
            text = ui.text,
            style = style
        )
    }
}

