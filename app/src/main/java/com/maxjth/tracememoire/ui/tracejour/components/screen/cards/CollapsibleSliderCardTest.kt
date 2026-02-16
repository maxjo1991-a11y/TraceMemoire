package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.TraceMemoireTheme // ajuste si ton Theme a un autre nom

@Preview(showBackground = true)
@Composable
private fun Preview_CollapsibleSliderCard_Test() {
    TraceMemoireTheme {
        Surface {
            CollapsibleSliderCard_Test()
        }
    }
}

@Composable
fun CollapsibleSliderCard_Test() {
    Column(modifier = Modifier.padding(16.dp)) {
        CollapsibleSliderCard(
            sliderKey = "test",
            title = "Énergie / rythme",
            isOpen = false,
            captured = true,
            createdAtMillis = null,
            locked = false,
            onLockClick = null,
            enabledForDot = true,
            onToggle = {},
            content = { /* vide */ },

            percent = 75,      // ✅ ça DOIT afficher le badge
            isHero = true,
            heroSubtitle = "TEST BADGE"
        )
    }
}