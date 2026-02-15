package com.maxjth.tracememoire.ui.tracejour.components.screen.save

/**
 * Statut simple et stable du bouton Enregistrer
 */
enum class TraceSaveStatus {
    IDLE,      // rien de spécial
    SAVING,    // animation / verrouillage bouton
    SAVED,     // sauvegarde confirmée
    ERROR      // échec optionnel
}