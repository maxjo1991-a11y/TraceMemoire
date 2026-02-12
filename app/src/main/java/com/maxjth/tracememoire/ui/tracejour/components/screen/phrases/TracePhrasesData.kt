// FILE: TracePhrasesData.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.phrases

import java.text.Normalizer

object TracePhrasesData {

    // ─────────────────────────────────────────────
    // NORMALISATION
    // ─────────────────────────────────────────────

    private fun normalize(raw: String): String {
        return Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z]".toRegex(), "")
    }

    private fun normalizeSliderKey(raw: String): String {
        val k = normalize(raw)
        return when {
            k.startsWith("humeur") -> "humeur"
            k.startsWith("energie") || k.startsWith("rythme") -> "energie"
            k.startsWith("corps") || k.startsWith("sensation") -> "corps"
            k.startsWith("presence") || k.startsWith("attention") || k.startsWith("focus") -> "presence"
            else -> k
        }
    }

    private fun normalizePhaseKey(raw: String): String {
        val k = normalize(raw)
        return when {
            k.startsWith("matin") -> "matin"
            k.startsWith("jour") || k.startsWith("midi") -> "jour"
            k.startsWith("soir") -> "soir"
            k.startsWith("nuit") -> "nuit"
            else -> "jour"
        }
    }

    // ─────────────────────────────────────────────
    // HUMEUR GLOBALE
    // ─────────────────────────────────────────────

    private val HUMEUR_MATIN = listOf(
        "Matin léger, esprit clair.",
        "Bonne dynamique intérieure.",
        "Sensation plutôt stable.",
        "Matin neutre, rien de marquant.",
        "Matin plus lourd, élan réduit.",
        "Matin dense, tout semble plus difficile."
    )

    private val HUMEUR_JOUR = listOf(
        "Journée fluide, tout s’enchaîne naturellement.",
        "Climat intérieur agréable, bonne stabilité.",
        "Journée globalement stable.",
        "Journée neutre, sans relief particulier.",
        "Journée plus lourde, effort constant.",
        "Journée difficile, sensation de tension."
    )

    private val HUMEUR_SOIR = listOf(
        "Soirée légère, esprit relâché.",
        "Bonne transition, état agréable.",
        "Soirée plutôt stable.",
        "Soirée neutre, rien de spécial.",
        "Soirée plus lourde, fatigue présente.",
        "Soirée difficile, tension persistante."
    )

    private val HUMEUR_NUIT = listOf(
        "Nuit calme, sensation apaisée.",
        "Bonne stabilité intérieure.",
        "Nuit correcte, sans perturbation notable.",
        "Nuit neutre, état passif.",
        "Nuit plus lourde, agitation de fond.",
        "Nuit difficile, esprit peu tranquille."
    )

    // ─────────────────────────────────────────────
    // ÉNERGIE / RYTHME
    // ─────────────────────────────────────────────

    private val ENERGIE_MATIN = listOf(
        "Énergie claire, mise en route facile.",
        "Bonne disponibilité mentale et physique.",
        "Rythme stable, fonctionnement correct.",
        "Énergie modérée, sans excès.",
        "Élan faible, démarrage plus lent.",
        "Manque d’énergie marqué, effort notable."
    )

    private val ENERGIE_JOUR = listOf(
        "Très bonne énergie, rythme naturel.",
        "Bonne dynamique générale.",
        "Énergie stable, fonctionnement régulier.",
        "Rythme neutre, sans variation notable.",
        "Énergie plus basse, fatigue diffuse.",
        "Journée énergétiquement difficile."
    )

    private val ENERGIE_SOIR = listOf(
        "Énergie encore confortable.",
        "Bonne réserve, soirée fluide.",
        "Niveau stable, sans tension.",
        "Énergie neutre, état calme.",
        "Fatigue présente, rythme ralenti.",
        "Énergie très basse, lourdeur marquée."
    )

    private val ENERGIE_NUIT = listOf(
        "État léger, récupération naturelle.",
        "Bonne détente globale.",
        "Niveau neutre, stable.",
        "Énergie basse mais normale pour la nuit.",
        "Agitation ou inconfort diffus.",
        "Nuit peu récupératrice."
    )

    // ─────────────────────────────────────────────
    // CORPS / SENSATIONS
    // ─────────────────────────────────────────────

    private val CORPS_MATIN = listOf(
        "Corps léger, sensations agréables.",
        "Bonne aisance physique.",
        "État corporel stable.",
        "Ressenti neutre.",
        "Corps plus lourd, légère tension.",
        "Inconfort corporel marqué."
    )

    private val CORPS_JOUR = listOf(
        "Très bon confort physique.",
        "Sensations globalement agréables.",
        "État stable et fonctionnel.",
        "Ressenti neutre, sans gêne notable.",
        "Tensions ou fatigue corporelle.",
        "Inconfort physique dominant."
    )

    private val CORPS_SOIR = listOf(
        "Corps détendu, sensations calmes.",
        "Bonne relâche physique.",
        "État stable.",
        "Ressenti neutre.",
        "Fatigue corporelle présente.",
        "Tensions physiques marquées."
    )

    private val CORPS_NUIT = listOf(
        "Corps calme, repos naturel.",
        "Bonne détente physique.",
        "État neutre et stable.",
        "Sensations passives.",
        "Inconfort léger ou diffus.",
        "Corps agité ou inconfortable."
    )

    // ─────────────────────────────────────────────
    // PRÉSENCE / ATTENTION
    // ─────────────────────────────────────────────

    private val PRESENCE_MATIN = listOf(
        "Esprit clair, attention fluide.",
        "Bonne présence mentale.",
        "Concentration stable.",
        "Attention neutre.",
        "Distraction facile, focus instable.",
        "Présence mentale difficile."
    )

    private val PRESENCE_JOUR = listOf(
        "Très bonne clarté mentale.",
        "Bonne capacité d’attention.",
        "Présence stable et fonctionnelle.",
        "Attention neutre.",
        "Difficulté légère à rester focus.",
        "Attention fragmentée, esprit dispersé."
    )

    private val PRESENCE_SOIR = listOf(
        "Esprit calme et présent.",
        "Bonne stabilité mentale.",
        "Attention stable.",
        "Présence neutre.",
        "Esprit plus diffus.",
        "Difficulté à maintenir l’attention."
    )

    private val PRESENCE_NUIT = listOf(
        "Esprit posé, calme mental.",
        "Bonne tranquillité intérieure.",
        "État neutre et passif.",
        "Présence légère.",
        "Agitation mentale diffuse.",
        "Esprit instable ou chargé."
    )

    // ─────────────────────────────────────────────
    // BUCKET % → INDEX (INVERSE)
    // ─────────────────────────────────────────────

    // ✅ 100% = meilleur (index 0)
    // ✅ 0%   = pire    (dernier index)
    private fun bucket(percent: Int, size: Int): Int {
        val p = percent.coerceIn(0, 100)
        val inverted = 100 - p
        return (inverted * size) / 101
    }

    // ─────────────────────────────────────────────
    // API PRINCIPALE
    // ─────────────────────────────────────────────

    fun phraseForSlider(sliderKey: String, phaseKey: String, percent: Int): String {
        val s = normalizeSliderKey(sliderKey)
        val p = normalizePhaseKey(phaseKey)

        val list = when (s) {
            "humeur" -> when (p) {
                "matin" -> HUMEUR_MATIN
                "jour" -> HUMEUR_JOUR
                "soir" -> HUMEUR_SOIR
                else -> HUMEUR_NUIT
            }

            "energie" -> when (p) {
                "matin" -> ENERGIE_MATIN
                "jour" -> ENERGIE_JOUR
                "soir" -> ENERGIE_SOIR
                else -> ENERGIE_NUIT
            }

            "corps" -> when (p) {
                "matin" -> CORPS_MATIN
                "jour" -> CORPS_JOUR
                "soir" -> CORPS_SOIR
                else -> CORPS_NUIT
            }

            else -> when (p) {
                "matin" -> PRESENCE_MATIN
                "jour" -> PRESENCE_JOUR
                "soir" -> PRESENCE_SOIR
                else -> PRESENCE_NUIT
            }
        }

        return list[bucket(percent, list.size)]
    }
}