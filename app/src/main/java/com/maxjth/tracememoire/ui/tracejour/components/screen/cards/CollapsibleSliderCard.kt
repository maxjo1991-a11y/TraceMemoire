package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceStatusDot
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp
import com.maxjth.tracememoire.ui.tracejour.components.screen.percent.PercentBadge
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

private val CARD_RADIUS = 24.dp
private val CARD_MIN_HEIGHT = 78.dp
private val CARD_PADDING = 16.dp

private fun formatCardTitle(sliderKey: String, raw: String): String {
    return raw.replace(" / ", "\n").trim()
}

@Composable
fun CollapsibleSliderCard(
    sliderKey: String,
    title: String,
    isOpen: Boolean,
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,
    onLockClick: (() -> Unit)?,
    enabledForDot: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,

    // % affiché sur la carte
    percent: Int? = null,

    // ✅ mémoire fermée future
    memoryPhrase: String? = null,
    memoryKeyword: String? = null,
    memoryMeta: String? = null,

    // mode hero
    isHero: Boolean = false,
    heroSubtitle: String? = null,
    heroMinHeight: Dp = 210.dp,
    heroTitleSizeSp: Int = 34
) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    val dotState = when {
        !enabledForDot -> TraceDotState.DISABLED
        captured -> TraceDotState.ON
        else -> TraceDotState.OFF
    }

    val displayTitle = remember(sliderKey, title) {
        formatCardTitle(sliderKey, title)
    }

    val minHeight = if (isHero) heroMinHeight else CARD_MIN_HEIGHT
    val padding = if (isHero) 20.dp else CARD_PADDING

    val mauveHaloA = if (isHero) 0.26f else 0.18f
    val mauveHaloB = if (isHero) 0.12f else 0.08f
    val turquoiseEdge = if (isHero) 0.12f else 0.08f
    val bgTop = if (isHero) 0.28f else 0.23f
    val bgBottom = if (isHero) 0.19f else 0.15f
    val borderMauve = if (isHero) 0.31f else 0.24f

    val titleStyleKey = remember(displayTitle, isHero) {
        val singleWord = !displayTitle.contains(" ")
        val longSingleWord = singleWord && displayTitle.length >= 12
        val longTitle = displayTitle.length >= 22
        Triple(longSingleWord, longTitle, singleWord)
    }

    val (longSingleWord, longTitle, _) = titleStyleKey

    val baseTitleSize = if (isHero) heroTitleSizeSp.sp else 24.sp
    val baseLineHeight = if (isHero) (heroTitleSizeSp + 6).sp else 29.sp

    val titleSize = when {
        isHero -> baseTitleSize
        longSingleWord -> 21.sp
        longTitle -> 22.sp
        else -> 24.sp
    }

    val titleLine = when {
        isHero -> baseLineHeight
        longSingleWord -> 25.sp
        longTitle -> 27.sp
        else -> 29.sp
    }

    val shownPercent = percent?.coerceIn(0, 100)

    val hasMeta = remember(captured, createdAtMillis, locked) {
        captured || (createdAtMillis != null) || locked
    }

    val hasCollapsedMemory = remember(memoryPhrase, memoryKeyword, memoryMeta) {
        !memoryPhrase.isNullOrBlank() || !memoryKeyword.isNullOrBlank() || !memoryMeta.isNullOrBlank()
    }

    val rowVerticalAlignment = if (hasMeta || hasCollapsedMemory) {
        Alignment.Top
    } else {
        Alignment.CenterVertically
    }

    val rightTopPadding = if (hasMeta || hasCollapsedMemory) 10.dp else 2.dp
    val cardClickable = !locked

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = mauveHaloA),
                            MAUVE.copy(alpha = mauveHaloB),
                            Color.Transparent
                        )
                    ),
                    shape = shape
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = turquoiseEdge),
                            Color.Transparent,
                            TURQUOISE.copy(alpha = turquoiseEdge)
                        )
                    ),
                    shape = shape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BG_SOFT.copy(alpha = bgTop),
                            BG_SOFT.copy(alpha = bgBottom)
                        )
                    ),
                    shape = shape
                )
                .border(1.4.dp, MAUVE.copy(alpha = borderMauve), shape)
                .padding(1.dp)
                .border(0.9.dp, TURQUOISE.copy(alpha = 0.10f), shape)
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .safeClickable(enabled = cardClickable) { onToggle() }
                    .padding(start = 2.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = rowVerticalAlignment
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .heightIn(min = if (isHero) 76.dp else 64.dp)
                ) {
                    if (isHero && !heroSubtitle.isNullOrBlank()) {
                        Text(
                            text = heroSubtitle,
                            color = WHITE_SOFT.copy(alpha = 0.54f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.size(5.dp))
                    }

                    Text(
                        text = displayTitle,
                        color = WHITE_SOFT.copy(alpha = 0.99f),
                        fontSize = titleSize,
                        lineHeight = titleLine,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )

                    // ✅ mémoire fermée affichée seulement quand la carte est fermée
                    if (!isOpen && hasCollapsedMemory) {
                        if (!memoryPhrase.isNullOrBlank()) {
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = memoryPhrase,
                                color = WHITE_SOFT.copy(alpha = 0.88f),
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (!memoryKeyword.isNullOrBlank()) {
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = memoryKeyword,
                                color = TURQUOISE.copy(alpha = 0.88f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (!memoryMeta.isNullOrBlank()) {
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = memoryMeta,
                                color = WHITE_SOFT.copy(alpha = 0.46f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                    } else {
                        Spacer(modifier = Modifier.size(6.dp))
                    }

                    TraceMemoryStamp(
                        captured = captured,
                        createdAtMillis = createdAtMillis,
                        locked = locked,
                        onLock = onLockClick,
                        showLockChip = true,
                        modifier = Modifier
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(top = rightTopPadding)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (shownPercent != null) {
                            PercentBadge(percent = shownPercent)
                        }

                        TraceStatusDot(
                            state = dotState,
                            glowSize = if (isHero) 18.dp else 16.dp
                        )
                    }
                }
            }

            if (isOpen) {
                Spacer(Modifier.size(10.dp))
                content()
            }
        }
    }
}