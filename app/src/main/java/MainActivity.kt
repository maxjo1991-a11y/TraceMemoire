package com.maxjth.tracememoire

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.maxjth.tracememoire.ui.historique.HistoryScreen
import com.maxjth.tracememoire.ui.accueil.ecran.HomeScreen
import com.maxjth.tracememoire.ui.planetes.astra.AstraScreen
import com.maxjth.tracememoire.ui.planetes.memora.MemoraScreen
import com.maxjth.tracememoire.ui.planetes.orion.OrionScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.components.screen.TraceJourScreen
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TraceMemoire"

        private const val SCREEN_HOME = "com/maxjth/tracememoire/ui/tracejour/components/screen/save/home"
        private const val SCREEN_TRACE_JOUR = "trace_jour"
        private const val SCREEN_HISTORY = "history"
        private const val SCREEN_DEEPEN = "deepen"

        // ✅ PLANÈTES
        private const val SCREEN_ASTRA = "astra"
        private const val SCREEN_MEMORA = "memora"
        private const val SCREEN_ORION = "orion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                // ✅ STORE UNIQUE (Home <-> TraceJour)
                val saveStore = remember { TraceSaveStore() }

                // ✅ Saveable: survives rotation / recreation
                val screen = rememberSaveable { mutableStateOf(SCREEN_HOME) }

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

                    SCREEN_HISTORY -> HistoryScreen(
                        onBack = {
                            Log.d(TAG, "CLICK HistoryScreen: onBack")
                            go(SCREEN_HOME)
                        }
                    )

                    // ✅ Placeholder temporaire (comme tu avais)
                    SCREEN_DEEPEN -> HomeScreen(
                        onAddTrace = {
                            Log.d(TAG, "CLICK Deepen placeholder -> onAddTrace")
                            go(SCREEN_TRACE_JOUR)
                        },
                        onOpenHistory = {
                            Log.d(TAG, "CLICK Deepen placeholder -> onOpenHistory")
                            go(SCREEN_HISTORY)
                        },
                        onOpenAstra = { go(SCREEN_ASTRA) },
                        onOpenMemora = { go(SCREEN_MEMORA) },
                        onOpenOrion = { go(SCREEN_ORION) },
                        saveStore = saveStore
                    )

                    // ✅ PLANÈTES
                    SCREEN_ASTRA -> AstraScreen(
                        onBack = { go(SCREEN_HOME) }
                    )

                    SCREEN_MEMORA -> MemoraScreen(
                        onBack = { go(SCREEN_HOME) }
                    )

                    SCREEN_ORION -> OrionScreen(
                        onBack = { go(SCREEN_HOME) }
                    )
                }
            }
        }
    }
}