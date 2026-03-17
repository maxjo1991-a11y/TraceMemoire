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

    val titleLine1 = "Empreinte"
    val titleLine2 = "du présent"

    val tagline = "Chaque instant intérieur mérite d’être observé"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = titleLine1,
            color = Color.White,
            fontSize = 56.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 58.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = titleLine2,
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = tagline,
            color = MAUVE.copy(alpha = 0.92f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}