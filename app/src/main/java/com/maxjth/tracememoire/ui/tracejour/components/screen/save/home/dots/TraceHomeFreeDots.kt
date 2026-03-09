package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.dots

object TraceHomeFreeDots {

    fun getFreeDotsForCycle(
        cycleKey: String,
        maskMap: Map<String, Int>
    ): List<Boolean> {
        val mask = maskMap[cycleKey] ?: 0

        return listOf(
            (mask and (1 shl 0)) != 0,
            (mask and (1 shl 1)) != 0,
            (mask and (1 shl 2)) != 0,
            (mask and (1 shl 3)) != 0
        )
    }

    fun freeDotIndexForSliderKey(sliderKey: String): Int? {

        val s = sliderKey.trim().lowercase()

        val core = when {
            s.contains("humeur") -> "humeur"
            s.contains("energie") || s.contains("énergie") -> "energie"
            s.contains("corps") -> "corps"
            s.contains("presence") || s.contains("présence") -> "presence"
            else -> null
        } ?: return null

        return when (core) {
            "humeur" -> 0
            "energie" -> 1
            "corps" -> 2
            "presence" -> 3
            else -> null
        }
    }

}