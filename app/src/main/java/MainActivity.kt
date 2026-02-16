package com.maxjth.tracememoire

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maxjth.tracememoire.ui.history.HistoryScreen
import com.maxjth.tracememoire.ui.home.HomeScreen
import com.maxjth.tracememoire.ui.deepen.TraceDeepenScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.components.screen.TraceJourScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                var screen by rememberSaveable { mutableStateOf(SCREEN_HOME) }

                fun go(dest: String) {
                    screen = dest
                }

                when (screen) {

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
                        onBack = { go(SCREEN_HOME) },
                        onDeepen = { go(SCREEN_DEEPEN) } // ✅ FIX: param manquant
                    )

                    SCREEN_DEEPEN -> TraceDeepenScreen(
                        onBack = { go(SCREEN_TRACE_JOUR) } // ou SCREEN_HOME si tu préfères
                    )

                    SCREEN_HISTORY -> HistoryScreen(
                        onBack = { go(SCREEN_HOME) }
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "TraceMemoire"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_TRACE_JOUR = "trace_jour"
        private const val SCREEN_DEEPEN = "deepen"
        private const val SCREEN_HISTORY = "history"
    }
}