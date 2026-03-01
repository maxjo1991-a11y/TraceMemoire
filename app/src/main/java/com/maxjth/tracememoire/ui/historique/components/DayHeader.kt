package com.maxjth.tracememoire.ui.historique.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun DayHeader(
    prettyDay: String,
    dayKey: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = prettyDay,
                color = WHITE_SOFT.copy(alpha = 0.96f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = dayKey,
                color = WHITE_SOFT.copy(alpha = 0.38f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MAUVE.copy(alpha = 0.26f),
                    shape = RoundedCornerShape(999.dp)
                )
                .background(
                    color = BG_SOFT.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$count év.",
                color = WHITE_SOFT.copy(alpha = 0.74f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}