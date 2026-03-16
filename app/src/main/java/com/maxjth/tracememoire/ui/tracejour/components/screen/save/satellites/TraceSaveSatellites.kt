package com.maxjth.tracememoire.ui.tracejour.components.screen.save.satellites

import android.content.Context
import com.maxjth.tracememoire.ui.terre.logic.TerreHistoryStore
import java.time.LocalDate
import java.time.LocalDateTime

object TraceSaveSatellites {

    private const val SATELLITE_PREFS_NAME = "trace_memoire_satellites"

    private fun satelliteDayKey(seedBase: String): String {
        return "last_home_tick_day_$seedBase"
    }

    private fun satellitePrefs(context: Context) =
        context.applicationContext.getSharedPreferences(
            SATELLITE_PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun readLastProcessedDay(
        context: Context,
        seedBase: String
    ): String? {
        return satellitePrefs(context).getString(
            satelliteDayKey(seedBase),
            null
        )
    }

    fun writeLastProcessedDay(
        context: Context,
        seedBase: String,
        day: LocalDate
    ) {
        satellitePrefs(context)
            .edit()
            .putString(
                satelliteDayKey(seedBase),
                day.toString()
            )
            .apply()
    }

    fun resolveScoreForTerreArchive(
        officialScore: Int?,
        liveScore: Int?,
        hasCompletedCycle: Boolean
    ): Int? {
        val safeOfficial = officialScore?.coerceIn(0, 100)
        if (safeOfficial != null) return safeOfficial

        val safeLive = liveScore?.coerceIn(0, 100)

        return if (hasCompletedCycle) safeLive else null
    }

    fun archiveYesterdayIfNeeded(
        context: Context,
        seedBase: String,
        now: LocalDateTime,
        officialScore: Int?,
        liveScore: Int?,
        hasCompletedCycle: Boolean,
        onArchived: (() -> Unit)? = null
    ) {
        val today = now.toLocalDate()
        val todayIso = today.toString()

        val lastProcessedDay = readLastProcessedDay(
            context = context,
            seedBase = seedBase
        )

        println(
            "DEBUG_ARCHIVE → Début archive | now=$now | today=$todayIso | lastProcessedDay=$lastProcessedDay"
        )

        if (lastProcessedDay == null) {
            println("DEBUG_ARCHIVE → Premier lancement → on écrit today=$todayIso et on skip archive")
            writeLastProcessedDay(
                context = context,
                seedBase = seedBase,
                day = today
            )
            return
        }

        if (lastProcessedDay == todayIso) {
            println("DEBUG_ARCHIVE → Même jour ($todayIso == $lastProcessedDay) → skip archive")
            return
        }

        val yesterday = today.minusDays(1)

        val scoreToArchive = resolveScoreForTerreArchive(
            officialScore = officialScore,
            liveScore = liveScore,
            hasCompletedCycle = hasCompletedCycle
        )

        println(
            "DEBUG_ARCHIVE → Nouveau jour détecté → archive hier $yesterday | " +
                    "official=$officialScore | live=$liveScore | hasCompletedCycle=$hasCompletedCycle | scoreToArchive=$scoreToArchive"
        )

        if (scoreToArchive != null && scoreToArchive in 0..100) {
            println("DEBUG_ARCHIVE → Upsert exécuté : $yesterday = $scoreToArchive")

            TerreHistoryStore.upsertForDate(
                context = context,
                seedBase = seedBase,
                date = yesterday,
                percent = scoreToArchive
            )

            onArchived?.invoke()

            println("DEBUG_ARCHIVE → Succès : archive Terre effectuée")
        } else {
            println("DEBUG_ARCHIVE → Aucun score fiable à archiver → PAS d'upsert")
        }

        writeLastProcessedDay(
            context = context,
            seedBase = seedBase,
            day = today
        )

        println("DEBUG_ARCHIVE → Fin : lastProcessedDay mis à jour à $todayIso")
    }

    fun clearLastProcessedDay(
        context: Context,
        seedBase: String
    ) {
        satellitePrefs(context)
            .edit()
            .remove(satelliteDayKey(seedBase))
            .apply()
    }
}