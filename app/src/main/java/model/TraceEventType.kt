package com.maxjth.tracememoire.ui.model

/**
 * Types d’événements traçables au cours d’une journée.
 * Chaque événement est horodaté et conservé comme une trace factuelle.
 */
enum class TraceEventType {

    /** Création de la trace (point de départ de la règle 24h) */
    TRACE_CREATE,

    /** Changement du pourcentage (slider principal) */
    PERCENT_UPDATE,

    /** Texte court (ex: 0–120 caractères) */
    NOTE_UPDATE,

    /** Activation ou désactivation d’un tag */
    TAG_UPDATE,

    /** Ajustement manuel d’une heure (si tu le gardes pour l’écran 4, pas obligé en UI écran 2) */
    TIME_ADJUST
}