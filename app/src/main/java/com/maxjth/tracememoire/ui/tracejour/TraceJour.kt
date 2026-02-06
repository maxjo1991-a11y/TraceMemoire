package com.maxjth.tracememoire.ui.tracejour

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ------------------------------
 HELPERS (écran 2)
-------------------------------- */

fun levelLabel(p: Int): String =
    when {
        p < 30 -> "Bas"
        p < 60 -> "Moyen"
        p < 80 -> "Bon"
        else -> "Élevé"
    }

fun isEditable(now: Long, created: Long): Boolean =
    now - created < 24 * 60 * 60 * 1000L

fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = 1f
    )
}

/* ------------------------------
 UI BRICKS (écran 2)
-------------------------------- */

@Composable
fun PercentMessageBubble(p: Int, dyn: Color) {
    Text(
        text = "$p %",
        color = dyn,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TraceStatusBlock(
    createdAtMs: Long,
    updatedAtMs: Long,
    updateCount: Int,
    nowMs: Long,
    textColor: Color
) {
    // ✅ pour l’instant: affichage minimal (tu pourras afficher "modifié", "x updates", etc.)
    Text(
        text = "Trace du jour",
        fontSize = 12.sp,
        color = textColor.copy(alpha = 0.6f)
    )
}

@Composable
fun TraceNoteField(
    note: String,
    onNoteChange: (String) -> Unit,
    enabled: Boolean,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(16.dp)
    ) {
        BasicTextField(
            value = note,
            onValueChange = { if (enabled) onNoteChange(it.take(120)) },
            textStyle = TextStyle(color = textColor, fontSize = 16.sp),
            modifier = Modifier.fillMaxSize()
        )
    }
}



data class TraceDuJour(
    val percent: Int = 50,
    val note: String = "",
    val tags: Set<String> = emptySet(),
    val createdAtMs: Long = System.currentTimeMillis(),
    val updatedAtMs: Long = createdAtMs,
    val updateCount: Int = 0
) {
    fun withPercent(newPercent: Int, nowMs: Long = System.currentTimeMillis()): TraceDuJour {
        val p = newPercent.coerceIn(0, 100)
        return copy(
            percent = p,
            updatedAtMs = nowMs,
            updateCount = updateCount + 1
        )
    }

    fun withNote(newNote: String, nowMs: Long = System.currentTimeMillis()): TraceDuJour {
        val clean = newNote.take(120)
        return copy(
            note = clean,
            updatedAtMs = nowMs,
            updateCount = updateCount + 1
        )
    }

    fun withTags(newTags: Set<String>, nowMs: Long = System.currentTimeMillis()): TraceDuJour {
        return copy(
            tags = newTags,
            updatedAtMs = nowMs,
            updateCount = updateCount + 1
        )
    }
}