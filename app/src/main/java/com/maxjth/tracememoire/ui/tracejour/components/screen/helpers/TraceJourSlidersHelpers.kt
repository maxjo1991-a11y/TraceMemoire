package com.maxjth.tracememoire.ui.tracejour.components.screen.helpers

import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class SliderDef(val key: String, val title: String)

val SLIDERS_FREE = listOf(
    SliderDef("humeur", "Humeur globale"),
    SliderDef("energie", "Énergie / rythme"),
    SliderDef("corps", "Corps / sensations"),
    SliderDef("presence", "Présence / attention")
)

val SLIDERS_PREMIUM = listOf(
    SliderDef(TraceSaveKeys.SLIDER_REPOS, "Qualité du repos vécu"),
    SliderDef(TraceSaveKeys.SLIDER_ARCHI_EMO, "Architecture émotionnelle"),
    SliderDef(TraceSaveKeys.SLIDER_TYPE_JOURNEE, "Type de journée"),
    SliderDef(TraceSaveKeys.SLIDER_MOTIFS, "Motifs psychiques"),
    SliderDef(TraceSaveKeys.SLIDER_ENVIRONNEMENT, "Environnement"),
    SliderDef(TraceSaveKeys.SLIDER_CLARTE, "Clarté mentale")
)

const val ROW_SPACING_DP = 14
const val SECTION_GAP_DP = 22
const val OUTER_HORIZONTAL_PADDING_DP = 10
const val INNER_OPEN_GAP_DP = 8

fun cycleLabelFr(cycleKey: String): String {
    return when (cycleKey.uppercase()) {
        "MATIN" -> "Matin"
        "JOUR" -> "Jour"
        "SOIR" -> "Soir"
        "NUIT" -> "Nuit"
        else -> cycleKey.lowercase().replaceFirstChar { c ->
            if (c.isLowerCase()) c.titlecase(Locale.CANADA_FRENCH) else c.toString()
        }
    }
}

fun formatMemoryMeta(
    cycleKey: String,
    createdAtMillis: Long?
): String? {
    val millis = createdAtMillis ?: return null
    if (millis <= 0L) return null

    return try {
        val zone = ZoneId.systemDefault()
        val instant = Instant.ofEpochMilli(millis)
        val dateTime = instant.atZone(zone)

        val cyclePart = cycleLabelFr(cycleKey)
        val timePart = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val datePart = dateTime.format(
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.CANADA_FRENCH)
        )

        "$cyclePart • $timePart • $datePart"
    } catch (_: Exception) {
        null
    }
}

fun extractKeywordFromPhrase(phrase: String): String {
    val stopWords = setOf(
        "de", "du", "des", "la", "le", "les", "un", "une", "et",
        "très", "plus", "peu", "assez", "dans", "sur", "avec", "sans",
        "au", "aux", "en", "du", "moment", "soirée", "journée", "nuit", "matinée",
        "niveau", "etat", "état"
    )

    val cleanedWords = phrase
        .replace("’", "'")
        .replace(",", " ")
        .replace(".", " ")
        .replace(";", " ")
        .replace(":", " ")
        .split(" ", "'", "-", "•")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.lowercase() }

    val best = cleanedWords.lastOrNull { it !in stopWords && it.length >= 3 }
        ?: cleanedWords.lastOrNull()
        ?: ""

    return best.replaceFirstChar { c ->
        if (c.isLowerCase()) c.titlecase(Locale.CANADA_FRENCH) else c.toString()
    }
}

fun buildMemoryPhrase(
    sliderKey: String,
    cycleKey: String,
    persistedPct: Int,
    captured: Boolean,
    createdAtMillis: Long?
): String? {
    val hasMemory = captured || (createdAtMillis != null)
    if (!hasMemory) return null

    return TracePhrasesData.phraseForSlider(
        sliderKey = sliderKey,
        phaseKey = cycleKey,
        percent = persistedPct.coerceIn(0, 100)
    )
}

fun buildMemoryKeyword(
    memoryPhrase: String?
): String? {
    if (memoryPhrase.isNullOrBlank()) return null
    val keyword = extractKeywordFromPhrase(memoryPhrase)
    return keyword.ifBlank { null }
}

