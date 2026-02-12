// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/phrases/TracePhrasesData.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.phrases

import java.text.Normalizer
import kotlin.math.abs

object TracePhrasesData {

    // 1) Normalise n’importe quelle clé reçue → "humeur" | "energie" | "corps" | "presence"
    private fun normalizeSliderKey(raw: String): String {
        val base = Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")           // enlève accents
            .lowercase()
            .replace("[^a-z]".toRegex(), "")             // enlève espaces, /, _, etc.

        // mapping tolérant (au cas où tu envoies des titres au lieu des keys)
        return when {
            base.startsWith("humeur") -> "humeur"
            base.startsWith("energie") || base.startsWith("rythme") -> "energie"
            base.startsWith("corps") || base.startsWith("sensation") -> "corps"
            base.startsWith("presence") || base.startsWith("attention") || base.startsWith("focus") -> "presence"
            else -> base
        }
    }

    fun phrasesForSlider(sliderKey: String, isPremium: Boolean): List<String> {
        return when (normalizeSliderKey(sliderKey)) {

            "humeur" -> if (isPremium) listOf(
                "Humeur changeante, difficile à lire.",
                "Je me sens un peu déphasé.",
                "Humeur fragile aujourd’hui.",
                "Je suis irritable sans raison claire.",
                "Intérieurement, c’est plus lourd que ça en a l’air."
            ) else listOf(
                "Ça va plutôt bien aujourd’hui.",
                "Journée correcte, rien de spécial.",
                "Bof… humeur moyenne.",
                "Pas dans ma meilleure vibe.",
                "Étonnamment de bonne humeur."
            )

            "energie" -> if (isPremium) listOf(
                "Mon énergie fluctue sans prévenir.",
                "J’avance, mais tout me coûte plus qu’hier.",
                "Je suis allumé dehors, vidé dedans.",
                "Je sens une tension de fond qui me draine.",
                "Mon rythme ne m’appartient pas aujourd’hui."
            ) else listOf(
                "J’ai de l’énergie aujourd’hui.",
                "Énergie correcte, ça fait la job.",
                "Un peu fatigué, mais ça passe.",
                "Je suis sur le pilote automatique.",
                "Je manque de jus aujourd’hui."
            )

            "corps" -> if (isPremium) listOf(
                "Je sens une tension qui ne lâche pas.",
                "Mon corps est là, mais pas en confort.",
                "Je suis sensible à tout aujourd’hui.",
                "Je traîne une lourdeur difficile à expliquer.",
                "J’ai l’impression d’être serré de l’intérieur."
            ) else listOf(
                "Je me sens bien dans mon corps.",
                "Ça va, rien à signaler.",
                "Un peu tendu aujourd’hui.",
                "Fatigue dans le corps.",
                "Corps lourd, journée lourde."
            )

            "presence" -> if (isPremium) listOf(
                "Je suis là… mais pas complètement.",
                "Mon attention décroche sans que je m’en rende compte.",
                "J’ai la tête pleine, difficile à trier.",
                "Je suis présent par moments, absent le reste.",
                "Je flotte entre deux états."
            ) else listOf(
                "Je suis focus aujourd’hui.",
                "Concentration correcte.",
                "Je me laisse distraire facilement.",
                "Je suis dans la lune.",
                "Difficile de rester présent."
            )

            else -> emptyList()
        }
    }

    // 2) Si tu veux UNE phrase directement (pour l’affichage sous le slider)
    fun phraseForSlider(sliderKey: String, isPremium: Boolean, seed: String = ""): String {
        val list = phrasesForSlider(sliderKey, isPremium)
        if (list.isEmpty()) return ""
        val idx = abs((normalizeSliderKey(sliderKey) + "|" + isPremium + "|" + seed).hashCode()) % list.size
        return list[idx]
    }
}