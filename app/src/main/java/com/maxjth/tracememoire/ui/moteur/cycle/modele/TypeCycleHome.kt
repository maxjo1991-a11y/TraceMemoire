// FILE: ui/moteur/cycle/modele/TypeCycle.kt
package com.maxjth.tracememoire.ui.moteur.cycle.modele

/**
 * Représente les 4 cycles principaux de la journée.
 * C’est l’unique source de vérité pour les cycles.
 *
 * IMPORTANT :
 * - Ne jamais utiliser "MATIN", "JOUR", etc. en String ailleurs.
 * - Toujours passer par TypeCycle.
 */
enum class TypeCycleHome {

    NUIT,
    MATIN,
    JOUR,
    SOIR;

    companion object {

        /**
         * Conversion sécurisée depuis une String (ex: prefs legacy).
         */
        fun fromKey(key: String?): TypeCycleHome? {
            return when (key?.uppercase()) {
                "NUIT" -> NUIT
                "MATIN" -> MATIN
                "JOUR" -> JOUR
                "SOIR" -> SOIR
                else -> null
            }
        }
    }

    /**
     * Clé standardisée pour stockage (SharedPrefs, maps, etc.)
     */
    fun storageKey(): String = name

    /**
     * Label français propre pour affichage UI.
     */
    fun labelFr(): String {
        return when (this) {
            NUIT -> "Nuit"
            MATIN -> "Matin"
            JOUR -> "Jour"
            SOIR -> "Soir"
        }
    }
}