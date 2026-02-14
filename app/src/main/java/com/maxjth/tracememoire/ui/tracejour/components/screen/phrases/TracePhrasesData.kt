// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/phrases/TracePhrasesData.kt
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
            // FREE
            k.startsWith("humeur") -> "humeur"
            k.startsWith("energie") || k.startsWith("rythme") -> "energie"
            k.startsWith("corps") || k.startsWith("sensation") -> "corps"
            k.startsWith("presence") || k.startsWith("attention") || k.startsWith("focus") -> "presence"

            // PREMIUM (déjà dans SlidersBlock)
            k.startsWith("typejour") || k.startsWith("type") -> "typejour"
            k.startsWith("motifs") || k.startsWith("motif") -> "motifs"
            k.startsWith("environ") || k.startsWith("environnement") -> "environ"
            k.startsWith("clarte") -> "clarte"
            k.startsWith("chargeemotionnelle") || k.startsWith("charge") -> "charge"

            // PREMIUM (nouveaux)
            k.startsWith("architectureemotionnelle") || k.startsWith("emotions") || k.startsWith("emotion") -> "emotion"
            k.startsWith("qualitedureposvecu") || k.startsWith("repos") || k.startsWith("sommeil") -> "sommeil"

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
    // FREE — HUMEUR GLOBALE
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
    // FREE — ÉNERGIE / RYTHME
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
    // FREE — CORPS / SENSATIONS
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
    // FREE — PRÉSENCE / ATTENTION
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
    // PREMIUM — ÉMOTION (sans cycle)
    // ─────────────────────────────────────────────

    private val EMOTION = listOf(
        "Plénitude intérieure.",
        "Joie présente.",
        "Élan positif.",
        "Stabilité émotionnelle.",
        "État sensible.",
        "Tension émotionnelle.",
        "Fragilité intérieure.",
        "Tristesse diffuse.",
        "État sombre.",
        "Vide émotionnel."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — SOMMEIL / REPOS (sans cycle)
    // ─────────────────────────────────────────────

    private val SOMMEIL = listOf(
        "Sommeil profondément réparateur.",
        "Repos très stable, récupération nette.",
        "Bonne nuit, continuité naturelle.",
        "Sommeil correct, récupération présente.",
        "Repos léger, récupération partielle.",
        "Sommeil instable, micro-réveils possibles.",
        "Nuit agitée, récupération limitée.",
        "Sommeil fragmenté, fatigue persistante.",
        "Nuit difficile, repos peu efficace.",
        "Cauchemars ou sommeil perturbé."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — TYPE DE JOURNÉE (avec cycle)
    // ─────────────────────────────────────────────

    private val TYPEJOUR_MATIN = listOf(
        "Départ fluide, intention claire.",
        "Début stable, bonne tenue.",
        "Mise en route correcte.",
        "Début neutre, sans direction nette.",
        "Départ lourd, résistance intérieure.",
        "Début difficile, charge immédiate."
    )

    private val TYPEJOUR_JOUR = listOf(
        "Journée simple, direction nette.",
        "Journée structurée, bonne continuité.",
        "Journée correcte, tenue générale.",
        "Journée neutre, sans relief.",
        "Journée lourde, effort constant.",
        "Journée difficile, tenue fragile."
    )

    private val TYPEJOUR_SOIR = listOf(
        "Soirée légère, relâche facile.",
        "Soirée stable, bonne continuité.",
        "Soirée correcte, tenue présente.",
        "Soirée neutre, sans relief.",
        "Soirée lourde, tension persistante.",
        "Soirée difficile, charge dominante."
    )

    private val TYPEJOUR_NUIT = listOf(
        "Nuit calme, décompression réelle.",
        "Nuit stable, retombée douce.",
        "Nuit correcte, état posé.",
        "Nuit neutre, état passif.",
        "Nuit lourde, rumination possible.",
        "Nuit difficile, décompression absente."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — MOTIFS PSYCHIQUES (avec cycle)
    // ─────────────────────────────────────────────

    private val MOTIFS_MATIN = listOf(
        "Pensée nette, axe intérieur clair.",
        "Bonne cohérence mentale.",
        "Fil mental stable.",
        "Pensée neutre, sans axe fort.",
        "Pensée lourde, friction intérieure.",
        "Pensée envahissante, charge mentale forte."
    )

    private val MOTIFS_JOUR = listOf(
        "Clarté psychique, direction facile.",
        "Bonne cohérence interne.",
        "Stabilité mentale fonctionnelle.",
        "Neutre, mécanique du jour.",
        "Charge mentale, répétitions présentes.",
        "Boucles mentales, tension psychique."
    )

    private val MOTIFS_SOIR = listOf(
        "Relâche mentale, calme interne.",
        "Bonne stabilité intérieure.",
        "Pensée stable.",
        "Neutre, sans relief.",
        "Retour de tensions, ruminations.",
        "Envahissement mental, charge forte."
    )

    private val MOTIFS_NUIT = listOf(
        "Silence mental, apaisement.",
        "Nuit stable, esprit posé.",
        "État neutre, pensées rares.",
        "Neutre, passage lent.",
        "Agitation mentale diffuse.",
        "Nuit envahie, pensées persistantes."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — ENVIRONNEMENT (avec cycle)
    // ─────────────────────────────────────────────

    private val ENVIRON_MATIN = listOf(
        "Environnement soutenant.",
        "Cadre favorable.",
        "Cadre correct, neutre.",
        "Cadre neutre, sans effet.",
        "Cadre pesant.",
        "Environnement difficile à porter."
    )

    private val ENVIRON_JOUR = listOf(
        "Cadre très soutenant.",
        "Cadre stable, bien géré.",
        "Cadre correct.",
        "Cadre neutre.",
        "Cadre lourd, irritant.",
        "Cadre hostile ou épuisant."
    )

    private val ENVIRON_SOIR = listOf(
        "Cadre apaisant.",
        "Cadre stable.",
        "Cadre correct.",
        "Cadre neutre.",
        "Cadre lourd.",
        "Cadre difficile, pression présente."
    )

    private val ENVIRON_NUIT = listOf(
        "Cadre calme, sécurisant.",
        "Cadre stable.",
        "Cadre correct.",
        "Cadre neutre.",
        "Cadre pesant.",
        "Cadre perturbant ou oppressant."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — CLARTÉ MENTALE (avec cycle)
    // ─────────────────────────────────────────────

    private val CLARTE_MATIN = listOf(
        "Clarté nette, esprit lumineux.",
        "Bonne clarté mentale.",
        "Clarté stable.",
        "Clarté neutre.",
        "Brume mentale présente.",
        "Esprit embrouillé, clarté faible."
    )

    private val CLARTE_JOUR = listOf(
        "Esprit très clair, décisions faciles.",
        "Bonne clarté, pensée fluide.",
        "Clarté stable.",
        "Neutre, sans netteté.",
        "Brume mentale, effort cognitif.",
        "Confusion mentale, charge cognitive."
    )

    private val CLARTE_SOIR = listOf(
        "Esprit clair malgré la fin du jour.",
        "Bonne clarté.",
        "Clarté stable.",
        "Neutre.",
        "Brume mentale.",
        "Esprit confus, clarté faible."
    )

    private val CLARTE_NUIT = listOf(
        "Nuit claire, esprit posé.",
        "Bonne clarté intérieure.",
        "État stable.",
        "Neutre.",
        "Brume mentale diffuse.",
        "Esprit instable, clarté absente."
    )

    // ─────────────────────────────────────────────
    // PREMIUM — CHARGE ÉMOTIONNELLE (avec cycle)
    // ─────────────────────────────────────────────

    private val CHARGE_MATIN = listOf(
        "Charge très basse, légèreté intérieure.",
        "Charge faible, stable.",
        "Charge modérée, gérable.",
        "Charge neutre.",
        "Charge lourde, pression interne.",
        "Charge écrasante, poids immédiat."
    )

    private val CHARGE_JOUR = listOf(
        "Charge très basse, respiration facile.",
        "Charge faible, bonne tenue.",
        "Charge modérée, gérable.",
        "Charge neutre.",
        "Charge lourde, effort constant.",
        "Charge écrasante, poids dominant."
    )

    private val CHARGE_SOIR = listOf(
        "Charge basse, relâche facile.",
        "Charge faible.",
        "Charge modérée.",
        "Neutre.",
        "Charge lourde, tension persistante.",
        "Charge écrasante, saturation."
    )

    private val CHARGE_NUIT = listOf(
        "Charge basse, nuit douce.",
        "Charge faible, repos possible.",
        "Charge modérée.",
        "Neutre.",
        "Charge lourde, agitation.",
        "Charge écrasante, nuit difficile."
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

            // FREE
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

            "presence" -> when (p) {
                "matin" -> PRESENCE_MATIN
                "jour" -> PRESENCE_JOUR
                "soir" -> PRESENCE_SOIR
                else -> PRESENCE_NUIT
            }

            // PREMIUM — sans cycle
            "emotion" -> EMOTION
            "sommeil" -> SOMMEIL

            // PREMIUM — avec cycle
            "typejour" -> when (p) {
                "matin" -> TYPEJOUR_MATIN
                "jour" -> TYPEJOUR_JOUR
                "soir" -> TYPEJOUR_SOIR
                else -> TYPEJOUR_NUIT
            }

            "motifs" -> when (p) {
                "matin" -> MOTIFS_MATIN
                "jour" -> MOTIFS_JOUR
                "soir" -> MOTIFS_SOIR
                else -> MOTIFS_NUIT
            }

            "environ" -> when (p) {
                "matin" -> ENVIRON_MATIN
                "jour" -> ENVIRON_JOUR
                "soir" -> ENVIRON_SOIR
                else -> ENVIRON_NUIT
            }

            "clarte" -> when (p) {
                "matin" -> CLARTE_MATIN
                "jour" -> CLARTE_JOUR
                "soir" -> CLARTE_SOIR
                else -> CLARTE_NUIT
            }

            "charge" -> when (p) {
                "matin" -> CHARGE_MATIN
                "jour" -> CHARGE_JOUR
                "soir" -> CHARGE_SOIR
                else -> CHARGE_NUIT
            }

            else -> HUMEUR_JOUR // fallback safe
        }

        return list[bucket(percent, list.size)]
    }
}