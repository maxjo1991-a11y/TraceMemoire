package com.maxjth.tracememoire.ui.tracejour.components.screen.save.logic

import com.maxjth.tracememoire.ui.noyau.lois.HomeScoreLogic

object TraceHomeScoreLogic {

    private const val SLIDER_HUMEUR = "humeur"
    private const val SLIDER_ENERGIE = "energie"
    private const val SLIDER_CORPS = "corps"
    private const val SLIDER_PRESENCE = "presence"

    private const val SLIDER_REPOS = "repos"
    private const val SLIDER_ARCHI_EMO = "archi_emo"
    private const val SLIDER_TYPE_JOURNEE = "type_journee"
    private const val SLIDER_MOTIFS = "motifs_psy"
    private const val SLIDER_ENVIRONNEMENT = "environnement"
    private const val SLIDER_CLARTE = "clarte"

    fun isPremiumKey(key: String): Boolean {
        return key == SLIDER_REPOS ||
                key == SLIDER_ARCHI_EMO ||
                key == SLIDER_TYPE_JOURNEE ||
                key == SLIDER_MOTIFS ||
                key == SLIDER_ENVIRONNEMENT ||
                key == SLIDER_CLARTE
    }

    fun computePercent(
        sliderMap: Map<String, Int>,
        premiumTouchedToday: Boolean,
        includePremiumAxes: Boolean = true
    ): Int? {
        return recomputeHomeScoreFromSliders(
            sliderMap = sliderMap,
            premiumTouchedToday = premiumTouchedToday,
            includePremiumAxes = includePremiumAxes
        )
    }

    fun recomputeHomeScoreFromSliders(
        sliderMap: Map<String, Int>,
        premiumTouchedToday: Boolean,
        includePremiumAxes: Boolean = true
    ): Int? {

        val baseGlobal = sliderMap[SLIDER_HUMEUR]
        val baseEnergy = sliderMap[SLIDER_ENERGIE]
        val baseBody = sliderMap[SLIDER_CORPS]
        val basePresence = sliderMap[SLIDER_PRESENCE]

        // ✅ RÈGLE IMPORTANTE :
        // si aucune base n’est touchée/renseignée, on retourne 0
        val noBaseData =
            baseGlobal == null &&
                    baseEnergy == null &&
                    baseBody == null &&
                    basePresence == null

        if (noBaseData) {
            return 0
        }

        var global = baseGlobal
        var energy = baseEnergy
        var body = baseBody
        var presence = basePresence

        val allowPremiumInfluence = includePremiumAxes && premiumTouchedToday

        if (allowPremiumInfluence) {
            val pRepos = sliderMap[SLIDER_REPOS]
            val pArchi = sliderMap[SLIDER_ARCHI_EMO]
            val pType = sliderMap[SLIDER_TYPE_JOURNEE]
            val pMotifs = sliderMap[SLIDER_MOTIFS]
            val pEnv = sliderMap[SLIDER_ENVIRONNEMENT]
            val pClarte = sliderMap[SLIDER_CLARTE]

            val premiumValues = listOf(
                pRepos, pArchi, pType, pMotifs, pEnv, pClarte
            ).filterNotNull()

            val premiumAvg = premiumValues
                .takeIf { it.isNotEmpty() }
                ?.average()
                ?.toInt()

            energy = blendOrKeep(energy, pRepos, 0.14f)
            presence = blendOrKeep(presence, pClarte, 0.14f)
            body = blendOrKeep(body, pEnv, 0.10f)

            global = blendOrKeep(global, premiumAvg, 0.12f)
            global = blendOrKeep(global, pArchi, 0.06f)
            global = blendOrKeep(global, pMotifs, 0.06f)
            global = blendOrKeep(global, pType, 0.06f)
        }

        val result = HomeScoreLogic.computeHomeScoreFromRaw(
            global = global,
            energy = energy,
            body = body,
            presence = presence,
            includePremiumAxes = allowPremiumInfluence
        )

        // ✅ GARDE-FOU SORTIE MOTEUR :
        // même si un calcul évolue plus tard, la sortie finale reste bornée
        return (result.percent ?: 0).coerceIn(0, 100)
    }

    private fun blendOrKeep(
        base: Int?,
        target: Int?,
        alpha: Float
    ): Int? {
        if (base == null) return null
        if (target == null) return base

        val a = alpha.coerceIn(0f, 1f)
        val out = (base * (1f - a) + target * a).toInt()
        return out.coerceIn(0, 100)
    }
}