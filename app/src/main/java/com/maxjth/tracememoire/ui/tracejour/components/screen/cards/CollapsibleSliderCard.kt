package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic.cycleBorderColors
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic.parseMemoryMeta
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model.CardOpenState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceStatusDot
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp
import com.maxjth.tracememoire.ui.tracejour.components.screen.percent.PercentBadge
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

private val CARD_RADIUS = 24.dp
private val CARD_MIN_HEIGHT = 76.dp
private val CARD_PADDING = 16.dp

private fun formatCardTitle(raw: String): String {
    return raw.replace(" / ", " ").trim()
}

@Composable
fun CollapsibleSliderCard(
    sliderKey: String,
    title: String,
    cardState: CardOpenState,
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,
    onLockClick: (() -> Unit)?,
    enabledForDot: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
    percent: Int? = null,
    memoryPhrase: String? = null,
    memoryKeyword: String? = null,
    memoryMeta: String? = null,
    isHero: Boolean = false,
    heroSubtitle: String? = null,
    heroMinHeight: Dp = 210.dp,
    heroTitleSizeSp: Int = 34,
    activeCycleKey: String? = null,
    modifier: Modifier = Modifier
) {
    val isPreview = cardState == CardOpenState.PREVIEW
    val isFull = cardState == CardOpenState.FULL
    val isExpanded = isPreview || isFull

    val shape = RoundedCornerShape(CARD_RADIUS)

    val dotState = when {
        !enabledForDot -> TraceDotState.DISABLED
        captured -> TraceDotState.ON
        else -> TraceDotState.OFF
    }

    val displayTitle = remember(sliderKey, title) {
        formatCardTitle(title)
    }

    val parsedMeta = remember(memoryMeta) {
        parseMemoryMeta(memoryMeta)
    }

    val minHeight = if (isHero) heroMinHeight else CARD_MIN_HEIGHT
    val padding = if (isHero) 20.dp else CARD_PADDING

    val innerLine = Color.White.copy(alpha = if (isHero) 0.045f else 0.035f)

    val titleSize = if (isHero) heroTitleSizeSp.sp else 23.sp
    val titleLine = if (isHero) (heroTitleSizeSp + 5).sp else 27.sp
    val emptyTitleSize = if (isHero) 36.sp else 26.sp
    val emptyTitleLine = if (isHero) 40.sp else 30.sp

    val shownPercent = percent?.coerceIn(0, 100)

    val hasCollapsedMemory = remember(memoryPhrase, memoryKeyword) {
        !memoryPhrase.isNullOrBlank() || !memoryKeyword.isNullOrBlank()
    }

    val isEmptyCollapsedCard = !isExpanded &&
            !hasCollapsedMemory &&
            (shownPercent == null || shownPercent == 0)

    val rowVerticalAlignment = if (isEmptyCollapsedCard) {
        Alignment.CenterVertically
    } else {
        Alignment.Top
    }

    val rightTopPadding = if (!isExpanded && hasCollapsedMemory) 6.dp else 0.dp

    // même verrouillée, la carte doit rester ouvrable
    val cardClickable = true

    val (borderStart, borderEnd) = remember(activeCycleKey, isHero) {
        cycleBorderColors(
            activeCycleKey = activeCycleKey,
            isHero = isHero
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = if (isHero) 0.05f else 0.04f),
                            Color.Transparent
                        ),
                        radius = 1100f
                    ),
                    shape = shape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = if (isHero) 0.18f else 0.14f),
                            MAUVE.copy(alpha = if (isHero) 0.08f else 0.06f),
                            BG_DEEP.copy(alpha = 0.975f),
                            BG_DEEP.copy(alpha = 1f)
                        ),
                        radius = 980f
                    ),
                    shape = shape
                )
                .border(
                    width = 0.95.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(borderStart, borderEnd)
                    ),
                    shape = shape
                )
                .padding(1.dp)
                .border(
                    width = 0.7.dp,
                    color = innerLine,
                    shape = shape
                )
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .safeClickable(enabled = cardClickable) { onToggle() }
                    .padding(start = 2.dp, end = 2.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = rowVerticalAlignment
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .heightIn(min = if (isHero) 76.dp else 64.dp),
                    verticalArrangement = if (isEmptyCollapsedCard) {
                        Arrangement.Center
                    } else {
                        Arrangement.Top
                    },
                    horizontalAlignment = if (isEmptyCollapsedCard) {
                        Alignment.CenterHorizontally
                    } else {
                        Alignment.Start
                    }
                ) {
                    if (isHero && !heroSubtitle.isNullOrBlank() && !isEmptyCollapsedCard) {
                        Text(
                            text = heroSubtitle,
                            color = WHITE_SOFT.copy(alpha = 0.54f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    }

                    Text(
                        text = displayTitle,
                        color = WHITE_SOFT.copy(alpha = 0.99f),
                        fontSize = if (isEmptyCollapsedCard) emptyTitleSize else titleSize,
                        lineHeight = if (isEmptyCollapsedCard) emptyTitleLine else titleLine,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (isEmptyCollapsedCard) TextAlign.Center else TextAlign.Start,
                        modifier = if (isEmptyCollapsedCard) Modifier.fillMaxWidth() else Modifier
                    )

                    if (!isEmptyCollapsedCard) {
                        when (cardState) {
                            CardOpenState.CLOSED -> {
                                if (!memoryPhrase.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(
                                        text = memoryPhrase,
                                        color = WHITE_SOFT.copy(alpha = 0.90f),
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!memoryKeyword.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = memoryKeyword,
                                        color = borderStart.copy(alpha = 0.95f),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            CardOpenState.PREVIEW -> {
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
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = memoryKeyword,
                                        color = borderStart.copy(alpha = 0.92f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (parsedMeta != null) {
                                    Spacer(modifier = Modifier.size(8.dp))

                                    Text(
                                        text = parsedMeta.topLine,
                                        color = WHITE_SOFT.copy(alpha = 0.78f),
                                        fontSize = 13.sp,
                                        lineHeight = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (!parsedMeta.bottomLine.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.size(2.dp))
                                        Text(
                                            text = parsedMeta.bottomLine,
                                            color = WHITE_SOFT.copy(alpha = 0.54f),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.size(8.dp))

                                TraceMemoryStamp(
                                    captured = captured,
                                    createdAtMillis = createdAtMillis,
                                    locked = locked,
                                    onLock = onLockClick,
                                    showLockChip = true,
                                    modifier = Modifier
                                )
                            }

                            CardOpenState.FULL -> {
                                if (!memoryPhrase.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(
                                        text = memoryPhrase,
                                        color = WHITE_SOFT.copy(alpha = 0.88f),
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!memoryKeyword.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = memoryKeyword,
                                        color = borderStart.copy(alpha = 0.92f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (parsedMeta != null) {
                                    Spacer(modifier = Modifier.size(8.dp))

                                    Text(
                                        text = parsedMeta.topLine,
                                        color = WHITE_SOFT.copy(alpha = 0.78f),
                                        fontSize = 13.sp,
                                        lineHeight = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (!parsedMeta.bottomLine.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.size(2.dp))
                                        Text(
                                            text = parsedMeta.bottomLine,
                                            color = WHITE_SOFT.copy(alpha = 0.54f),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.size(8.dp))

                                TraceMemoryStamp(
                                    captured = captured,
                                    createdAtMillis = createdAtMillis,
                                    locked = locked,
                                    onLock = onLockClick,
                                    showLockChip = true,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                if (!isEmptyCollapsedCard) {
                    Spacer(modifier = Modifier.size(12.dp))

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(top = rightTopPadding)
                            .widthIn(min = 66.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (shownPercent != null) {
                                PercentBadge(
                                    percent = shownPercent,
                                    fontSizeSp = 17
                                )
                            }

                            TraceStatusDot(
                                state = dotState,
                                glowSize = if (isHero) 18.dp else 16.dp
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isFull,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 240,
                        easing = FastOutSlowInEasing
                    )
                ) + expandVertically(
                    animationSpec = tween(
                        durationMillis = 260,
                        easing = FastOutSlowInEasing
                    ),
                    expandFrom = Alignment.Top
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 180,
                        easing = FastOutSlowInEasing
                    )
                ) + shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 220,
                        easing = FastOutSlowInEasing
                    ),
                    shrinkTowards = Alignment.Top
                )
            ) {
                Column {
                    Spacer(modifier = Modifier.size(10.dp))
                    content()
                }
            }
        }
    }
}