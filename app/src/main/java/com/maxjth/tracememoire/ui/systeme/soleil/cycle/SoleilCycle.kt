package com.maxjth.tracememoire.ui.systeme.soleil.cycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import java.time.LocalDateTime
import kotlinx.coroutines.delay

/**
 * Fournit une heure "vivante" pour le Home.
 * Mise à jour toutes les 30 secondes.
 */
@Composable
fun rememberHomeNow(nowOverride: LocalDateTime? = null): LocalDateTime {

    val now by produceState(
        initialValue = nowOverride ?: LocalDateTime.now(),
        key1 = nowOverride
    ) {

        if (nowOverride != null) {
            value = nowOverride
            return@produceState
        }

        while (true) {
            value = LocalDateTime.now()
            delay(30_000L)
        }
    }

    return now
}

/**
 * Couleur dynamique de l'anneau Soleil selon le moment du jour.
 */
fun ringColorForCycle(now: LocalDateTime): Color {

    val minutes = now.hour * 60 + now.minute

    val milk = WHITE_SOFT.copy(alpha = 0.90f)
    val mauveNight = lerp(MAUVE, Color(0xFF2A1840), 0.55f)
    val mauveEvening = lerp(MAUVE, Color(0xFF3A2460), 0.35f)

    return when {

        // Nuit
        minutes in 0..(4 * 60 + 59) ->
            mauveNight.copy(alpha = 0.95f)

        // Matin
        minutes in (5 * 60)..(11 * 60 + 59) ->
            lerp(TURQUOISE, milk, 0.10f).copy(alpha = 0.95f)

        // Jour
        minutes in (12 * 60)..(17 * 60 + 59) ->
            lerp(TURQUOISE, milk, 0.20f).copy(alpha = 0.95f)

        // Soir
        else ->
            lerp(TURQUOISE, mauveEvening, 0.72f).copy(alpha = 0.95f)
    }
}