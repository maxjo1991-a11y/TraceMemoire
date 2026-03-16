// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/store/TraceSaveStoreCycle.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys

/**
 * Bloc dédié aux opérations liées au cycle :
 * - accès aux maps du CycleEngine
 * - capture
 * - lock
 * - touched sliders
 */
internal object TraceSaveStoreCycle {

    // ─────────────────────────────────────────────
    // MAP ACCESS
    // ─────────────────────────────────────────────

    fun sliderMap(store: TraceSaveStore) =
        store.cycle.sliderMap

    fun noteMap(store: TraceSaveStore) =
        store.cycle.noteMap

    fun capturedMap(store: TraceSaveStore) =
        store.cycle.capturedMap

    fun createdAtMap(store: TraceSaveStore) =
        store.cycle.createdAtMap

    fun lockedMap(store: TraceSaveStore) =
        store.cycle.lockedMap

    fun touchedMap(store: TraceSaveStore) =
        store.cycle.touchedMap


    // ─────────────────────────────────────────────
    // CAPTURE
    // ─────────────────────────────────────────────

    fun markCaptured(
        store: TraceSaveStore,
        key: String
    ) {
        store.cycle.markCaptured(key)
        store.markDirty()
    }


    // ─────────────────────────────────────────────
    // LOCK
    // ─────────────────────────────────────────────

    fun lockCard(
        store: TraceSaveStore,
        key: String
    ) {
        store.cycle.lockCard(key)
        store.markDirty()
    }

    fun isLocked(
        store: TraceSaveStore,
        key: String
    ): Boolean {
        return store.cycle.isLocked(key)
    }


    // ─────────────────────────────────────────────
    // CREATED AT
    // ─────────────────────────────────────────────

    fun createdAtMillis(
        store: TraceSaveStore,
        key: String
    ): Long? {
        return store.home.createdAtMillis(key)
    }


    // ─────────────────────────────────────────────
    // PREMIUM CHECK
    // ─────────────────────────────────────────────

    private fun isPremiumSlider(key: String): Boolean {
        return key == TraceSaveKeys.SLIDER_REPOS ||
                key == TraceSaveKeys.SLIDER_ARCHI_EMO ||
                key == TraceSaveKeys.SLIDER_TYPE_JOURNEE ||
                key == TraceSaveKeys.SLIDER_MOTIFS ||
                key == TraceSaveKeys.SLIDER_ENVIRONNEMENT ||
                key == TraceSaveKeys.SLIDER_CLARTE
    }


    // ─────────────────────────────────────────────
    // TOUCH
    // ─────────────────────────────────────────────

    fun markSliderTouched(
        store: TraceSaveStore,
        key: String
    ) {
        store.cycle.markTouched(key)

        if (isPremiumSlider(key)) {
            store.home.onPremiumTouched()
        }

        store.markDirty()
    }

    fun isSliderTouched(
        store: TraceSaveStore,
        key: String
    ): Boolean {
        return store.cycle.isTouched(key)
    }
}

