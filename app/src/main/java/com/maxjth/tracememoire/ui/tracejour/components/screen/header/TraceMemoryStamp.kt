// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/header/TraceMemoryStamp.kt
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

/**
 * ✅ PILL "Jour • en cours" + STAMP "Mémoire créée • 14:32" + lock chip
 *
 * - La PILL est indépendante de captured (peut s'afficher même si mémoire absente)
 * - captured == true : on affiche le stamp
 * - createdAtMillis => affichage "14:32" (heure secondaire, discret)
 */
@Composable
fun TraceMemoryStamp(
    // --- PILL (cycle en cours) ---
    cycleLabel: String? = null,          // ex: "Nuit"
    cycleActive: Boolean = false,         // true => affiche la Pill
    showCyclePill: Boolean = true,

    // --- STAMP (mémoire créée) ---
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,

    modifier: Modifier = Modifier,
    onLock: (() -> Unit)? = null,
    showLockChip: Boolean = true
) {
    Column(modifier = modifier) {

        // ─────────────────────────────────────────────
        // 0) PILL "Jour • en cours"
        // ─────────────────────────────────────────────
        if (showCyclePill && cycleActive && !cycleLabel.isNullOrBlank()) {
            CycleStatusPill(
                label = cycleLabel,
                isActive = true
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
        }

        // ─────────────────────────────────────────────
        // 1) STAMP (seulement si mémoire capturée)
        // ─────────────────────────────────────────────
        if (!captured) return

        val hhmm = createdAtMillis?.let { formatHourMinute(it) }

        // ✅ Ligne unique : "Mémoire créée • 14:32"
        Text(
            text = if (hhmm != null) "Mémoire créée • $hhmm" else "Mémoire créée",
            color = TURQUOISE.copy(alpha = 0.92f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        // ─────────────────────────────────────────────
        // 2) état cycle (verrouillé / bouton verrouiller)
        // ─────────────────────────────────────────────
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
                        elevation = AssistChipDefaults.assistChipElevation(
                            elevation = 0.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            draggedElevation = 0.dp,
                            disabledElevation = 0.dp
                        ),
                        modifier = Modifier.heightIn(min = 28.dp)
                    )
                }
            }
        }
    }
}

/* -----------------------------
  Heure locale stable : "14:32"
------------------------------ */
private fun formatHourMinute(millis: Long): String {
    val zone = ZoneId.systemDefault()
    val dt = Instant.ofEpochMilli(millis).atZone(zone)
    val h = dt.hour.toString().padStart(2, '0')
    val m = dt.minute.toString().padStart(2, '0')
    return "$h:$m"
}