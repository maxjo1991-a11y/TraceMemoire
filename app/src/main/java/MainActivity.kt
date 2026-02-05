package com.maxjth.tracememoire.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.maxjth.tracememoire.ui.history.HistoryScreen
import com.maxjth.tracememoire.ui.home.HomeScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.TraceJourScreen

class MainActivity : ComponentActivity() {

    companion object {
        private const val SCREEN_HOME = "HOME"
        private const val SCREEN_TRACE_JOUR = "TRACE_JOUR"
        private const val SCREEN_HISTORY = "HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                val currentScreen = rememberSaveable {
                    mutableStateOf(SCREEN_HOME)
                }

                when (currentScreen.value) {

                    SCREEN_HOME -> HomeScreen(
                        onAddTrace = { currentScreen.value = SCREEN_TRACE_JOUR },
                        onOpenHistory = { currentScreen.value = SCREEN_HISTORY }
                    )

                    SCREEN_TRACE_JOUR -> TraceJourScreen(
                        onBack = { currentScreen.value = SCREEN_HOME }
                    )

                    SCREEN_HISTORY -> HistoryScreen(
                        onBack = { currentScreen.value = SCREEN_HOME }
                    )
                }
            }
        }
    }
}