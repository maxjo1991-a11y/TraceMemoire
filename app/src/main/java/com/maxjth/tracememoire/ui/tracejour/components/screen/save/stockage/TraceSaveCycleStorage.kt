// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/stockage/TraceSaveCycleStorage.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs

/**
 * IO CYCLE (prefs) — séparé du store
 *
 * - sliders
 * - notes
 * - captured
 * - createdAt
 * - locked
 * - touched
 *
 * Persist par jour via daySeed = TraceJourPrefs.seedForToday(seedBase)
 *
 * ✅ MISE À JOUR:
 * - defaults sliders = 0
 * - ajout lecture rapide createdAt pour HOME
 */
class TraceSaveCycleStorage {

    /**
     * Lecture rapide de l'heure de création d'un cycle.
     * Utilisé par HomeScreen pour afficher:
     * "Mémoire créée • 16:32"
     */
    fun readCycleCreatedAtMillis(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ): Long? {

        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        sliderKeys.forEach { k ->

            val createdAt = TraceJourPrefs.getLong(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.createdAtKey(k),
                def = 0L
            )

            if (createdAt > 0L) {
                return createdAt
            }
        }

        return null
    }

    fun loadCycle(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>,
        sliderMap: MutableMap<String, Int>,
        noteMap: MutableMap<String, String>,
        capturedMap: MutableMap<String, Boolean>,
        createdAtMap: MutableMap<String, Long>,
        lockedMap: MutableMap<String, Boolean>,
        touchedMap: MutableMap<String, Boolean>
    ) {

        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        sliderKeys.forEach { k ->

            sliderMap[k] = TraceJourPrefs.getInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.sliderKey(k),
                def = (sliderMap[k] ?: 0)
            )

            noteMap[k] = TraceJourPrefs.getString(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.noteKey(k),
                def = (noteMap[k] ?: "")
            )

            capturedMap[k] = TraceJourPrefs.getBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.capturedKey(k),
                def = (capturedMap[k] ?: false)
            )

            createdAtMap[k] = TraceJourPrefs.getLong(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.createdAtKey(k),
                def = (createdAtMap[k] ?: 0L)
            )

            lockedMap[k] = TraceJourPrefs.getBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.lockedKey(k),
                def = (lockedMap[k] ?: false)
            )

            touchedMap[k] = TraceJourPrefs.getBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.touchedKey(k),
                def = (touchedMap[k] ?: false)
            )
        }
    }

    fun persistCycle(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderMap: Map<String, Int>,
        noteMap: Map<String, String>,
        capturedMap: Map<String, Boolean>,
        createdAtMap: Map<String, Long>,
        lockedMap: Map<String, Boolean>,
        touchedMap: Map<String, Boolean>
    ) {

        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        sliderMap.forEach { (k, v) ->
            TraceJourPrefs.putInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.sliderKey(k),
                value = v
            )
        }

        noteMap.forEach { (k, v) ->
            TraceJourPrefs.putString(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.noteKey(k),
                value = v
            )
        }

        capturedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.capturedKey(k),
                value = v
            )
        }

        createdAtMap.forEach { (k, v) ->
            TraceJourPrefs.putLong(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.createdAtKey(k),
                value = v
            )
        }

        lockedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.lockedKey(k),
                value = v
            )
        }

        touchedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(
                context = context,
                seedBase = daySeed,
                cycleKey = cycleKey,
                id = TraceSaveKeys.touchedKey(k),
                value = v
            )
        }
    }
}