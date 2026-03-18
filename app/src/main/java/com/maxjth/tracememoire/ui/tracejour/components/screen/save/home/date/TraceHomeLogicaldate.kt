package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.date

import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * TraceHomeLogicalDate
 *
 * Centralise la date logique HOME.
 *
 * Règle verrouillée :
 * - 00:00 -> 05:59 = encore la veille logique
 * - 06:00 -> 23:59 = journée logique actuelle
 *
 * Usage :
 * - savoir à quelle journée HOME rattacher une trace
 * - calculer le seed logique HOME
 * - calculer la veille logique HOME
 */
object TraceHomeLogicalDate {

    private val DAY_START_TIME: LocalTime = LocalTime.of(6, 0)

    /**
     * Retourne la date logique HOME à partir d'un LocalDateTime.
     */
    fun effectiveDate(
        now: LocalDateTime = LocalDateTime.now()
    ): LocalDate {
        return if (now.toLocalTime().isBefore(DAY_START_TIME)) {
            now.toLocalDate().minusDays(1)
        } else {
            now.toLocalDate()
        }
    }

    /**
     * Retourne la date logique HOME à partir du fuseau courant.
     */
    fun effectiveDate(
        zoneId: ZoneId = ZoneId.systemDefault()
    ): LocalDate {
        val now = LocalDateTime.now(zoneId)
        return effectiveDate(now)
    }

    /**
     * Retourne le seed logique HOME à partir d’un seedBase.
     */
    fun effectiveSeed(
        seedBase: String,
        now: LocalDateTime = LocalDateTime.now()
    ): String {
        return TraceJourPrefs.seedForDate(seedBase, effectiveDate(now))
    }

    /**
     * Retourne la veille logique HOME à partir du fuseau courant.
     */
    fun logicalYesterday(
        zoneId: ZoneId = ZoneId.systemDefault()
    ): LocalDate {
        return effectiveDate(zoneId).minusDays(1)
    }

    /**
     * Retourne la veille logique HOME à partir d’un LocalDateTime.
     */
    fun logicalYesterday(
        now: LocalDateTime
    ): LocalDate {
        return effectiveDate(now).minusDays(1)
    }

    /**
     * Indique si l’instant fourni appartient encore à la veille logique.
     */
    fun isStillPreviousLogicalDay(
        now: LocalDateTime = LocalDateTime.now()
    ): Boolean {
        return now.toLocalTime().isBefore(DAY_START_TIME)
    }

    /**
     * Heure officielle de début de journée logique HOME.
     */
    fun dayStartTime(): LocalTime = DAY_START_TIME
}