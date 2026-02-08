package com.maxjth.tracememoire.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maxjth.tracememoire.ui.history.HistoryScreen
import com.maxjth.tracememoire.ui.home.HomeScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.screen.TraceJourScreen

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TraceMemoireNav"

        private const val SCREEN_HOME = "HOME"
        private const val SCREEN_TRACE_JOUR = "TRACE_JOUR"
        private const val SCREEN_HISTORY = "HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                var currentScreen by rememberSaveable { mutableStateOf(SCREEN_HOME) }

                fun go(screen: String) {
                    Log.d(TAG, "NAV -> $screen")
                    currentScreen = screen
                }

                when (currentScreen) {

                    SCREEN_HOME -> HomeScreen(
                        onAddTrace = {
                            Log.d(TAG, "CLICK HomeScreen: onAddTrace()")
                            go(SCREEN_TRACE_JOUR)
                        },
                        onOpenHistory = {
                            Log.d(TAG, "CLICK HomeScreen: onOpenHistory()")
                            go(SCREEN_HISTORY)
                        }
                    )

                    SCREEN_TRACE_JOUR -> TraceJourScreen(
                        onBack = { go(SCREEN_HOME) }
                    )

                    SCREEN_HISTORY -> HistoryScreen(
                        onBack = { go(SCREEN_HOME) }
                    )
                }
            }
        }
    }
}