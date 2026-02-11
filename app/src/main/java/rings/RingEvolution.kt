package rings

import kotlin.math.PI
import kotlin.math.sin

/**
 * RingEvolution
 *
 * But:
 * - Donner un RingBreathSpec stable et cohérent
 * - La "personnalité" dépend seulement de (seedBase + cycleKey + ringKey)
 * - Le % (slider) ne doit JAMAIS changer la spec (mode stable)
 *
 * AUCUN import Compose ici.
 */
object RingEvolution {

    /**
     * API principale (mode stable)
     */
    fun specForKey(
        seedBase: String,   // ex: "20260210"
        cycleKey: String,   // ex: "MORNING" / "SOIR" / "NUIT"
        ringKey: String     // ex: "main" / "energy" / "focus" etc.
    ): RingBreathSpec {
        val seed = stableHash("$seedBase|$cycleKey|$ringKey")
        return specForIndex(seed)
    }

    /**
     * Génère une spec à partir d’un index stable (int).
     * Ici tu peux ajuster les ranges sans casser l’API.
     */
    fun specForIndex(index: Int): RingBreathSpec {
        // t dans 0..1 stable
        val t = ((index % 14) / 14f).coerceIn(0f, 1f)

        // onde douce -1..1
        val wave = sin((t * 2f * PI).toFloat())

        // helpers
        fun lerp(a: Float, b: Float, f: Float) = a + (b - a) * f
        fun clamp(x: Float, lo: Float, hi: Float) = x.coerceIn(lo, hi)

        // Respiration (ms)
        val breatheMs = (lerp(2050f, 2600f, (t + 1f) / 2f)).toInt()

        // Wobble (ms)
        val wobbleMs = (lerp(7800f, 11200f, (1f - t))).toInt()

        // Alpha (base + amplitude)
        val baseAlpha = clamp(lerp(0.28f, 0.40f, (t + 1f) / 2f), 0.18f, 0.55f)
        val ampAlpha  = clamp(lerp(0.14f, 0.26f, (1f - t)), 0.08f, 0.30f)

        // Scale amplitude
        val ampScale = clamp(lerp(0.018f, 0.036f, (t + 1f) / 2f), 0.012f, 0.050f)

        // Wobble amplitude (rayon)
        val wobbleAmp = clamp(lerp(0.010f, 0.020f, ((wave + 1f) / 2f)), 0.006f, 0.030f)

        // Epaisseur (diamètre * ratio)
        val strokeBase = clamp(lerp(0.012f, 0.017f, (t + 1f) / 2f), 0.008f, 0.028f)
        val strokeAmp  = clamp(lerp(0.004f, 0.008f, (1f - t)), 0.002f, 0.012f)

        return RingBreathSpec(
            breatheMs = breatheMs,
            wobbleMs = wobbleMs,
            baseAlpha = baseAlpha,
            ampAlpha = ampAlpha,
            ampScale = ampScale,
            wobbleAmp = wobbleAmp,
            strokeRatioBase = strokeBase,
            strokeRatioAmp = strokeAmp
        )
    }

    /**
     * Hash stable -> int positif
     * (pas crypto, juste stable et déterministe)
     */
    private fun stableHash(s: String): Int {
        var h = 1125899907L
        for (c in s) h = 31L * h + c.code
        return (h and 0x7fffffffL).toInt()
    }
}