package com.maxjth.tracememoire.ui.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object TimeTicker {

    /**
     * Émet l’heure (format HH:mm) en continu.
     * intervalMs = 1000ms par défaut (1 sec).
     */
    fun flow(intervalMs: Long = 1000L): Flow<String> = flow {
        while (true) {
            emit(TimeFormat.nowHour())
            delay(intervalMs)
        }
    }
}