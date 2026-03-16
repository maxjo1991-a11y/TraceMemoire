package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.accueil.ajout.button.EmpreinteButton
import com.maxjth.tracememoire.ui.accueil.ajout.effect.EmpreinteEffectController
import com.maxjth.tracememoire.ui.accueil.ajout.effect.EmpreinteOverlay
import com.maxjth.tracememoire.ui.accueil.ajout.effect.empreinteInput
import com.maxjth.tracememoire.ui.accueil.blocs.AccueilHeaderBloc
import com.maxjth.tracememoire.ui.accueil.blocs.MemoireHistoriquePortail
import com.maxjth.tracememoire.ui.accueil.rectangle.AccueilRectangleBloc
import com.maxjth.tracememoire.ui.systeme.saturne.SaturneCircle
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.CycleStatusPill
import java.time.LocalDateTime

@Composable
fun HomeScreenContent(
    subtitle: String,
    pillLabel: String,
    showPill: Boolean,
    luneCount: Int,
    luneDeltaToday: Int,
    scoreHier: Int?,
    soleilValue: Int?,
    soleilDeltaText: String?,
    terreDeltaText: String?,
    homeNow: LocalDateTime?,
    activeCycleKey: String?,
    pMatin: Int?,
    pJour: Int?,
    pSoir: Int?,
    pNuit: Int?,
    empreinteController: EmpreinteEffectController,
    galaxyFlashValue: Float,
    onAddTraceClick: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenComprendre: () -> Unit,
    onBoutonCenterChanged: (Offset) -> Unit,
    onMemoireCenterChanged: (Offset) -> Unit,
    createdAtMillisForCycleKey: (String) -> Long?,
    dotsForCycleKey: (String) -> List<Boolean>,
    onTestMinuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // =========================
    // TUNING HOME
    // =========================
    val contentTopPadding = 36.dp
    val contentHorizontalPadding = 32.dp

    val headerToPillSpacer = 4.dp
    val pillOffsetY = (-6).dp
    val pillGapToConstellation = 8.dp

    val constellationZoneHeight = 430.dp
    val saturneOffsetY = (-16).dp

    val empreinteOffsetY = (-40).dp
    val afterConstellationSpacer = 2.dp

    val rectangleOffsetY = (-12).dp
    val afterRectangleSpacer = 0.dp

    val historyPortalOffsetY = 8.dp
    val historyHaloSize = 208.dp

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(
                horizontal = contentHorizontalPadding,
                vertical = contentTopPadding
            )
            .widthIn(max = 520.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccueilHeaderBloc(subtitle = subtitle)

        Spacer(Modifier.height(headerToPillSpacer))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = pillOffsetY),
            contentAlignment = Alignment.Center
        ) {
            CycleStatusPill(
                label = pillLabel,
                isActive = showPill
            )
        }

        Spacer(Modifier.height(pillGapToConstellation))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(constellationZoneHeight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.offset(y = saturneOffsetY),
                contentAlignment = Alignment.Center
            ) {
                SaturneCircle(
                    memoire = luneCount,
                    valeurHier = scoreHier ?: 0,
                    valeurMaintenant = soleilValue ?: 0,
                    activeCycleKey = activeCycleKey
                )
            }
        }

        Spacer(Modifier.height(afterConstellationSpacer))

        // =========================
        // BOUTON AJOUTER EMPREINTE
        // =========================
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = empreinteOffsetY)
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInRoot()
                    onBoutonCenterChanged(
                        Offset(
                            x = pos.x + coords.size.width / 2f,
                            y = pos.y + coords.size.height / 2f
                        )
                    )
                }
                .empreinteInput(
                    controller = empreinteController,
                    onClick = onAddTraceClick,
                    onLongPress = onAddTraceClick
                )
        ) {
            EmpreinteButton(modifier = Modifier)

            EmpreinteOverlay(
                controller = empreinteController,
                modifier = Modifier.matchParentSize(),
                colorStart = MAUVE,
                colorEnd = TURQUOISE
            )
        }

        // =========================
        // RECTANGLE DES CYCLES
        // =========================
        Box(
            modifier = Modifier.offset(y = rectangleOffsetY)
        ) {
            AccueilRectangleBloc(
                pMatin = pMatin,
                pJour = pJour,
                pSoir = pSoir,
                pNuit = pNuit,
                activeCycleKey = activeCycleKey,
                createdAtMillisForCycleKey = createdAtMillisForCycleKey,
                dotsForCycleKey = dotsForCycleKey
            )
        }

        Spacer(Modifier.height(afterRectangleSpacer))

        // =========================
        // MÉMOIRE HISTORIQUE
        // =========================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = historyPortalOffsetY)
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInRoot()
                    onMemoireCenterChanged(
                        Offset(
                            x = pos.x + coords.size.width / 2f,
                            y = pos.y + coords.size.height / 2f
                        )
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(historyHaloSize)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                TURQUOISE.copy(alpha = 0.10f + (galaxyFlashValue * 0.10f)),
                                MAUVE.copy(alpha = 0.05f + (galaxyFlashValue * 0.06f)),
                                androidx.compose.ui.graphics.Color.Transparent
                            ),
                            radius = 420f
                        )
                    )
                    .blur((26 + 8 * galaxyFlashValue).dp)
            )

            MemoireHistoriquePortail(
                onClick = onOpenHistory
            )
        }

        Spacer(Modifier.height(14.dp))
    }
}