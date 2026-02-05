package com.maxjth.tracememoire.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.maxjth.tracememoire.ui.home.HomeScreen
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme
import com.maxjth.tracememoire.ui.tracejour.TraceJourScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TraceMemoireTheme {

                val currentScreen = rememberSaveable {
                    mutableStateOf("HOME")
                }

                when (currentScreen.value) {

                    "HOME" -> HomeScreen(
                        onAddTrace = { currentScreen.value = "TRACE_JOUR" },
                        onOpenHistory = { }
                    )

                    "TRACE_JOUR" -> TraceJourScreen(
                        onBack = { currentScreen.value = "HOME" }
                    )
                }
            }
        }
    }
}