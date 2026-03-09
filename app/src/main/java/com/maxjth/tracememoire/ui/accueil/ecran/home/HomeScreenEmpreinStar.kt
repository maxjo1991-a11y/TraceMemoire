package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
data class HomeScreenEmpreinteStarState(
    val boutonCenterInRoot: Offset?,
    val memoireCenterInRoot: Offset?,
    val starTravel: Animatable<Float, *>,
    val starAlpha: Animatable<Float, *>,
    val galaxyFlash: Animatable<Float, *>,
    val onBoutonCenterChanged: (Offset) -> Unit,
    val onMemoireCenterChanged: (Offset) -> Unit,
    val launchEmpreinteStar: () -> Unit
)

@Composable
fun rememberHomeScreenEmpreinteStar(
    onAddTrace: () -> Unit
): HomeScreenEmpreinteStarState {
    val scope = rememberCoroutineScope()

    var boutonCenterInRoot by remember { mutableStateOf<Offset?>(null) }
    var memoireCenterInRoot by remember { mutableStateOf<Offset?>(null) }

    val starTravel = remember { Animatable(0f) }
    val starAlpha = remember { Animatable(0f) }
    val galaxyFlash = remember { Animatable(0f) }

    fun launchStar() {
        val start = boutonCenterInRoot
        val end = memoireCenterInRoot

        if (start == null || end == null) {
            onAddTrace()
            return
        }

        scope.launch {
            launch {
                starTravel.snapTo(0f)
                starAlpha.snapTo(1f)

                starTravel.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 680,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                starAlpha.snapTo(1f)

                starAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 760,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            launch {
                delay(520)

                galaxyFlash.snapTo(0.95f)

                galaxyFlash.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 420,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            // ✅ IMPORTANT :
            // on laisse le temps à l’animation de se voir
            delay(620)
            onAddTrace()
        }
    }

    return HomeScreenEmpreinteStarState(
        boutonCenterInRoot = boutonCenterInRoot,
        memoireCenterInRoot = memoireCenterInRoot,
        starTravel = starTravel,
        starAlpha = starAlpha,
        galaxyFlash = galaxyFlash,
        onBoutonCenterChanged = { boutonCenterInRoot = it },
        onMemoireCenterChanged = { memoireCenterInRoot = it },
        launchEmpreinteStar = { launchStar() }
    )
}