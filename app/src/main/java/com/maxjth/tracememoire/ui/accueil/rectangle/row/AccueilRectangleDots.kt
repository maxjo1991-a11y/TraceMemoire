// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/row/AccueilRectangleDots.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.row

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

/**
 * Affiche les 4 pastilles FREE pour un cycle.
 * Version centrée + stable visuellement.
 */
@Composable
fun AccueilRectangleDots(
    dots: List<Boolean>,
    isSaved: Boolean,
    modifier: Modifier = Modifier
) {

    // Sécurise la taille (toujours 4)
    val safeDots = List(4) { index ->
        dots.getOrNull(index) == true
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        safeDots.forEach { done ->

            val dotColor = when {
                done -> TURQUOISE.copy(alpha = 0.95f)
                isSaved -> MAUVE.copy(alpha = 0.45f)
                else -> Color.White.copy(alpha = 0.15f)
            }

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}