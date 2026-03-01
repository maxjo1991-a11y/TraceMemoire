// FILE: app/src/main/java/com/maxjth/tracememoire/ui/noyau/lois/HomeScoreLogic.kt
package com.maxjth.tracememoire.ui.noyau.lois

import kotlin.math.roundToInt

/**
 * HomeScoreLogic
 *
 * But :
 * - Calculer un % "Home" propre (0..100) à partir de plusieurs axes (ex: Écran 2).
 * - Évolutif : inclure / exclure les axes Premium sans casser le calcul.
 * - Tolérant : si certains axes sont null / pas remplis, on calcule avec ce qui existe.
 *
 * IMPORTANT :
 * - AUCUN lien direct ici avec l'UI (pas de Compose).
 * - AUCUN accès DB ici : ce fichier ne fait que des calculs.
 */
object HomeScoreLogic {

    // ✅ Clés (faciles à reconnaître)
    const val AXIS_GLOBAL = "global"
    const val AXIS_ENERGY = "energy"
    const val AXIS_BODY = "body"
    const val AXIS_PRESENCE = "presence"

    // ✅ Premium (pour plus tard)
    const val AXIS_PREMIUM_DAYTYPE = "premium_daytype"
    const val AXIS_PREMIUM_PATTERNS = "premium_patterns"
    const val AXIS_PREMIUM_ENV = "premium_env"
    const val AXIS_PREMIUM_CLARITY = "premium_clarity"
    const val AXIS_PREMIUM_EMOLOAD = "premium_emoload"

    // ✅ Set pratique (si tu veux marquer Premium facilement ailleurs)
    val DEFAULT_PREMIUM_KEYS: Set<String> = setOf(
        AXIS_PREMIUM_DAYTYPE,
        AXIS_PREMIUM_PATTERNS,
        AXIS_PREMIUM_ENV,
        AXIS_PREMIUM_CLARITY,
        AXIS_PREMIUM_EMOLOAD
    )

    data class AxisScore(
        val key: String,
        val percent: Int?,            // null = pas renseigné
        val weight: Float = 1f,       // poids par défaut
        val isPremium: Boolean = false
    )

    data class HomeScoreResult(
        val percent: Int?,            // null si rien de calculable
        val usedAxesCount: Int,       // combien d'axes ont servi au calcul
        val ignoredAxesCount: Int,    // combien ont été ignorés (null / exclu)
        val includePremiumAxes: Boolean
    )

    /**
     * Calcul principal : moyenne pondérée (arrondie) des axes disponibles.
     *
     * Règles :
     * - percent doit être 0..100 sinon clamp.
     * - weight <= 0 => axe ignoré (sécurité).
     * - si includePremiumAxes = false => on ignore isPremium = true.
     * - si aucun axe valide => percent = null.
     */
    fun computeHomeScore(
        axes: List<AxisScore>,
        includePremiumAxes: Boolean = true
    ): HomeScoreResult {
        var sum = 0f
        var wsum = 0f
        var used = 0
        var ignored = 0

        axes.forEach { a ->
            if (!includePremiumAxes && a.isPremium) {
                ignored++
                return@forEach
            }

            val w = a.weight
            val p = a.percent

            if (w <= 0f || p == null) {
                ignored++
                return@forEach
            }

            val pc = clampPercent(p)
            sum += pc * w
            wsum += w
            used++
        }

        val resultPercent =
            if (used == 0 || wsum <= 0f) null
            else (sum / wsum).roundToInt().coerceIn(0, 100)

        return HomeScoreResult(
            percent = resultPercent,
            usedAxesCount = used,
            ignoredAxesCount = ignored,
            includePremiumAxes = includePremiumAxes
        )
    }

    /**
     * Helper ultra simple (quand tu as juste des Int?).
     * Exemple : computeHomeScoreFromRaw(global, energy, body, presence)
     */
    fun computeHomeScoreFromRaw(
        global: Int?,
        energy: Int?,
        body: Int?,
        presence: Int?,
        includePremiumAxes: Boolean = true
    ): HomeScoreResult {
        val axes = listOf(
            AxisScore(key = AXIS_GLOBAL, percent = global, weight = 1f, isPremium = false),
            AxisScore(key = AXIS_ENERGY, percent = energy, weight = 1f, isPremium = false),
            AxisScore(key = AXIS_BODY, percent = body, weight = 1f, isPremium = false),
            AxisScore(key = AXIS_PRESENCE, percent = presence, weight = 1f, isPremium = false)
        )
        return computeHomeScore(axes = axes, includePremiumAxes = includePremiumAxes)
    }

    /**
     * Variante "map" (quand tu as un dictionnaire d'axes).
     * weights optionnel : si absent, poids = 1f.
     * premiumKeys optionnel : set des clés Premium.
     */
    fun computeHomeScoreFromMap(
        percents: Map<String, Int?>,
        weights: Map<String, Float> = emptyMap(),
        premiumKeys: Set<String> = emptySet(),
        includePremiumAxes: Boolean = true
    ): HomeScoreResult {
        val axes = percents.map { (key, p) ->
            AxisScore(
                key = key,
                percent = p,
                weight = (weights[key] ?: 1f),
                isPremium = premiumKeys.contains(key)
            )
        }
        return computeHomeScore(axes = axes, includePremiumAxes = includePremiumAxes)
    }

    /**
     * ✅ Helper pratique :
     * - Si rien n'est calculable => retourne 0 (au lieu de null)
     */
    fun computeHomePercentOrZero(
        axes: List<AxisScore>,
        includePremiumAxes: Boolean = true
    ): Int {
        return computeHomeScore(axes, includePremiumAxes).percent ?: 0
    }

    /**
     * ✅ NOUVEAU (Terre) :
     * Calcul annuel simple à partir d'une liste de scores journaliers.
     * - Ignore les nulls
     * - Clamp 0..100
     * - Retourne null si aucune donnée
     */
    fun computeYearlyPercent(
        dailyPercents: List<Int?>
    ): Int? {
        val values = dailyPercents
            .filterNotNull()
            .map { clampPercent(it) }

        if (values.isEmpty()) return null

        val avg = values.average()
        return avg.roundToInt().coerceIn(0, 100)
    }

    private fun clampPercent(v: Int): Int = v.coerceIn(0, 100)
}