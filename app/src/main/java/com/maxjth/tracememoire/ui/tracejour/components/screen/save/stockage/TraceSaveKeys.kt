// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/stockage/TraceSaveKeys.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage

/**
 * ⚠️ VERROUILLÉ
 * - Ne renomme jamais ces keys prefs :
 *   HOME_LAST_PERCENT, HOME_DAILY_PERCENT, home_done_*, home_percent_*
 */
object TraceSaveKeys {

    // ============================================================
    // SECTION HOME (prefs sous le scope "HOME")
    // ============================================================

    const val HOME_KEY = "HOME"

    // Cycles (HomeTemporalGridRect + done flags)
    val VALID_CYCLES: Set<String> = setOf("NUIT", "MATIN", "JOUR", "SOIR")

    // Soleil (vivant)
    const val HOME_LAST_PERCENT = "home_lastPercent"

    // Flags
    const val HOME_PREMIUM_TOUCHED = "home_premium_touched"

    // ✅ Terre (figé du jour) — sert à Orion (annuel)
    const val HOME_DAILY_PERCENT = "home_daily_percent"

    // Keys dynamiques HOME
    fun homeDoneKey(cycle: String): String =
        "home_done_${cycle.trim().uppercase()}"

    fun homeCyclePercentKey(cycle: String): String =
        "home_percent_${cycle.trim().uppercase()}"


    // ============================================================
    // SECTION SLIDERS (ids "métier" — utilisés dans sliderMap)
    // ============================================================

    // Base (FREE)
    const val SLIDER_HUMEUR = "humeur"
    const val SLIDER_ENERGIE = "energie"
    const val SLIDER_CORPS = "corps"
    const val SLIDER_PRESENCE = "presence"

    // Premium
    const val SLIDER_REPOS = "repos"
    const val SLIDER_ARCHI_EMO = "archi_emo"
    const val SLIDER_TYPE_JOURNEE = "type_journee"
    const val SLIDER_MOTIFS = "motifs_psy"
    const val SLIDER_ENVIRONNEMENT = "environnement"
    const val SLIDER_CLARTE = "clarte"

    fun isPremiumSliderKey(key: String): Boolean {
        return key == SLIDER_REPOS ||
                key == SLIDER_ARCHI_EMO ||
                key == SLIDER_TYPE_JOURNEE ||
                key == SLIDER_MOTIFS ||
                key == SLIDER_ENVIRONNEMENT ||
                key == SLIDER_CLARTE
    }


    // ============================================================
    // SECTION SAVE (keys génériques par cycle pour sliders/notes/etc.)
    // Scope = cycleKey (ex: "MATIN") sauf HOME qui a son scope HOME_KEY
    // ============================================================

    // Valeurs du slider
    fun sliderKey(sliderId: String): String = "slider_$sliderId"

    // Note associée au slider
    fun noteKey(sliderId: String): String = "note_$sliderId"

    // Capture (ex: screenshot/flag interne)
    fun capturedKey(sliderId: String): String = "cap_$sliderId"

    // Timestamp de création
    fun createdAtKey(sliderId: String): String = "createdAt_$sliderId"

    // Lock par carte/slider
    fun lockedKey(sliderId: String): String = "locked_$sliderId"

    // Touched par slider
    fun touchedKey(sliderId: String): String = "touched_$sliderId"

    // État global save
    const val LAST_SAVED_EPOCH = "lastSavedEpoch"
    const val LAST_SAVED_CYCLE_KEY = "lastSavedCycleKey"
    const val IS_SAVED = "isSaved"
    const val DIRTY = "dirty"
}