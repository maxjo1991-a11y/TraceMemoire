package com.maxjth.tracememoire.ui.model

/**
 * Représente UN événement factuel dans la journée.
 * Chaque modification crée un nouvel événement.
 * Rien n'est écrasé.
 */
data class TraceEvent(

    /** Identifiant unique */
    val id: String = java.util.UUID.randomUUID().toString(),

    /** Timestamp exact (millis système, vérité technique) */
    val timestamp: Long = System.currentTimeMillis(),

    /** Jour de référence (ex: "2026-02-05") */
    val dayKey: String,

    /** Heure affichable (ex: "11:25") */
    val hourLabel: String,

    /** Type d’événement */
    val type: TraceEventType,

    /** Valeur associée (pourcentage, tag, etc.) */
    val value: String
)