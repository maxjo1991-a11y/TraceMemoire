package com.maxjth.tracememoire.ui.tracejour.components.screen.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

@Composable
fun TraceNoteBlock(
    note: String,
    onNoteChange: (String) -> Unit,
    enabled: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    externalLocked: Boolean = false,
    userIsPremium: Boolean = false,
    maxCharsFree: Int = 200,
    maxCharsPremium: Int = 589,
    title: String = "Trace écrite",
    placeholder: String = "Écrire une note…"
) {
    val freeCap = maxCharsFree.coerceAtLeast(1)
    val premiumCap = maxCharsPremium.coerceAtLeast(freeCap)

    val maxChars = remember(userIsPremium, freeCap, premiumCap) {
        if (userIsPremium) premiumCap else freeCap
    }

    var localNote by remember(note, maxChars) {
        mutableStateOf(if (note.length > maxChars) note.take(maxChars) else note)
    }

    LaunchedEffect(note, maxChars) {
        val clamped = if (note.length > maxChars) note.take(maxChars) else note
        if (clamped != localNote) {
            localNote = clamped
        }
    }

    val safeNote = localNote
    val noteEditable = enabled && !externalLocked

    var isOpen by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()
    val shapeOuter = RoundedCornerShape(22.dp)
    val shapeInner = RoundedCornerShape(22.dp)

    val noteAccent = remember(accent) {
        lerp(accent, Color(0xFF8A5CFF), 0.22f)
    }

    val noteTurquoise = remember(accent) {
        lerp(TURQUOISE, accent, 0.25f)
    }

    val headerBrush = remember(noteAccent, noteTurquoise) {
        Brush.horizontalGradient(
            colors = listOf(
                noteTurquoise.copy(alpha = 0.08f),
                noteAccent.copy(alpha = 0.12f),
                Color.Transparent,
                noteAccent.copy(alpha = 0.06f)
            )
        )
    }

    val writingBrush = remember(noteAccent, noteTurquoise, isFocused) {
        Brush.verticalGradient(
            colors = listOf(
                if (isFocused) Color(0xFF262626) else Color(0xFF1C1C1C),
                if (isFocused) Color(0xFF303030) else Color(0xFF252525),
                lerp(noteTurquoise, noteAccent, 0.55f).copy(alpha = if (isFocused) 0.09f else 0.045f)
            )
        )
    }

    val infinite = rememberInfiniteTransition(label = "trace_note_star")

    val pulseScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    val glowAlpha by infinite.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_glow"
    )

    val breatheScale by infinite.animateFloat(
        initialValue = 0.998f,
        targetValue = 1.014f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "note_breathe"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapeOuter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BG_SOFT.copy(alpha = 0.10f),
                        BG_SOFT.copy(alpha = 0.07f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = noteAccent.copy(alpha = 0.20f),
                shape = shapeOuter
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(headerBrush)
                .safeClickable {
                    if (enabled) isOpen = !isOpen
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    lerp(noteTurquoise, noteAccent, 0.55f).copy(alpha = glowAlpha),
                                    noteTurquoise.copy(alpha = glowAlpha * 0.45f),
                                    noteAccent.copy(alpha = glowAlpha * 0.35f),
                                    Color.Black.copy(alpha = 0.84f)
                                ),
                                radius = 120f
                            )
                        )
                        .border(
                            width = 1.3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    noteTurquoise.copy(alpha = 0.72f),
                                    noteAccent.copy(alpha = 0.84f),
                                    Color.White.copy(alpha = 0.18f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✦",
                        color = WHITE_SOFT.copy(alpha = 0.98f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.98f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "${safeNote.length}",
                color = WHITE_SOFT.copy(alpha = 0.74f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.widthIn(min = 28.dp)
            )
        }

        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(animationSpec = tween(280)) +
                    expandVertically(animationSpec = tween(360)),
            exit = fadeOut(animationSpec = tween(180)) +
                    shrinkVertically(animationSpec = tween(260))
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 156.dp)
                        .height(if (userIsPremium) 210.dp else 184.dp)
                        .graphicsLayer {
                            val scale = if (noteEditable && isFocused) breatheScale else 1f
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(shapeInner)
                        .background(writingBrush)
                        .border(
                            width = if (isFocused && noteEditable) 1.6.dp else 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lerp(noteTurquoise, noteAccent, 0.45f).copy(
                                        alpha = if (isFocused && noteEditable) 0.58f else 0.30f
                                    ),
                                    Color.White.copy(alpha = if (isFocused && noteEditable) 0.18f else 0.08f)
                                )
                            ),
                            shape = shapeInner
                        )
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        lerp(noteTurquoise, noteAccent, 0.55f).copy(
                                            alpha = if (isFocused && noteEditable) 0.07f else 0.025f
                                        ),
                                        Color.Transparent
                                    ),
                                    radius = 520f
                                )
                            )
                    )

                    if (!noteEditable) {
                        Text(
                            text = safeNote.ifBlank { "Aucune trace écrite." },
                            color = Color.White.copy(alpha = if (safeNote.isBlank()) 0.42f else 0.88f),
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            fontStyle = if (safeNote.isBlank()) FontStyle.Italic else FontStyle.Normal,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        BasicTextField(
                            value = safeNote,
                            onValueChange = { raw ->
                                val next = raw.take(maxChars)
                                localNote = next
                                onNoteChange(next)
                            },
                            enabled = noteEditable,
                            textStyle = TextStyle(
                                color = Color.White.copy(alpha = 0.98f),
                                fontSize = 17.sp,
                                lineHeight = 26.sp
                            ),
                            cursorBrush = SolidColor(
                                if (isFocused) noteTurquoise.copy(alpha = 0.95f)
                                else Color.White.copy(alpha = 0.80f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .verticalScroll(scroll)
                                .onFocusChanged {
                                    isFocused = it.isFocused
                                },
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (safeNote.isBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(148.dp),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Text(
                                                text = placeholder,
                                                color = lerp(noteTurquoise, noteAccent, 0.45f).copy(alpha = 0.48f),
                                                fontSize = 17.sp,
                                                fontStyle = FontStyle.Italic,
                                                letterSpacing = 0.35.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                    }
                                    inner()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Fermer",
                        color = noteAccent.copy(alpha = 0.96f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .safeClickable {
                                if (enabled) isOpen = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}