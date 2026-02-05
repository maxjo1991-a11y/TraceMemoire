package com.maxjth.tracememoire.ui.logic

object HomeMessages {

    fun messageForTraceCount(traceCount: Int): String {
        val n = traceCount.coerceAtLeast(0)

        return when {
            n == 0 ->
                "Pose ta première trace. Rien d’autre."

            n in 1..6 ->
                "Reste simple : un pourcentage, quelques repères."

            n in 7..13 ->
                "Une semaine : le rythme est lancé."

            n in 14..29 ->
                "Deux semaines : tu vois déjà la cadence."

            n in 30..59 ->
                "Un mois : la trace devient naturelle."

            n in 60..89 ->
                "Deux mois : c’est stable, sans effort."

            n in 90..179 ->
                "Trois mois : tu tiens un fil."

            n in 180..364 ->
                "Une saison entière : tu peux relire."

            else ->
                "Ça continue. Calme. Régulier."
        }
    }
}