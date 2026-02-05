package com.maxjth.tracememoire.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// Trace Mémoire – Palette officielle (verrouillée)
// ─────────────────────────────────────────────

// Fonds
val BG_DEEP = Color(0xFF000000)          // Noir profond, base de l’app
val BG_SOFT = Color(0xFF0A0A0A)          // Noir doux (variations possibles)

// Textes
val WHITE_SOFT = Color(0xFFE9E9E9)       // Texte principal
val TEXT_MUTED = Color(0xFFAAAAAA)       // Texte secondaire / discret

// Texte narratif (blanc + souffle mauve)
// → blanc dominant, mauve perceptible sans être violet
val WHITE_MAUVE = Color(0xFFE6E4F2)

// Accents Trace Mémoire
val TURQUOISE = Color(0xFF00A3A3)        // Présence, stabilité, souffle
val MAUVE = Color(0xFF7A63C6)            // Intériorité, trace, mémoire

// États doux (alphas utilisés dynamiquement)
val TURQUOISE_SOFT = Color(0xFF00A3A3).copy(alpha = 0.25f)
val MAUVE_SOFT = Color(0xFF7A63C6).copy(alpha = 0.25f)