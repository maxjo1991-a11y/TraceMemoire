// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/ajout/preview/AjoutEmpreintePreview.kt
package com.maxjth.tracememoire.ui.accueil.ajout.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.maxjth.tracememoire.ui.accueil.ajout.effect.EmpreinteOverlay
import com.maxjth.tracememoire.ui.accueil.ajout.effect.empreinteInput
import com.maxjth.tracememoire.ui.accueil.ajout.effect.rememberEmpreinteEffectController

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun AjoutEmpreintePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center
        ) {
            AjoutEmpreintePreviewButton(
                sizeDp = 150,
                label = "Ajouter\nEmpreinte"
            )
        }
    }
}

@Composable
private fun AjoutEmpreintePreviewButton(
    sizeDp: Int,
    label: String
) {
    val turquoise = Color(0xFF00A3A3)
    val mauve = Color(0xFF7A63C6)

    val empreinte = rememberEmpreinteEffectController()

    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .empreinteInput(
                controller = empreinte,
                onClick = { /* preview */ },
                onLongPress = { /* preview */ }
            ),
        contentAlignment = Alignment.Center
    ) {

        // Fond cercle
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(Color(0xFF0F1012))
        )

        // Contour dégradé (sweep)
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 6.dp.toPx()
            val inset = stroke / 2f
            val diameter = size.minDimension - stroke

            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        turquoise,
                        turquoise,
                        mauve,
                        mauve,
                        turquoise
                    )
                ),
                radius = diameter / 2f,
                center = center,
                style = Stroke(width = stroke)
            )

            // léger halo externe (super discret)
            drawCircle(
                color = turquoise.copy(alpha = 0.08f),
                radius = (diameter / 2f) + inset + 10.dp.toPx()
            )
        }

        // Effet empreinte (tap / long press)
        EmpreinteOverlay(
            controller = empreinte,
            modifier = Modifier.matchParentSize(),
            colorStart = turquoise,
            colorEnd = mauve
        )

        // Contenu
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "+",
                color = turquoise.copy(alpha = 0.95f),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                color = Color(0xFFF2F2F2),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        }
    }
}

