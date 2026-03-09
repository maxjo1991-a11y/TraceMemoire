package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle

@Composable
fun TraceJourTitleBlock(
    cycle: TraceCycle,
    modifier: Modifier = Modifier
) {
    val titleLine2 = "de l’expérience du moment"
    val tagline = "Chaque instant intérieur mérite d’être observé"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trace",
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 64.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = titleLine2,
            color = Color.White.copy(alpha = 0.90f),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = tagline,
            color = MAUVE.copy(alpha = 0.92f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}