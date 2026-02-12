// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/keywords/TraceKeywordsData.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.keywords

object TraceKeywordsData {

    fun wordsForSlider(sliderKey: String): List<String> {
        return when (sliderKey.lowercase()) {

            // GRATUIT (socle)
            "humeur", "humeur_globale" -> listOf(
                "Stable", "Oscillant", "montant", "Léger", "Lucide", "Flou","Descendant",
            )

            "energie", "energie_rythme", "rythme" -> listOf(
                "Fluide", "Constant", "Soutenu", "Accéléré", "Pulsé", "Irrégulier","Fragmenté"
            )

            "corps", "corps_sensations", "sensations" -> listOf(
                "Engourdi", "Relâché", "Lourd", "Léger", "Sensible", "Raide", "Crispé",
            )

            "presence", "presence_attention", "attention" -> listOf(
                "Présent", "Dispersé", "Focalisé", "Absent", "Vigilant", "Calme"
            )

            // PREMIUM (si tu veux déjà les préparer)
            "type_journee", "typejour" -> listOf(
                "Ordinaire", "Chargée", "Légère", "Dense", "Instable", "Fluide", "Lente", "Fragmentée", "Pleine", "Vide"
            )

            "motifs_psychiques", "motifs" -> listOf(
                "Anticipation", "Rumination", "Dispersion", "Focalisation", "Évitement", "Répétition", "Tension interne", "Apaisement", "Vigilance", "Retrait"
            )

            "environnement" -> listOf(
                "Calme", "Bruyant", "Stimulant", "Oppressant", "Neutre", "Familier", "Instable", "Sécurisant", "Exigeant", "Dispersant"
            )

            "clarte_mentale", "clarte" -> listOf(
                "Clair", "Brouillé", "Confus", "Lucide", "Saturé", "Ordonné", "Diffus", "Stable", "Embrouillé", "Fluide"
            )

            "charge_emotionnelle", "charge" -> listOf(
                "Faible", "Modérée", "Forte", "Contenue", "Débordante", "Diffuse", "Stable", "Montante", "Lourde", "Légère"
            )

            else -> listOf("Stable", "Changeant", "Calme", "Tendu", "Léger", "Dense")
        }
    }
}