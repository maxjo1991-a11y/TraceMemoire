package com.maxjth.tracememoire.ui.model

import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Trace d’état d’âme — Modèle OFFICIEL (socle)
 *
 * Règle clé :
 * - intensityPct = null  => "Non renseigné" (l’utilisateur n’a jamais touché le slider)
 * - word = null          => aucun mot choisi
 *
 * Notes :
 * - photoUri : String? (URI/chemin, selon ton implémentation)
 * - cycle : Matin / Jour / Soir / Nuit (piloté par TraceCycleClock)
 */
data class TraceAmeModel(
    val id: String = newId(),
    val date: LocalDate = LocalDate.now(),
    val cycle: TraceCycle,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val photoUri: String? = null,

    // 4 FREE + 5 PREMIUM (tu peux stocker tout, et filtrer l’affichage selon le tier)
    val entries: List<TraceAmeEntry> = emptyList()
) {

    /** Entrée d’une catégorie (slider + mot optionnel) */
    data class TraceAmeEntry(
        val category: TraceAmeCategory,
        val intensityPct: Int? = null,  // null = Non renseigné
        val word: String? = null        // 1 mot max, optionnel
    ) {
        fun isTouched(): Boolean = intensityPct != null
        fun safePct(): Int = (intensityPct ?: 0).coerceIn(0, 100)
    }

    /** Catégories OFFICIELLES (FREE + PREMIUM) */
    enum class TraceAmeCategory(val label: String, val isPremium: Boolean) {
        // ----- FREE (4) -----
        HUMEUR_GLOBALE("Humeur globale", false),
        ENERGIE_RYTHME("Énergie / rythme", false),
        CORPS_SENSATIONS("Corps / sensations", false),
        PRESENCE_ATTENTION("Présence / attention", false),

        // ----- PREMIUM (5) -----
        TYPE_DE_JOURNEE("Type de journée", true),
        MOTIFS_PSYCHIQUES("Motifs psychiques", true),
        ENVIRONNEMENT("Environnement", true),
        CLARTE_MENTALE("Clarté mentale", true),
        CHARGE_EMOTIONNELLE("Charge émotionnelle", true)
    }

    companion object {

        /** Liste “de base” FREE uniquement (pratique pour initialiser l’écran) */
        fun defaultFreeEntries(): List<TraceAmeEntry> = listOf(
            TraceAmeEntry(TraceAmeCategory.HUMEUR_GLOBALE),
            TraceAmeEntry(TraceAmeCategory.ENERGIE_RYTHME),
            TraceAmeEntry(TraceAmeCategory.CORPS_SENSATIONS),
            TraceAmeEntry(TraceAmeCategory.PRESENCE_ATTENTION)
        )

        /** Liste complète (FREE + PREMIUM) */
        fun defaultAllEntries(): List<TraceAmeEntry> = listOf(
            // FREE
            TraceAmeEntry(TraceAmeCategory.HUMEUR_GLOBALE),
            TraceAmeEntry(TraceAmeCategory.ENERGIE_RYTHME),
            TraceAmeEntry(TraceAmeCategory.CORPS_SENSATIONS),
            TraceAmeEntry(TraceAmeCategory.PRESENCE_ATTENTION),

            // PREMIUM
            TraceAmeEntry(TraceAmeCategory.TYPE_DE_JOURNEE),
            TraceAmeEntry(TraceAmeCategory.MOTIFS_PSYCHIQUES),
            TraceAmeEntry(TraceAmeCategory.ENVIRONNEMENT),
            TraceAmeEntry(TraceAmeCategory.CLARTE_MENTALE),
            TraceAmeEntry(TraceAmeCategory.CHARGE_EMOTIONNELLE)
        )

        private fun newId(): String {
            // Simple, local, stable (pas besoin de lib)
            return "TA_${System.currentTimeMillis()}"
        }
    }
}