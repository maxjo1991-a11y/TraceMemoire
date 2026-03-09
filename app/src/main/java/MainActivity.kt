package com.maxjth.tracememoire

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maxjth.tracememoire.ui.accueil.ecran.HomeScreen
import com.maxjth.tracememoire.ui.comprendre.AstraScreen
import com.maxjth.tracememoire.ui.historique.HistoryScreen
import com.maxjth.tracememoire.ui.planetes.memora.MemoraScreen
import com.maxjth.tracememoire.ui.planetes.orion.OrionScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.components.screen.TraceJourScreen
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TraceMemoire"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_TRACE_JOUR = "trace_jour"
        private const val SCREEN_HISTORY = "history"
        private const val SCREEN_DEEPEN = "deepen"
        private const val SCREEN_COMPRENDRE = "comprendre"
        private const val SCREEN_ASTRA = "astra"
        private const val SCREEN_MEMORA = "memora"
        private const val SCREEN_ORION = "orion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {
                val saveStore = remember { TraceSaveStore() }

                var screen by rememberSaveable {
                    mutableStateOf(SCREEN_HOME)
                }

                fun go(dest: String) {
                    Log.d(TAG, "NAV -> $dest")
                    screen = dest
                }

                // ✅ CAS SPÉCIAL IMPORTANT :
                // Home reste vivant pendant TraceJour pour éviter le flash au retour
                if (screen == SCREEN_HOME || screen == SCREEN_TRACE_JOUR) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        // Fond permanent = Écran 1 toujours monté
                        HomeScreen(
                            onAddTrace = {
                                Log.d(TAG, "CLICK HomeScreen: onAddTrace")
                                go(SCREEN_TRACE_JOUR)
                            },
                            onOpenHistory = {
                                Log.d(TAG, "CLICK HomeScreen: onOpenHistory")
                                go(SCREEN_HISTORY)
                            },
                            onOpenComprendre = {
                                Log.d(TAG, "CLICK HomeScreen: onOpenComprendre")
                                go(SCREEN_COMPRENDRE)
                            },
                            onOpenAstra = {
                                Log.d(TAG, "CLICK HomeScreen: onOpenAstra")
                                go(SCREEN_ASTRA)
                            },
                            onOpenMemora = {
                                Log.d(TAG, "CLICK HomeScreen: onOpenMemora")
                                go(SCREEN_MEMORA)
                            },
                            onOpenOrion = {
                                Log.d(TAG, "CLICK HomeScreen: onOpenOrion")
                                go(SCREEN_ORION)
                            },
                            saveStore = saveStore
                        )

                        // Overlay = Écran 2 par-dessus
                        if (screen == SCREEN_TRACE_JOUR) {
                            TraceJourScreen(
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
                        }
                    }
                } else {
                    when (screen) {

                        SCREEN_HISTORY -> HistoryScreen(
                            onBack = {
                                Log.d(TAG, "CLICK HistoryScreen: onBack")
                                go(SCREEN_HOME)
                            }
                        )

                        SCREEN_COMPRENDRE -> AstraScreen(
                            onBack = {
                                Log.d(TAG, "CLICK ComprendreScreen: onBack")
                                go(SCREEN_HOME)
                            }
                        )

                        SCREEN_DEEPEN -> HomeScreen(
                            onAddTrace = {
                                Log.d(TAG, "CLICK Deepen placeholder -> onAddTrace")
                                go(SCREEN_TRACE_JOUR)
                            },
                            onOpenHistory = {
                                Log.d(TAG, "CLICK Deepen placeholder -> onOpenHistory")
                                go(SCREEN_HISTORY)
                            },
                            onOpenComprendre = {
                                Log.d(TAG, "CLICK Deepen placeholder -> onOpenComprendre")
                                go(SCREEN_COMPRENDRE)
                            },
                            onOpenAstra = { go(SCREEN_ASTRA) },
                            onOpenMemora = { go(SCREEN_MEMORA) },
                            onOpenOrion = { go(SCREEN_ORION) },
                            saveStore = saveStore
                        )

                        SCREEN_ASTRA -> AstraScreen(
                            onBack = { go(SCREEN_HOME) }
                        )

                        SCREEN_MEMORA -> MemoraScreen(
                            onBack = { go(SCREEN_HOME) }
                        )

                        SCREEN_ORION -> OrionScreen(
                            onBack = { go(SCREEN_HOME) }
                        )

                        else -> HomeScreen(
                            onAddTrace = {
                                Log.d(TAG, "CLICK fallback HomeScreen: onAddTrace")
                                go(SCREEN_TRACE_JOUR)
                            },
                            onOpenHistory = {
                                Log.d(TAG, "CLICK fallback HomeScreen: onOpenHistory")
                                go(SCREEN_HISTORY)
                            },
                            onOpenComprendre = {
                                Log.d(TAG, "CLICK fallback HomeScreen: onOpenComprendre")
                                go(SCREEN_COMPRENDRE)
                            },
                            onOpenAstra = {
                                go(SCREEN_ASTRA)
                            },
                            onOpenMemora = {
                                go(SCREEN_MEMORA)
                            },
                            onOpenOrion = {
                                go(SCREEN_ORION)
                            },
                            saveStore = saveStore
                        )
                    }
                }
            }
        }
    }
}