package com.maxjth.tracememoire.ui.tracejour.components.screen.valeurcapsule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceCycleValeurCapsule(
    cycleLabel: String,
    value: Int,
    max: Int,
    modifier: Modifier = Modifier
) {

    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = 0.10f),
                        TURQUOISE.copy(alpha = 0.08f)
                    )
                ),
                shape = shape
            )
            .border(
                width = 1.4.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MAUVE,
                        TURQUOISE
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = buildAnnotatedString {

                    withStyle(
                        SpanStyle(
                            color = WHITE_SOFT
                        )
                    ) {
                        append(cycleLabel)
                    }

                    append(" ")

                    withStyle(
                        SpanStyle(
                            color = TURQUOISE
                        )
                    ) {
                        append("•")
                    }

                    append(" ")

                    withStyle(
                        SpanStyle(
                            color = WHITE_SOFT.copy(alpha = 0.70f)
                        )
                    ) {
                        append("en cours")
                    }
                },
                fontSize = 18.sp
            )

            Text(
                text = "$value / $max",
                color = WHITE_SOFT,
                fontSize = 20.sp
            )
        }
    }
}

