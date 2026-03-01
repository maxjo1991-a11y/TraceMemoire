// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/blocs/AccueilHeaderBloc.kt
package com.maxjth.tracememoire.ui.accueil.blocs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE

@Composable
fun AccueilHeaderBloc(
    subtitle: String,
    modifier: Modifier = Modifier
) {
    // modifier présent pour évolution future (padding, test tags, etc.)
    Text(
        text = "Trace\nMémoire",
        fontSize = 61.sp,
        lineHeight = 60.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = Color.White
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text = subtitle,
        color = MAUVE.copy(alpha = 0.9f),
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )

    Spacer(Modifier.height(22.dp))
}

