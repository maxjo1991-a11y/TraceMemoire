package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.components.TriangleOutlineBreathing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(onBack: () -> Unit) {

    var percent by remember { mutableStateOf(50) }
    var isInteracting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            TopAppBar(
                title = { Text("Trace du jour") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Retour")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TriangleOutlineBreathing(
                percent = percent,
                isInteracting = isInteracting,
                modifier = Modifier.size(260.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$percent%",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFF5F5F5)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = percent.toFloat(),
                onValueChange = {
                    percent = it.toInt()
                    isInteracting = true
                },
                onValueChangeFinished = {
                    isInteracting = false
                },
                valueRange = 0f..100f
            )
        }
    }
}