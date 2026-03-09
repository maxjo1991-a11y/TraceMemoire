package com.maxjth.tracememoire.ui.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormat {

    private val HH_MM: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale.CANADA_FRENCH)

    fun nowHour(now: LocalDateTime = LocalDateTime.now()): String {
        return now.format(HH_MM)
    }
}