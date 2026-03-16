package com.maxjth.tracememoire.ui.tracejour.components.screen.header

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

/**
 * TraceMemoryStamp
 *
 * - PILL optionnelle "Jour • en cours"
 * - mémoire créée (sans heure répétée)
 * - état verrouillé / bouton verrouiller
 * - lignes bien alignées, compactes et propres
 */
@Composable
fun TraceMemoryStamp(
    // --- PILL (cycle en cours) ---
    cycleLabel: String? = null,
    cycleActive: Boolean = false,
    showCyclePill: Boolean = true,

    // --- STAMP ---
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,

    modifier: Modifier = Modifier,
    onLock: (() -> Unit)? = null,
    showLockChip: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {

        if (showCyclePill && cycleActive && !cycleLabel.isNullOrBlank()) {
            CycleStatusPill(
                label = cycleLabel,
                isActive = true
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
        }

        if (!captured) return

        val stampTextColor = WHITE_SOFT.copy(alpha = 0.88f)
        val lockIconColor = MAUVE.copy(alpha = 0.88f)
        val textSize = 13.sp
        val iconSize = 14.dp
        val slotWidth = 16.dp

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Mémoire créée",
                color = stampTextColor,
                fontSize = textSize,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.size(1.dp))

        if (locked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier.width(slotWidth),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Cycle verrouillé",
                        tint = lockIconColor,
                        modifier = Modifier.size(iconSize)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Cycle verrouillé",
                    color = stampTextColor,
                    fontSize = textSize,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else if (showLockChip && onLock != null) {
            Spacer(modifier = Modifier.size(3.dp))

            AssistChip(
                onClick = onLock,
                label = {
                    Text(
                        text = "Verrouiller le cycle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.LockOpen,
                        contentDescription = "Verrouiller le cycle",
                        modifier = Modifier.size(15.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = TURQUOISE.copy(alpha = 0.08f),
                    labelColor = TURQUOISE.copy(alpha = 0.92f),
                    leadingIconContentColor = TURQUOISE.copy(alpha = 0.92f)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = TURQUOISE.copy(alpha = 0.28f)
                ),
                elevation = AssistChipDefaults.assistChipElevation(
                    elevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    draggedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                modifier = Modifier
                    .heightIn(min = 28.dp)
                    .widthIn(min = 0.dp)
            )
        }
    }
}