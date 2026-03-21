package com.maxjth.tracememoire.ui.tracejour.components.screen.phrases

import java.text.Normalizer

object TracePhrasesData {

    data class PhraseItem(
        val text: String,
        val keyword: String
    )

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
            k.startsWith("presence") || k.startsWith("attention") -> "presence"

            k.startsWith("typejour") || k.startsWith("type") -> "typejour"
            k.startsWith("motifs") || k.startsWith("motif") -> "motifs"
            k.startsWith("environ") -> "environ"
            k.startsWith("clarte") -> "clarte"
            k.startsWith("charge") -> "charge"

            k.startsWith("emotion") -> "emotion"
            k.startsWith("repos") || k.startsWith("sommeil") -> "sommeil"

            else -> k
        }
    }

    private fun normalizePhaseKey(raw: String): String {
        val k = normalize(raw)
        return when {
            k.startsWith("matin") -> "matin"
            k.startsWith("jour") -> "jour"
            k.startsWith("soir") -> "soir"
            k.startsWith("nuit") -> "nuit"
            else -> "jour"
        }
    }

    // ───────────────── GRATUIT ─────────────────
    // HUMEUR
    // Keywords stables :
    // Légère / Agréable / Stable / Neutre / Lourde / Tendue

    private val HUMEUR_MATIN = listOf(
        PhraseItem("Matin léger, ça part bien.", "Légère"),
        PhraseItem("Matin agréable.", "Agréable"),
        PhraseItem("Matin stable.", "Stable"),
        PhraseItem("Matin neutre.", "Neutre"),
        PhraseItem("Matin plus lourd.", "Lourde"),
        PhraseItem("Matin tendu.", "Tendue")
    )

    private val HUMEUR_JOUR = listOf(
        PhraseItem("Journée légère, ça roule.", "Légère"),
        PhraseItem("Journée agréable.", "Agréable"),
        PhraseItem("Journée stable.", "Stable"),
        PhraseItem("Journée neutre.", "Neutre"),
        PhraseItem("Journée plus lourde.", "Lourde"),
        PhraseItem("Journée tendue.", "Tendue")
    )

    private val HUMEUR_SOIR = listOf(
        PhraseItem("Soirée légère.", "Légère"),
        PhraseItem("Soirée agréable.", "Agréable"),
        PhraseItem("Soirée stable.", "Stable"),
        PhraseItem("Soirée neutre.", "Neutre"),
        PhraseItem("Soirée plus lourde.", "Lourde"),
        PhraseItem("Soirée tendue.", "Tendue")
    )

    private val HUMEUR_NUIT = listOf(
        PhraseItem("Nuit légère.", "Légère"),
        PhraseItem("Nuit agréable.", "Agréable"),
        PhraseItem("Nuit stable.", "Stable"),
        PhraseItem("Nuit neutre.", "Neutre"),
        PhraseItem("Nuit plus lourde.", "Lourde"),
        PhraseItem("Nuit tendue.", "Tendue")
    )

    // ÉNERGIE
    // Keywords stables :
    // Fluide / Ouverte / Stable / Ralentie / Lourde / Bloquée

    private val ENERGIE_MATIN = listOf(
        PhraseItem("Ça circule bien dès le matin.", "Fluide"),
        PhraseItem("L’ambiance est ouverte.", "Ouverte"),
        PhraseItem("Ça tient normalement.", "Stable"),
        PhraseItem("Ça ralentit.", "Ralentie"),
        PhraseItem("C’est lourd dès le départ.", "Lourde"),
        PhraseItem("Ça bloque.", "Bloquée")
    )

    private val ENERGIE_JOUR = listOf(
        PhraseItem("Ça circule bien.", "Fluide"),
        PhraseItem("L’ambiance reste ouverte.", "Ouverte"),
        PhraseItem("Énergie stable.", "Stable"),
        PhraseItem("Ça ralentit un peu.", "Ralentie"),
        PhraseItem("C’est lourd.", "Lourde"),
        PhraseItem("Tout bloque.", "Bloquée")
    )

    private val ENERGIE_SOIR = listOf(
        PhraseItem("Ça redescend bien.", "Fluide"),
        PhraseItem("L’ambiance s’ouvre.", "Ouverte"),
        PhraseItem("Ça reste stable.", "Stable"),
        PhraseItem("Ça ralentit.", "Ralentie"),
        PhraseItem("C’est pesant.", "Lourde"),
        PhraseItem("Ça reste bloqué.", "Bloquée")
    )

    private val ENERGIE_NUIT = listOf(
        PhraseItem("Tout se calme.", "Fluide"),
        PhraseItem("L’air devient ouvert.", "Ouverte"),
        PhraseItem("Énergie stable.", "Stable"),
        PhraseItem("Le repos tarde.", "Ralentie"),
        PhraseItem("L’ambiance reste lourde.", "Lourde"),
        PhraseItem("Ça ne décroche pas.", "Bloquée")
    )

    // CORPS
    // Keywords stables :
    // Léger / Confortable / Stable / Fatigué / Tendu / Inconfortable

    private val CORPS_MATIN = listOf(
        PhraseItem("Corps léger.", "Léger"),
        PhraseItem("Corps confortable.", "Confortable"),
        PhraseItem("Corps stable.", "Stable"),
        PhraseItem("Fatigue légère.", "Fatigué"),
        PhraseItem("Corps tendu.", "Tendu"),
        PhraseItem("Corps inconfortable.", "Inconfortable")
    )

    private val CORPS_JOUR = listOf(
        PhraseItem("Corps léger.", "Léger"),
        PhraseItem("Bon confort.", "Confortable"),
        PhraseItem("État stable.", "Stable"),
        PhraseItem("Fatigue présente.", "Fatigué"),
        PhraseItem("Tensions présentes.", "Tendu"),
        PhraseItem("Inconfort marqué.", "Inconfortable")
    )

    private val CORPS_SOIR = listOf(
        PhraseItem("Corps relâché.", "Léger"),
        PhraseItem("Corps confortable.", "Confortable"),
        PhraseItem("État stable.", "Stable"),
        PhraseItem("Fatigue accumulée.", "Fatigué"),
        PhraseItem("Corps tendu.", "Tendu"),
        PhraseItem("Inconfort présent.", "Inconfortable")
    )

    private val CORPS_NUIT = listOf(
        PhraseItem("Corps relâché.", "Léger"),
        PhraseItem("Bonne détente.", "Confortable"),
        PhraseItem("État stable.", "Stable"),
        PhraseItem("Repos léger.", "Fatigué"),
        PhraseItem("Corps tendu.", "Tendu"),
        PhraseItem("Inconfort empêche de dormir.", "Inconfortable")
    )

    // PRÉSENCE
    // Keywords stables :
    // Clair / Présent / Stable / Flottant / Dispersé / Brouillé

    private val PRESENCE_MATIN = listOf(
        PhraseItem("Esprit clair.", "Clair"),
        PhraseItem("Bonne présence.", "Présent"),
        PhraseItem("Attention stable.", "Stable"),
        PhraseItem("Esprit flottant.", "Flottant"),
        PhraseItem("Attention dispersée.", "Dispersé"),
        PhraseItem("Esprit brouillé.", "Brouillé")
    )

    private val PRESENCE_JOUR = listOf(
        PhraseItem("Clarté mentale.", "Clair"),
        PhraseItem("Présence solide.", "Présent"),
        PhraseItem("Attention stable.", "Stable"),
        PhraseItem("Attention fluctuante.", "Flottant"),
        PhraseItem("Difficulté à rester concentré.", "Dispersé"),
        PhraseItem("Esprit brouillé.", "Brouillé")
    )

    private val PRESENCE_SOIR = listOf(
        PhraseItem("Esprit clair.", "Clair"),
        PhraseItem("Présence calme.", "Présent"),
        PhraseItem("Attention stable.", "Stable"),
        PhraseItem("Esprit plus flottant.", "Flottant"),
        PhraseItem("Attention dispersée.", "Dispersé"),
        PhraseItem("Esprit brouillé.", "Brouillé")
    )

    private val PRESENCE_NUIT = listOf(
        PhraseItem("Esprit calme.", "Clair"),
        PhraseItem("Présence tranquille.", "Présent"),
        PhraseItem("État stable.", "Stable"),
        PhraseItem("Pensées flottantes.", "Flottant"),
        PhraseItem("Esprit dispersé.", "Dispersé"),
        PhraseItem("Esprit brouillé.", "Brouillé")
    )

    // ───────────────── PREMIUM ─────────────────

    private val EMOTION = listOf(
        PhraseItem("État de plénitude.", "Plénitude"),
        PhraseItem("Joie présente.", "Joie"),
        PhraseItem("Élan positif.", "Positif"),
        PhraseItem("État stable.", "Stable"),
        PhraseItem("État sensible.", "Sensible"),
        PhraseItem("Tension émotionnelle.", "Tension"),
        PhraseItem("Fragilité présente.", "Fragile"),
        PhraseItem("Tristesse présente.", "Tristesse"),
        PhraseItem("État sombre.", "Sombre"),
        PhraseItem("Vide ressenti.", "Vide")
    )

    private val SOMMEIL = listOf(
        PhraseItem("Sommeil réparateur.", "Réparateur"),
        PhraseItem("Bonne nuit.", "Reposé"),
        PhraseItem("Sommeil stable.", "Stable"),
        PhraseItem("Sommeil correct.", "Correct"),
        PhraseItem("Repos léger.", "Léger"),
        PhraseItem("Sommeil instable.", "Instable"),
        PhraseItem("Nuit agitée.", "Agité"),
        PhraseItem("Sommeil fragmenté.", "Fragmenté"),
        PhraseItem("Nuit difficile.", "Difficile"),
        PhraseItem("Sommeil perturbé.", "Perturbé")
    )

    private val TYPEJOUR = listOf(
        PhraseItem("Journée fluide.", "Fluide"),
        PhraseItem("Journée bien structurée.", "Structurée"),
        PhraseItem("Journée stable.", "Stable"),
        PhraseItem("Journée variable.", "Variable"),
        PhraseItem("Journée chargée.", "Chargée"),
        PhraseItem("Journée désorganisée.", "Désorganisée")
    )

    private val MOTIFS = listOf(
        PhraseItem("Peu de remous en fond.", "Calme"),
        PhraseItem("Quelques pensées reviennent.", "Présent"),
        PhraseItem("Un motif se répète.", "Répétitif"),
        PhraseItem("Ça revient par moments.", "Insistant"),
        PhraseItem("Ça tourne davantage.", "Envahissant"),
        PhraseItem("Le motif prend toute la place.", "Dominant")
    )

    private val ENVIRON = listOf(
        PhraseItem("Environnement calme.", "Calme"),
        PhraseItem("Ambiance agréable.", "Agréable"),
        PhraseItem("Environnement neutre.", "Neutre"),
        PhraseItem("Environnement bruyant.", "Bruyant"),
        PhraseItem("Ambiance tendue.", "Tendu"),
        PhraseItem("Environnement lourd.", "Lourd")
    )

    private val CLARTE = listOf(
        PhraseItem("Clarté très nette.", "Très claire"),
        PhraseItem("Clarté présente.", "Claire"),
        PhraseItem("Clarté stable.", "Stable"),
        PhraseItem("Clarté plus floue.", "Floue"),
        PhraseItem("Pensée confuse.", "Confuse"),
        PhraseItem("Clarté brouillée.", "Brouillée")
    )

    private val CHARGE = listOf(
        PhraseItem("Charge légère.", "Légère"),
        PhraseItem("Charge bien gérée.", "Gérée"),
        PhraseItem("Charge stable.", "Stable"),
        PhraseItem("Charge présente.", "Présente"),
        PhraseItem("Charge lourde.", "Lourde"),
        PhraseItem("Charge écrasante.", "Écrasante")
    )

    // ───────────────── LOGIQUE ─────────────────

    private fun bucket(percent: Int, size: Int): Int {
        val p = percent.coerceIn(0, 100)
        val inverted = 100 - p
        return (inverted * size) / 101
    }

    private fun phraseListFor(sliderKey: String, phaseKey: String): List<PhraseItem> {
        val s = normalizeSliderKey(sliderKey)
        val p = normalizePhaseKey(phaseKey)

        return when (s) {
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

            "emotion" -> EMOTION
            "sommeil" -> SOMMEIL
            "typejour" -> TYPEJOUR
            "motifs" -> MOTIFS
            "environ" -> ENVIRON
            "clarte" -> CLARTE
            "charge" -> CHARGE

            else -> HUMEUR_JOUR
        }
    }

    fun phraseForSlider(sliderKey: String, phaseKey: String, percent: Int): String {
        val list = phraseListFor(sliderKey, phaseKey)
        return list[bucket(percent, list.size)].text
    }

    fun keywordForSlider(sliderKey: String, phaseKey: String, percent: Int): String {
        val list = phraseListFor(sliderKey, phaseKey)
        return list[bucket(percent, list.size)].keyword
    }
}