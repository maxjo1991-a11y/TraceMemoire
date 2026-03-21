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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic.cycleBorderColors
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model.CardOpenState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

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
    hasNote: Boolean = false,
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

    val minHeight = if (isHero) heroMinHeight else CARD_MIN_HEIGHT
    val padding = if (isHero) 20.dp else CARD_PADDING

    val innerLine = Color.White.copy(alpha = if (isHero) 0.045f else 0.035f)

    val shownPercent = percent?.coerceIn(0, 100)

    val hasCollapsedMemory = remember(memoryPhrase, memoryKeyword) {
        !memoryPhrase.isNullOrBlank() || !memoryKeyword.isNullOrBlank()
    }

    val isEmptyCollapsedCard = !isExpanded &&
            !hasCollapsedMemory &&
            !hasNote &&
            (shownPercent == null || shownPercent == 0)

    val rowVerticalAlignment = if (isEmptyCollapsedCard) {
        Alignment.CenterVertically
    } else {
        Alignment.Top
    }

    val rightTopPadding = if (!isExpanded && (hasCollapsedMemory || hasNote)) 6.dp else 0.dp
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
                CardHeaderBlock(
                    sliderKey = sliderKey,
                    title = displayTitle,
                    cardState = cardState,
                    isEmptyCollapsedCard = isEmptyCollapsedCard,
                    isHero = isHero,
                    heroSubtitle = heroSubtitle,
                    heroTitleSizeSp = heroTitleSizeSp,
                    memoryPhrase = memoryPhrase,
                    memoryKeyword = memoryKeyword,
                    memoryMeta = memoryMeta,
                    hasNote = hasNote, // ✅ AJOUT ICI
                    captured = captured,
                    createdAtMillis = createdAtMillis,
                    locked = locked,
                    onLockClick = onLockClick,
                    borderStart = borderStart,
                    modifier = Modifier
                        .weight(1f, fill = true)
                )

                if (!isEmptyCollapsedCard) {
                    Spacer(modifier = Modifier.size(12.dp))

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        CardRightMetricsBlock(
                            shownPercent = shownPercent,
                            dotState = dotState,
                            rightTopPadding = rightTopPadding,
                            isHero = isHero
                        )


                    }
                }
            }

            AnimatedVisibility(
                visible = isFull && !locked,
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