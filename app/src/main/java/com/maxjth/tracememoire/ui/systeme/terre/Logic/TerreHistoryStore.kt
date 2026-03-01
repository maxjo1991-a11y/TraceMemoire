// FILE: app/src/main/java/com/maxjth/tracememoire/ui/terre/logic/TerreHistoryStore.kt
package com.maxjth.tracememoire.ui.terre.logic

import android.content.Context
import java.time.LocalDate
import java.time.ZoneId

/**
 * TerreHistoryStore
 *
 * But (Terre) :
 * - Conserver une "histoire" des % journaliers (1 valeur par date).
 * - Calculer un % annuel (moyenne simple des jours enregistrés pour l'année).
 *
 * Notes :
 * - Stockage simple et robuste dans SharedPreferences.
 * - Format interne : "YYYY-MM-DD=57|YYYY-MM-DD=62|..."
 * - Une date = une valeur (si on réécrit la même date, ça remplace).
 */
object TerreHistoryStore {

    private const val PREFS_NAME = "trace_memoire_terre_history"

    private fun keyForSeed(seedBase: String) = "history_$seedBase"

    // Sécurité : on garde large (plusieurs années possibles)
    private const val MAX_ENTRIES = 5000

    /**
     * Ajoute / remplace la valeur du jour (LocalDate.now()).
     */
    fun upsertToday(
        context: Context,
        seedBase: String,
        percent: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val today = LocalDate.now(zoneId)
        upsertForDate(context, seedBase, today, percent)
    }

    /**
     * Ajoute / remplace une valeur pour une date précise.
     */
    fun upsertForDate(
        context: Context,
        seedBase: String,
        date: LocalDate,
        percent: Int
    ) {
        val p = percent.coerceIn(0, 100)

        val map = readAll(context, seedBase).toMutableMap()
        map[date] = p

        // Prune si trop gros (on enlève les plus anciennes dates)
        if (map.size > MAX_ENTRIES) {
            val sorted = map.toSortedMap()
            val overflow = sorted.size - MAX_ENTRIES
            var removed = 0
            val it = sorted.entries.iterator()
            while (it.hasNext() && removed < overflow) {
                it.next()
                it.remove()
                removed++
            }
            writeAll(context, seedBase, sorted)
            return
        }

        writeAll(context, seedBase, map)
    }

    /**
     * Lecture complète (toutes dates enregistrées).
     */
    fun readAll(
        context: Context,
        seedBase: String
    ): Map<LocalDate, Int> {
        val raw = prefs(context).getString(keyForSeed(seedBase), "") ?: ""
        if (raw.isBlank()) return emptyMap()

        val out = linkedMapOf<LocalDate, Int>()
        raw.split("|")
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { token ->
                val parts = token.split("=")
                if (parts.size != 2) return@forEach

                val d = runCatching { LocalDate.parse(parts[0].trim()) }.getOrNull() ?: return@forEach
                val v = parts[1].trim().toIntOrNull()?.coerceIn(0, 100) ?: return@forEach
                out[d] = v
            }

        return out
    }

    /**
     * Lecture filtrée pour une année.
     */
    fun readYear(
        context: Context,
        seedBase: String,
        year: Int
    ): Map<LocalDate, Int> {
        return readAll(context, seedBase).filterKeys { it.year == year }
    }

    /**
     * % annuel = moyenne simple des jours enregistrés dans l'année.
     * Retourne null si aucune donnée pour l'année.
     */
    fun computeYearlyPercent(
        context: Context,
        seedBase: String,
        year: Int
    ): Int? {
        val values = readYear(context, seedBase, year).values
        if (values.isEmpty()) return null
        val avg = values.average()
        return avg.toInt().coerceIn(0, 100)
    }

    /**
     * % global = moyenne simple de toutes les dates enregistrées.
     */
    fun computeGlobalPercent(
        context: Context,
        seedBase: String
    ): Int? {
        val values = readAll(context, seedBase).values
        if (values.isEmpty()) return null
        val avg = values.average()
        return avg.toInt().coerceIn(0, 100)
    }

    /**
     * Supprime une date (si tu veux corriger une journée).
     */
    fun removeDate(
        context: Context,
        seedBase: String,
        date: LocalDate
    ) {
        val map = readAll(context, seedBase).toMutableMap()
        if (map.remove(date) != null) {
            writeAll(context, seedBase, map)
        }
    }

    /**
     * Reset complet (debug / dev).
     */
    fun clearAll(context: Context, seedBase: String) {
        prefs(context).edit().remove(keyForSeed(seedBase)).apply()
    }

    // ─────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────
    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun writeAll(
        context: Context,
        seedBase: String,
        map: Map<LocalDate, Int>
    ) {
        // On écrit trié pour stabilité (debug + déterminisme)
        val sorted = map.toSortedMap()

        val raw = buildString {
            sorted.entries.forEachIndexed { idx, e ->
                if (idx > 0) append("|")
                append(e.key.toString())
                append("=")
                append(e.value.coerceIn(0, 100))
            }
        }

        prefs(context).edit().putString(keyForSeed(seedBase), raw).apply()
    }
}