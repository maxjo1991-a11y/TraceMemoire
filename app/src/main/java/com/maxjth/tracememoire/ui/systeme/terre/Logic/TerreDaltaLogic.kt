package com.maxjth.tracememoire.ui.systeme.terre.logic

import android.content.Context
import com.maxjth.tracememoire.ui.terre.logic.TerreHistoryStore

object TerreDeltaLogic {

    fun computeDeltaText(
        context: Context,
        seedBase: String
    ): String? {

        val entries =
            TerreHistoryStore.readAll(context, seedBase)
                .toList()
                .sortedBy { it.first }

        if (entries.size < 2) return null

        val last = entries[entries.lastIndex].second
        val prev = entries[entries.lastIndex - 1].second

        val delta = last - prev

        return when {
            delta > 0 -> "+$delta"
            delta < 0 -> delta.toString()
            else -> null
        }
    }
}