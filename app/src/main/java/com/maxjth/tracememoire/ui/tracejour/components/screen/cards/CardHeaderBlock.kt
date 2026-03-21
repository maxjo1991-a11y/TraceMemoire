package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic.parseMemoryMeta
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model.CardOpenState
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp

@Composable
internal fun CardHeaderBlock(
    sliderKey: String,
    title: String,
    cardState: CardOpenState,
    isEmptyCollapsedCard: Boolean,
    isHero: Boolean,
    heroSubtitle: String?,
    heroTitleSizeSp: Int,
    memoryPhrase: String?,
    memoryKeyword: String?,
    memoryMeta: String?,
    hasNote: Boolean,
    captured: Boolean,
    createdAtMillis: Long?,
    locked: Boolean,
    onLockClick: (() -> Unit)?,
    borderStart: Color,
    modifier: Modifier = Modifier
) {
    val displayTitle = remember(sliderKey, title) {
        formatCardTitle(title)
    }

    val parsedMeta = remember(memoryMeta) {
        parseMemoryMeta(memoryMeta)
    }

    val titleSize = if (isHero) heroTitleSizeSp.sp else 23.sp
    val titleLine = if (isHero) (heroTitleSizeSp + 5).sp else 27.sp
    val emptyTitleSize = if (isHero) 36.sp else 26.sp
    val emptyTitleLine = if (isHero) 40.sp else 30.sp

    Column(
        modifier = modifier
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
            textAlign = if (isEmptyCollapsedCard) TextAlign.Center else TextAlign.Start
        )

        if (!isEmptyCollapsedCard) {

            when (cardState) {

                CardOpenState.CLOSED -> {

                    if (locked) {
                        if (!memoryPhrase.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
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
                            Spacer(Modifier.size(10.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.95f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                    } else {
                        if (!memoryKeyword.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.92f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                CardOpenState.PREVIEW -> {

                    if (locked) {

                        if (!memoryPhrase.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
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
                            Spacer(Modifier.size(10.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.92f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (parsedMeta != null) {
                            Spacer(Modifier.size(8.dp))

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
                                Spacer(Modifier.size(2.dp))
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

                        Spacer(Modifier.size(8.dp))

                        TraceMemoryStamp(
                            captured = captured,
                            createdAtMillis = createdAtMillis,
                            locked = locked,
                            onLock = onLockClick,
                            showLockChip = true,
                            modifier = Modifier
                        )

                    } else {
                        if (!memoryKeyword.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.92f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                CardOpenState.FULL -> {

                    if (locked) {

                        if (!memoryPhrase.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
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
                            Spacer(Modifier.size(10.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.92f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (parsedMeta != null) {
                            Spacer(Modifier.size(8.dp))

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
                                Spacer(Modifier.size(2.dp))
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

                        Spacer(Modifier.size(8.dp))

                        TraceMemoryStamp(
                            captured = captured,
                            createdAtMillis = createdAtMillis,
                            locked = locked,
                            onLock = onLockClick,
                            showLockChip = true,
                            modifier = Modifier
                        )

                    } else {
                        if (!memoryKeyword.isNullOrBlank()) {
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = memoryKeyword,
                                color = borderStart.copy(alpha = 0.92f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasNote) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "✦ Note",
                                color = borderStart.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}