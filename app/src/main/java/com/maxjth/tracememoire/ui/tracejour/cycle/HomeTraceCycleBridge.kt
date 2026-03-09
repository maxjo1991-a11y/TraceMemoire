// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/cycle/liaison/HomeTraceCycleBridge.kt
package com.maxjth.tracememoire.ui.tracejour.cycle

import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle

object HomeTraceCycleBridge {

    fun toTraceCycle(home: TypeCycleHome): TraceCycle = when (home) {
        TypeCycleHome.NUIT -> TraceCycle.NUIT
        TypeCycleHome.MATIN -> TraceCycle.MATIN
        TypeCycleHome.JOUR -> TraceCycle.JOUR
        TypeCycleHome.SOIR -> TraceCycle.SOIR
    }

    fun toHomeCycle(trace: TraceCycle): TypeCycleHome = when (trace) {
        TraceCycle.NUIT -> TypeCycleHome.NUIT
        TraceCycle.MATIN -> TypeCycleHome.MATIN
        TraceCycle.JOUR -> TypeCycleHome.JOUR
        TraceCycle.SOIR -> TypeCycleHome.SOIR
    }
}