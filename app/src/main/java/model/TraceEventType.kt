package com.maxjth.tracememoire.ui.model

/**
 * Types d’événements traçables au cours d’une journée.
 * Chaque événement est horodaté et conservé comme une trace factuelle.
 */
enum class TraceEventType {

    /** Changement du pourcentage (slider principal) */
    PERCENT_UPDATE,

    /** Activation ou désactivation d’un tag (pastille) */
    TAG_UPDATE,

    /** Ajustement manuel de l’heure associée à une trace */
    TIME_ADJUST
}