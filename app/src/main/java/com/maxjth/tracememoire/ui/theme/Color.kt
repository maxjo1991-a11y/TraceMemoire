package com.maxjth.tracememoire.ui.theme

import androidx.compose.ui.graphics.Color

/* ------------------------------------------------
   COULEURS FONDAMENTALES — TRACE MÉMOIRE
   Source unique de vérité visuelle
------------------------------------------------- */

/* ✅ Couleurs signature */
val TURQUOISE = Color(0xFF2EC4B6)
val MAUVE = Color(0xFF7E5BEF)

/* ✅ Fonds principaux */
val BG_DEEP = Color(0xFF0A0A0A)
val BG_SOFT = Color(0xFF141414)

/* ✅ Blanc principal */
val WHITE_SOFT = Color(0xFFF2F2F2)

/* ✅ Blanc légèrement teinté mauve (textes doux / secondaire) */
val WHITE_MAUVE = MAUVE.copy(alpha = 0.10f)

/* ✅ Blanc neutre pour séparateurs / strokes subtils */
val WHITE_FAINT = Color.White.copy(alpha = 0.12f)

/* ✅ Turquoise adouci (halos / accents faibles) */
val TURQUOISE_SOFT = TURQUOISE.copy(alpha = 0.35f)

/* ✅ Mauve adouci (glow / bordures) */
val MAUVE_SOFT = MAUVE.copy(alpha = 0.35f)