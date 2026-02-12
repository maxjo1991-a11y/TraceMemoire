package com.maxjth.tracememoire.ui.tracejour.components.screen.phrases

// ─────────────────────────────────────────
// Cycles officiels (jour / soir / nuit)
// ─────────────────────────────────────────
enum class TraceCycle(val key: String) {
    JOUR("jour"),
    SOIR("soir"),
    NUIT("nuit");

    companion object {
        fun fromKey(raw: String?): TraceCycle {
            return when (raw?.lowercase()?.trim()) {
                "soir" -> SOIR
                "nuit" -> NUIT
                else -> JOUR
            }
        }
    }
}

// ─────────────────────────────────────────
// Clés stables des sliders
// ─────────────────────────────────────────
object TraceSliderKeys {
    const val HUMEUR = "humeur"
    const val ENERGIE = "energie"
    const val CORPS = "corps"
    const val PRESENCE = "presence"
}

// ─────────────────────────────────────────
// Mapping officiel : Catégorie × Cycle
// 4 catégories × 3 cycles = 12 phrases
// Ton : accompagnant, neutre, non jugeant
// ─────────────────────────────────────────
object TraceSliderPhrases {

    fun phraseFor(sliderKey: String, cycleKey: String): String {
        val cycle = TraceCycle.fromKey(cycleKey)

        return when (sliderKey.lowercase()) {

            // 🧠 HUMEUR GLOBALE
            TraceSliderKeys.HUMEUR -> when (cycle) {
                TraceCycle.JOUR ->
                    "Voici comment ton état s’est manifesté aujourd’hui."
                TraceCycle.SOIR ->
                    "Voici comment ton humeur se dépose en fin de journée."
                TraceCycle.NUIT ->
                    "Voici l’état dans lequel ta journée se termine."
            }

            // ⚡ ÉNERGIE / RYTHME
            TraceSliderKeys.ENERGIE -> when (cycle) {
                TraceCycle.JOUR ->
                    "Voici le rythme avec lequel ta journée s’est déroulée."
                TraceCycle.SOIR ->
                    "Voici comment ton énergie a évolué aujourd’hui."
                TraceCycle.NUIT ->
                    "Voici le niveau d’énergie avec lequel tu arrives au repos."
            }

            // 🫀 CORPS / SENSATIONS
            TraceSliderKeys.CORPS -> when (cycle) {
                TraceCycle.JOUR ->
                    "Voici comment ton corps s’est fait sentir aujourd’hui."
                TraceCycle.SOIR ->
                    "Voici ce que ton corps a exprimé au fil de la journée."
                TraceCycle.NUIT ->
                    "Voici l’état physique dans lequel tu te poses maintenant."
            }

            // 🌫 PRÉSENCE / ATTENTION
            TraceSliderKeys.PRESENCE -> when (cycle) {
                TraceCycle.JOUR ->
                    "Voici ton niveau de présence au fil de la journée."
                TraceCycle.SOIR ->
                    "Voici comment ton attention s’est maintenue aujourd’hui."
                TraceCycle.NUIT ->
                    "Voici avec quel degré de présence la journée se ferme."
            }

            // Fallback de sécurité
            else -> when (cycle) {
                TraceCycle.JOUR ->
                    "Voici comment cette journée s’est présentée."
                TraceCycle.SOIR ->
                    "Voici comment la journée se dépose maintenant."
                TraceCycle.NUIT ->
                    "Voici l’état dans lequel tu te poses."
            }
        }
    }
}