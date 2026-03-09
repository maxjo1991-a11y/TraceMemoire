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
     * Ajoute / remplace la valeur d'hier.
     * Très utile pour transférer le score final du Soleil vers la Terre à minuit.
     */
    fun upsertYesterday(
        context: Context,
        seedBase: String,
        percent: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        upsertForDate(context, seedBase, yesterday, percent)
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

                val d = runCatching { LocalDate.parse(parts[0].trim()) }.getOrNull()
                    ?: return@forEach

                val v = parts[1].trim().toIntOrNull()?.coerceIn(0, 100)
                    ?: return@forEach

                out[d] = v
            }

        return out
    }

    /**
     * Retourne la valeur enregistrée pour une date précise.
     */
    fun readForDate(
        context: Context,
        seedBase: String,
        date: LocalDate
    ): Int? {
        return readAll(context, seedBase)[date]
    }

    /**
     * Retourne la valeur d'hier si elle existe.
     */
    fun readYesterday(
        context: Context,
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int? {
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        return readForDate(context, seedBase, yesterday)
    }

    /**
     * Retourne la dernière date enregistrée + sa valeur.
     * Très utile pour afficher la dernière mémoire stable de la Terre.
     *
     * Attention :
     * - cette fonction lit la date la plus récente, y compris aujourd'hui si elle existe.
     * - pour la Terre figée sur "dernier jour fermé", préfère readLatestClosedEntry().
     */
    fun readLatestEntry(
        context: Context,
        seedBase: String
    ): Pair<LocalDate, Int>? {
        val all = readAll(context, seedBase)
        if (all.isEmpty()) return null

        val latestDate = all.keys.maxOrNull() ?: return null
        val latestValue = all[latestDate] ?: return null

        return latestDate to latestValue
    }

    /**
     * Retourne juste la dernière valeur enregistrée.
     *
     * Attention :
     * - cette fonction lit la date la plus récente, y compris aujourd'hui si elle existe.
     * - pour la Terre figée sur "dernier jour fermé", préfère readLatestClosedPercent().
     */
    fun readLatestPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return readLatestEntry(context, seedBase)?.second
    }

    /**
     * Retourne la dernière entrée "fermée", donc strictement avant aujourd'hui.
     *
     * C'est la bonne lecture pour la TERRE :
     * - la Terre ne doit pas bouger pendant la journée
     * - elle doit refléter le dernier jour terminé
     */
    fun readLatestClosedEntry(
        context: Context,
        seedBase: String,
        today: LocalDate = LocalDate.now()
    ): Pair<LocalDate, Int>? {
        val closed = readAll(context, seedBase)
            .filterKeys { it.isBefore(today) }

        if (closed.isEmpty()) return null

        val latestDate = closed.keys.maxOrNull() ?: return null
        val latestValue = closed[latestDate] ?: return null

        return latestDate to latestValue
    }

    /**
     * Retourne juste la dernière valeur "fermée", donc strictement avant aujourd'hui.
     *
     * C'est cette fonction qu'il faut utiliser pour afficher le chiffre principal de la Terre.
     */
    fun readLatestClosedPercent(
        context: Context,
        seedBase: String,
        today: LocalDate = LocalDate.now()
    ): Int? {
        return readLatestClosedEntry(context, seedBase, today)?.second
    }

    /**
     * Retourne l'entrée fermée précédente (avant la dernière entrée fermée).
     *
     * Exemple :
     * - avant-hier = 62
     * - hier = 59
     *
     * readLatestClosedEntry()   -> 2026-03-05 to 59
     * readPreviousClosedEntry() -> 2026-03-04 to 62
     */
    fun readPreviousClosedEntry(
        context: Context,
        seedBase: String,
        today: LocalDate = LocalDate.now()
    ): Pair<LocalDate, Int>? {
        val sortedClosed = readAll(context, seedBase)
            .filterKeys { it.isBefore(today) }
            .toSortedMap()

        if (sortedClosed.size < 2) return null

        val entries = sortedClosed.entries.toList()
        val previous = entries[entries.lastIndex - 1]

        return previous.key to previous.value
    }

    /**
     * Retourne juste la valeur fermée précédente, si elle existe.
     */
    fun readPreviousClosedPercent(
        context: Context,
        seedBase: String,
        today: LocalDate = LocalDate.now()
    ): Int? {
        return readPreviousClosedEntry(context, seedBase, today)?.second
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