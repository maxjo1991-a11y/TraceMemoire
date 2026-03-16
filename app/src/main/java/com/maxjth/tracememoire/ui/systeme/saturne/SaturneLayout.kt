package com.maxjth.tracememoire.ui.systeme.saturne

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun SaturneLayout(
    memoire: Int,
    valeurHier: Int,
    valeurJournee: Int,
    modifier: Modifier = Modifier
) {
    val primary = WHITE_SOFT
    val secondary = WHITE_SOFT.copy(alpha = 0.86f)
    val tertiary = WHITE_SOFT.copy(alpha = 0.68f)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "TRACE",
            color = secondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(letterSpacing = 0.22.em),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 38.dp)
        )

        Text(
            text = memoire.toString(),
            color = primary,
            fontSize = 70.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 78.dp)
        )

        Text(
            text = "HIER",
            color = tertiary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(letterSpacing = 0.16.em),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-70).dp, y = 36.dp)
        )

        Text(
            text = valeurHier.toString(),
            color = primary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-70).dp, y = 82.dp)
        )

        Text(
            text = "AUJOURD'HUI ",
            color = tertiary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(letterSpacing = 0.12.em),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 70.dp, y = 36.dp)
        )

        Text(
            text = valeurJournee.toString(),
            color = primary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 70.dp, y = 82.dp)
        )
    }
}