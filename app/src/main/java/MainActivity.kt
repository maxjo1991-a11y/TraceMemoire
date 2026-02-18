package com.maxjth.tracememoire

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.maxjth.tracememoire.ui.home.HomeScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.components.screen.TraceJourScreen
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TraceMemoire"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_TRACE_JOUR = "trace_jour"
        private const val SCREEN_HISTORY = "history"
        private const val SCREEN_DEEPEN = "deepen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                // ✅ STORE UNIQUE (Home <-> TraceJour)
                val saveStore = remember { TraceSaveStore() }

                val screen = remember { mutableStateOf(SCREEN_HOME) }

                fun go(dest: String) {
                    Log.d(TAG, "NAV -> $dest")
                    screen.value = dest
                }

                when (screen.value) {

                    SCREEN_HOME -> HomeScreen(
                        onAddTrace = {
                            Log.d(TAG, "CLICK HomeScreen: onAddTrace")
                            go(SCREEN_TRACE_JOUR)
                        },
                        onOpenHistory = {
                            Log.d(TAG, "CLICK HomeScreen: onOpenHistory")
                            go(SCREEN_HISTORY)
                        },
                        saveStore = saveStore
                    )

                    SCREEN_TRACE_JOUR -> TraceJourScreen(
                        onBack = {
                            Log.d(TAG, "CLICK TraceJourScreen: onBack")
                            go(SCREEN_HOME)
                        },
                        onHistory = {
                            Log.d(TAG, "CLICK TraceJourScreen: onHistory")
                            go(SCREEN_HISTORY)
                        },
                        onDeepen = {
                            Log.d(TAG, "CLICK TraceJourScreen: onDeepen")
                            go(SCREEN_DEEPEN)
                        },
                        saveStore = saveStore
                    )

                    SCREEN_HISTORY -> {
                        // ✅ temporaire: écran placeholder
                        Log.d(TAG, "SCREEN_HISTORY placeholder -> retour Home")
                        go(SCREEN_HOME)
                    }

                    SCREEN_DEEPEN -> {
                        // ✅ temporaire: écran placeholder
                        Log.d(TAG, "SCREEN_DEEPEN placeholder -> retour Home")
                        go(SCREEN_HOME)
                    }
                }
            }
        }
    }
}