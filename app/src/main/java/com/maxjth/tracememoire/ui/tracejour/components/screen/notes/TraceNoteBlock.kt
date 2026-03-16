// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/notes/TraceNoteBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

@Composable
fun TraceNoteBlock(
    note: String,
    onNoteChange: (String) -> Unit,
    enabled: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    userIsPremium: Boolean = false,
    maxCharsFree: Int = 200,
    maxCharsPremium: Int = 589,
    title: String = "Trace écrite",
    placeholder: String = "Écrire une note…",
    footerMessageFree: String =
        "Tout ne demande pas à être écrit. Mais tout peut l'être.",
    footerMessagePremium: String =
        "Ici, tout peut exister. Sans filtre."
) {
    val freeCap = maxCharsFree.coerceAtLeast(1)
    val premiumCap = maxCharsPremium.coerceAtLeast(freeCap)

    val maxChars = remember(userIsPremium, freeCap, premiumCap) {
        if (userIsPremium) premiumCap else freeCap
    }

    val safeNote = remember(note, maxChars) {
        if (note.length > maxChars) note.take(maxChars) else note
    }

    val hasContent = safeNote.isNotBlank()
    var isOpen by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()
    val shapeOuter = RoundedCornerShape(18.dp)
    val shapeInner = RoundedCornerShape(16.dp)

    val noteAccent = remember(accent) {
        lerp(accent, Color(0xFF8A5CFF), 0.22f)
    }

    val paperBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.18f),
                Color.Black.copy(alpha = 0.10f),
                Color.Black.copy(alpha = 0.16f)
            )
        )
    }

    val footerText = remember(userIsPremium) {
        if (userIsPremium) footerMessagePremium else footerMessageFree
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapeOuter)
            .background(BG_SOFT.copy(alpha = 0.06f))
            .border(
                width = 1.dp,
                color = noteAccent.copy(alpha = 0.20f),
                shape = shapeOuter
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .safeClickable {
                    if (enabled) isOpen = !isOpen
                }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    tint = noteAccent.copy(alpha = 0.90f)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = title,
                        color = WHITE_SOFT.copy(alpha = 0.96f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Clip,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (hasContent) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(noteAccent)
                            )

                            Spacer(modifier = Modifier.size(6.dp))

                            Text(
                                text = "Ajouté",
                                color = WHITE_SOFT.copy(alpha = 0.70f),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Ajouter",
                            color = WHITE_SOFT.copy(alpha = 0.55f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                text = "${safeNote.length}/$maxChars",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 13.sp
            )
        }

        if (isOpen) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 116.dp)
                    .height(if (userIsPremium) 176.dp else 156.dp)
                    .clip(shapeInner)
                    .background(paperBrush)
                    .border(
                        width = if (userIsPremium) 1.4.dp else 1.dp,
                        color = noteAccent.copy(alpha = 0.18f),
                        shape = shapeInner
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = safeNote,
                    onValueChange = { raw ->
                        if (!enabled) return@BasicTextField
                        onNoteChange(raw.take(maxChars))
                    },
                    enabled = enabled,
                    textStyle = TextStyle(
                        color = WHITE_SOFT.copy(alpha = 0.92f),
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll),
                    decorationBox = { inner ->
                        if (safeNote.isBlank()) {
                            Text(
                                text = placeholder,
                                color = WHITE_SOFT.copy(alpha = 0.28f),
                                fontSize = 16.sp
                            )
                        }
                        inner()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Fermer",
                color = noteAccent.copy(alpha = 0.95f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .safeClickable {
                        if (enabled) isOpen = false
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(
                        width = 1.dp,
                        color = noteAccent.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = footerText,
                    color = WHITE_SOFT.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Clip,
                    style = TextStyle(
                        shadow = Shadow(
                            color = noteAccent.copy(alpha = 0.25f),
                            offset = Offset(0f, 0f),
                            blurRadius = 12f
                        )
                    )
                )
            }
        }
    }
}