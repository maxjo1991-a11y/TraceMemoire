package com.maxjth.tracememoire.ui.logic

object HomeMessages {

    fun messageForTraceCount(traceCount: Int): String {
        val n = traceCount.coerceAtLeast(0)

        return when {
            n == 0 -> "Quelque chose est en train de commencer"

            n in 1..6 ->
                "Chaque trace ajoute une couche de mémoire"

            n in 7..13 ->
                "Une première semaine est inscrite"

            n in 14..29 ->
                "Deux semaines. La mémoire prend forme"

            n in 30..59 ->
                "Un mois. La trace devient rythme"

            n in 60..89 ->
                "Deux mois. La mémoire s'organise"

            n in 90..179 ->
                "Trois mois. Une continuité apparaît"

            else ->
                "La mémoire grandit. Sans pression"
        }
    }
}