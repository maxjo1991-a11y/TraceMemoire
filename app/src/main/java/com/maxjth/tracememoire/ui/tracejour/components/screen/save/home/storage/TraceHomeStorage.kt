package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.storage

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.date.TraceHomeLogicalDate
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.official.TraceHomeOfficial
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.prefs.TraceHomePrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.state.TraceHomeState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * TraceHomeStorage
 *
 * Gère uniquement :
 * - lecture HOME depuis prefs
 * - écriture HOME dans prefs
 * - lecture de la veille logique HOME
 *
 * Aucun état Compose créé ici.
 * Aucune logique métier HOME ici.
 * Aucune logique de reset journalier ici.
 */
class TraceHomeStorage {

    companion object {
        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")
        private const val HOME_KEY = "HOME"

        // ⚠️ keys HOME (à ne pas renommer côté prefs)
        private const val KEY_PREMIUM_TOUCHED = "home_premium_touched"
        private const val KEY_TODAY_VALUE = "home_today_value"
        private const val KEY_YESTERDAY_VALUE = "home_yesterday_value"
        private const val KEY_HOME_OFFICIAL_DELTA = "HOME_OFFICIAL_DELTA"

        private fun keyDotsFree(cycle: String) = "home_dots_free_${cycle.trim().uppercase()}"
        private fun keyDone(cycle: String) = "home_done_${cycle.trim().uppercase()}"
        private fun keyRect(cycle: String) = "home_percent_${cycle.trim().uppercase()}"
        private fun keyCreatedAt(cycle: String) = "home_created_at_${cycle.trim().uppercase()}"
    }

    fun loadHomeOnly(
        context: Context,
        seedBase: String,
        state: TraceHomeState
    ) {
        val daySeed = TraceHomeLogicalDate.effectiveSeed(seedBase)

        state.lastPercent.value = TraceHomePrefs.readLastPercent(context, daySeed)

        state.premiumTouchedToday.value =
            TraceJourPrefs.getBool(context, daySeed, HOME_KEY, KEY_PREMIUM_TOUCHED, false)

        state.todayValue.value =
            TraceJourPrefs.getInt(context, daySeed, HOME_KEY, KEY_TODAY_VALUE, 0).coerceAtLeast(0)

        state.yesterdayValue.value =
            TraceJourPrefs.getInt(context, daySeed, HOME_KEY, KEY_YESTERDAY_VALUE, 0).coerceAtLeast(0)

        state.clearCycleMaps()

        VALID_CYCLES.forEach { c ->
            val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, keyDone(c), false)
            if (done) state.completedCycleMap[c] = true

            val rect = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyRect(c), -1)
            if (rect in 0..100) state.cyclePercentMap[c] = rect

            val mask = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyDotsFree(c), 0)
            if (mask in 0..15) state.cycleDotsFreeMaskMap[c] = mask

            val createdAt = TraceJourPrefs.getLong(context, daySeed, HOME_KEY, keyCreatedAt(c), -1L)
            if (createdAt > 0L) state.cycleCreatedAtMillisMap[c] = createdAt
        }

        state.yesterdayPercent.value = readYesterdayLastPercent(context, seedBase)

        if (state.yesterdayValue.value <= 0) {
            state.yesterdayValue.value = readYesterdayValue(context, seedBase)
        }

        state.officialCurrent.value = TraceHomeOfficial.readCurrent(context, daySeed)
        state.officialPrev.value = TraceHomeOfficial.readPrev(context, daySeed)

        val rawDelta = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, KEY_HOME_OFFICIAL_DELTA, 0)
        state.officialDelta.value = if (state.officialPrev.value == null) null else rawDelta
    }

    fun persistHomeOnly(
        context: Context,
        seedBase: String,
        state: TraceHomeState,
        now: LocalDateTime = LocalDateTime.now()
    ) {
        val daySeed = TraceHomeLogicalDate.effectiveSeed(seedBase, now)

        TraceHomePrefs.saveLastPercent(context, daySeed, state.lastPercent.value)

        TraceJourPrefs.putBool(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_PREMIUM_TOUCHED,
            value = state.premiumTouchedToday.value
        )

        TraceJourPrefs.putInt(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_TODAY_VALUE,
            value = state.todayValue.value.coerceAtLeast(0)
        )

        TraceJourPrefs.putInt(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_YESTERDAY_VALUE,
            value = state.yesterdayValue.value.coerceAtLeast(0)
        )

        VALID_CYCLES.forEach { c ->
            TraceJourPrefs.putBool(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyDone(c),
                value = (state.completedCycleMap[c] == true)
            )

            val rect = state.cyclePercentMap[c] ?: -1
            TraceJourPrefs.putInt(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyRect(c),
                value = rect
            )

            val mask = (state.cycleDotsFreeMaskMap[c] ?: 0).coerceIn(0, 15)
            TraceJourPrefs.putInt(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyDotsFree(c),
                value = mask
            )

            val createdAt = state.cycleCreatedAtMillisMap[c] ?: -1L
            TraceJourPrefs.putLong(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyCreatedAt(c),
                value = createdAt
            )
        }
    }

    fun readYesterdayLastPercent(
        context: Context,
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int? {
        val yesterday = TraceHomeLogicalDate.logicalYesterday(zoneId)
        val ySeed = TraceJourPrefs.seedForDate(seedBase, yesterday)
        return TraceHomePrefs.readLastPercent(context, ySeed)
    }

    fun readYesterdayValue(
        context: Context,
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int {
        val yesterday = TraceHomeLogicalDate.logicalYesterday(zoneId)
        val ySeed = TraceJourPrefs.seedForDate(seedBase, yesterday)

        return TraceJourPrefs.getInt(
            context,
            ySeed,
            HOME_KEY,
            KEY_TODAY_VALUE,
            0
        ).coerceAtLeast(0)
    }
}