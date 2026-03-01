package com.maxjth.tracememoire.ui.structure

/**
 * Événement unitaire de l’historique.
 * Modèle neutre, stable, sans logique UI.
 */
data class TraceEvent(
    val id: String,
    val timestamp: Long,
    val dayKey: String,      // format: yyyy-MM-dd
    val hourLabel: String,   // format: HH:mm
    val type: TraceEventType,
    val value: String
)

/**
 * Types d’événements possibles dans l’historique.
 * Extensible plus tard sans casser l’existant.
 */
enum class TraceEventType {
    PERCENT_UPDATE,
    TAG_UPDATE,
    TIME_ADJUST,
    OTHER
}

