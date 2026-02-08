package com.maxjth.tracememoire.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(

    // GRAND TITRE PRINCIPAL (ÉCRANS 1–2–3–4)
    displayLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 67.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.5).sp
    ),

    // TITRES SECONDAIRES / BLOCS
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
)