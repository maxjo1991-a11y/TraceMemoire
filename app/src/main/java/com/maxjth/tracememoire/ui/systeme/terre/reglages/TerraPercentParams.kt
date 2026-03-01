package com.maxjth.tracememoire.ui.systeme.terre.reglages

data class TerrePercentParams(
    // Size / text
    val defaultSizeDp: Int = 116,
    val defaultNumberScale: Float = 0.78f,
    val showPercentSymbol: Boolean = true,

    // Breath / motion tuning
    val breathSlowFactor: Float = 1.25f,
    val microDriftStrength: Float = 0.0048f,
    val eyeXStrength: Float = 0.014f,
    val eyeYStrength: Float = 0.038f,

    // Stroke
    val strokeBaseDp: Float = 6f,
    val strokeBoostDp: Float = 1.4f,

    // Orbit
    val orbitDurationFactor: Float = 7.5f,
    val orbitRadiusMinDp: Float = 1.6f,
    val orbitRadiusMaxDp: Float = 2.4f,
    val orbitYScale: Float = 0.70f,

    // Tilt
    val tiltDurationFactor: Float = 9.0f,
    val tiltMinDeg: Float = 0.16f,
    val tiltMaxDeg: Float = 0.28f,

    // Global light attenuation (so it doesn't steal the sun)
    val lightBoost: Float = 0.82f,

    // Color mixing
    val vividLerpToWhite: Float = 0.22f,

    // Neon / ring alphas (pre-lightBoost is applied inside circle)
    val neonGlowAlpha: Float = 0.14f,
    val neonEdgeAlpha: Float = 0.82f,
    val ringBaseAlpha: Float = 0.26f,
    val ringBrightAlpha: Float = 0.88f,

    // Halo
    val haloBase: Float = 0.016f,
    val haloBoost: Float = 0.030f,
    val haloOuter: Float = 1.14f,
    val haloMid: Float = 1.06f,

    // Iris / pupil ratios
    val irisRRatio: Float = 0.70f,
    val lensCxRatio: Float = 0.26f,
    val lensCyRatio: Float = 0.28f,
    val lensRadiusRatio: Float = 0.40f,
    val starsRRatio: Float = 0.78f,

    val pupilRRatio: Float = 0.42f,
    val pupilGlowRadiusRatio: Float = 1.20f,
    val pupilGlowDrawRatio: Float = 1.25f,

    // Stars
    val starCount: Int = 8,
    val starAlphaMin: Float = 0.05f,
    val starAlphaVar: Float = 0.08f,
    val starAlphaScale: Float = 0.50f,
    val starAlphaClampMin: Float = 0.02f,
    val starAlphaClampMax: Float = 0.06f,
    val starRadiusRatio: Float = 0.010f,
    val starRadiusMinPx: Float = 1.1f,

    // Glow widths
    val neonGlowExtraPx: Float = 7f,
    val neonEdgeExtraPx: Float = 1.6f,
)