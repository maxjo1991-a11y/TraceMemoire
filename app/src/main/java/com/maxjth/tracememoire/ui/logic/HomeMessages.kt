package com.maxjth.tracememoire.ui.logic

object HomeMessages {

    fun messageForTraceCount(traceCount: Int): String {
        val n = traceCount.coerceAtLeast(0)

        return when {
            n == 0 ->
                "Tout commence ici."

            n in 1..6 ->
                "Quelques repères sont posés."

            n in 7..13 ->
                "Une première continuité apparaît."

            n in 14..29 ->
                "Les journées commencent à se répondre."

            n in 30..59 ->
                "Un rythme discret s’installe."

            n in 60..89 ->
                "La trace devient familière."

            n in 90..179 ->
                "Une présence régulière se dessine."

            n in 180..364 ->
                "Ce chemin t’appartient maintenant."

            else ->
                "Tu n’as rien à prouver. La trace est là."
        }
    }
}