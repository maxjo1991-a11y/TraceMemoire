// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/store/TraceSaveStoreAttach.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context

/**
 * Bloc dédié aux opérations d’attachement et à l’état de sauvegarde
 * du TraceSaveStore. Ce fichier permet d’alléger TraceSaveStore.kt
 * sans modifier la logique centrale.
 */
internal object TraceSaveStoreAttach {

    fun attach(
        store: TraceSaveStore,
        context: Context,
        seedBase: String
    ) {
        store.attachedContext = context.applicationContext
        store.attachedSeedBase = seedBase

        store.homeIO.attach(context, seedBase)
        store.valeur.attach(context, seedBase)
    }

    fun setCanSave(
        store: TraceSaveStore,
        enabled: Boolean
    ) {
        store.canSaveEnabled.value = enabled
    }

    fun markDirty(
        store: TraceSaveStore
    ) {
        store.state.value = store.state.value.markDirty()
    }

    fun setSaving(
        store: TraceSaveStore
    ) {
        store.state.value = store.state.value.beginSaving()
    }
}

