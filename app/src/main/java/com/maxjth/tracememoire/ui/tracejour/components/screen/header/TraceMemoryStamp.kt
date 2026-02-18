package com.maxjth.tracememoire.ui.tracejour.components.screen.header

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/**
 * ✅ "Mémoire créée" + date/heure + état verrouillage
 *
 * - captured == false : on n’affiche rien
 * - locked == false   : chip "Verrouiller le cycle" (si onLock != null)
 * - locked == true    : "Cycle verrouillé" + icône lock (chip disparaît)
 *
 * createdAtMillis:
 *  - null => pas de date
 *  - sinon => "15 fév 2026 9:15 AM"
 */
@Composable
fun TraceMemoryStamp(
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,
    modifier: Modifier = Modifier,
    onLock: (() -> Unit)? = null,
    showLockChip: Boolean = true
) {
    if (!captured) return

    val createdText = createdAtMillis?.let { formatStampFr(it) }

    Column(modifier = modifier) {

        // Ligne 1 : Mémoire créée
        Text(
            text = "• Mémoire créée",
            color = MAUVE.copy(alpha = 0.90f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        // Ligne 2 : Date/heure (si dispo)
        if (!createdText.isNullOrBlank()) {
            Text(
                text = createdText,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Ligne 3 : état cycle (minimal, sans pollution)
        Row(
            modifier = Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (locked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Cycle verrouillé",
                    tint = MAUVE.copy(alpha = 0.82f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cycle verrouillé",
                    color = MAUVE.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                // ✅ Chip seulement (pas de "Cycle modifiable" -> bruit visuel)
                if (showLockChip && onLock != null) {
                    AssistChip(
                        onClick = onLock,
                        label = {
                            Text(
                                text = "Verrouiller le cycle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.LockOpen,
                                contentDescription = "Verrouiller le cycle",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = TURQUOISE.copy(alpha = 0.10f),
                            labelColor = TURQUOISE.copy(alpha = 0.90f),
                            leadingIconContentColor = TURQUOISE.copy(alpha = 0.90f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = TURQUOISE.copy(alpha = 0.35f)
                        ),
                        // ✅ IMPORTANT: enlève la “poudre/brume” (shadow/elevation)
                        elevation = AssistChipDefaults.assistChipElevation(
                            elevation = 0.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            draggedElevation = 0.dp,
                            disabledElevation = 0.dp
                        ),
                        // ✅ option compact (si tu veux que ça prenne moins de place)
                        modifier = Modifier.heightIn(min = 28.dp)
                    )
                }
            }
        }
    }
}

/* -----------------------------
   Format date FR stable
   "15 fév 2026 9:15 AM"
------------------------------ */
private fun formatStampFr(millis: Long): String {
    val zone = ZoneId.systemDefault()
    val dt = Instant.ofEpochMilli(millis).atZone(zone)

    val day = dt.dayOfMonth
    val month = frMonthAbbrev(dt.monthValue)
    val year = dt.year

    val hour24 = dt.hour
    val minute = dt.minute.toString().padStart(2, '0')
    val ampm = if (hour24 < 12) "AM" else "PM"

    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }

    return "$day $month $year $hour12:$minute $ampm"
}

private fun frMonthAbbrev(month: Int): String {
    // ✅ Abréviations stables (évite "févr." sur certains devices)
    return when (month) {
        1 -> "jan"
        2 -> "fév"
        3 -> "mar"
        4 -> "avr"
        5 -> "mai"
        6 -> "jun"
        7 -> "jul"
        8 -> "aoû"
        9 -> "sep"
        10 -> "oct"
        11 -> "nov"
        12 -> "déc"
        else -> "?"
    }
}