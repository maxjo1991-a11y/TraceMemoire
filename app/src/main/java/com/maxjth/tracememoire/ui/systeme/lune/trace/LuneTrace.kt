package com.maxjth.tracememoire.ui.systeme.lune.trace

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class LuneTrace(
    val daySeed: String,
    val cycleKey: String,
    val timestamp: Long
) {

    /** ✅ clé cycle normalisée (stable pour stockage / comparaisons) */
    val normalizedCycleKey: String
        get() = cycleKey
            .trim()
            .uppercase()
            .replace("|", "_")
            .replace(" ", "_")
            .replace("__", "_")

    /** ✅ lecture directe temps humain (debug / UI / historique futur) */
    fun dateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            zoneId
        )
    }
}