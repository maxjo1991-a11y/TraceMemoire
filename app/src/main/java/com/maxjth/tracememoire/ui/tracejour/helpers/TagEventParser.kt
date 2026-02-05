package com.maxjth.tracememoire.ui.tracejour.helpers

/**
 * Parse les événements TAG_ON / TAG_OFF
 * Format officiel :
 * TAG_ON|11:25|Calme
 * TAG_OFF|21:40|Calme
 */
object TagEventParser {

    data class ParsedTagEvent(
        val action: TagAction,
        val tag: String,
        val hour: String
    )

    enum class TagAction {
        ON, OFF
    }

    fun parse(raw: String, fallbackHour: String): ParsedTagEvent? {
        val parts = raw.split("|", limit = 3)

        // Format moderne : TAG_ON|11:25|Calme
        if (parts.size == 3) {
            val action = when (parts[0]) {
                "TAG_ON" -> TagAction.ON
                "TAG_OFF" -> TagAction.OFF
                else -> return null
            }

            return ParsedTagEvent(
                action = action,
                hour = parts[1],
                tag = parts[2]
            )
        }

        // Ancien format : TAG_ON|Calme
        if (parts.size == 2) {
            val action = when (parts[0]) {
                "TAG_ON" -> TagAction.ON
                "TAG_OFF" -> TagAction.OFF
                else -> return null
            }

            return ParsedTagEvent(
                action = action,
                hour = fallbackHour,
                tag = parts[1]
            )
        }

        return null
    }
}

