package com.maxjth.tracememoire.archiver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay

/**
 * HomeAmbientPhrasesRect
 * - 72 phrases "mystérieuses / philosophiques" (calmes)
 * - Rotation automatique (par défaut: 20s)
 * - AUCUNE analyse, AUCUN jugement : juste une présence.
 */
object HomeAmbientPhrasesRect {

    // ✅ 72 phrases (base v1)
    val PHRASES: List<String> = listOf(
        "Le temps ne répète rien. Il reformule.",
        "Certaines traces ne disparaissent jamais vraiment.",
        "Une stabilité peut contenir mille variations.",
        "Rien ne semble changer. Tout évolue pourtant.",
        "Les jours se suivent sans jamais se reproduire.",
        "Une continuité discrète traverse ce moment.",
        "Ce qui revient n’est jamais identique.",
        "L’équilibre possède ses propres mouvements.",
        "Des constantes invisibles accompagnent la journée.",
        "Chaque trace modifie subtilement l’ensemble.",
        "Le présent dialogue toujours avec autre chose.",
        "Certaines dynamiques ne demandent aucune explication.",
        "L’immobilité peut masquer un mouvement réel.",
        "Les variations les plus calmes sont souvent les plus stables.",
        "Rien d’exceptionnel. Rien d’insignifiant.",
        "Une cohérence silencieuse persiste.",
        "Ce jour ne s’isole pas totalement des autres.",
        "Les traces récentes laissent une géométrie subtile.",
        "L’équilibre du moment n’est jamais fixe.",
        "Une journée peut être stable sans être immobile.",

        "Ce que tu vis s’inscrit, même sans bruit.",
        "Parfois, la clarté vient sans raison.",
        "Le temps conserve plus qu’il ne montre.",
        "Une trace suffit pour changer la lecture.",
        "La répétition n’existe pas. Seulement des échos.",
        "Ce qui est simple peut être profond.",
        "Une nuance peut valoir une journée entière.",
        "Ce moment contient déjà sa suite.",
        "Le calme n’est pas vide : il est structuré.",
        "Tout semble pareil… jusqu’au détail.",
        "Une journée stable peut cacher une bascule.",
        "Le rythme du soir n’est pas celui du matin.",
        "Ce que tu notes devient une forme.",
        "Certaines journées parlent en silence.",
        "Les chiffres ne jugent pas. Ils décrivent.",
        "Le temps rassemble ce que l’esprit disperse.",
        "La mémoire n’explique rien : elle montre.",
        "Un motif revient, mais change de visage.",
        "Ce jour s’ajoute au reste, sans demander la permission.",
        "Une continuité suffit pour créer du sens.",

        "Le présent laisse des angles invisibles.",
        "Ce qui paraît neutre peut être précieux.",
        "Les jours calmes écrivent une vraie histoire.",
        "Tu n’es pas en retard. Tu traces.",
        "La stabilité n’est pas une absence : c’est une tenue.",
        "Une trace peut être légère et décisive.",
        "Tout ne se sent pas. Tout se marque.",
        "Le temps ne fait pas de bruit : il empile.",
        "Il y a des jours qui ne crient pas, mais restent.",
        "La constance a sa propre élégance.",
        "Certaines preuves sont minuscules.",
        "Ce moment ressemble à un autre… sans être le même.",
        "Le réel change à petites doses.",
        "Un jour peut être sobre et très dense.",
        "Le silence contient parfois une direction.",
        "Le temps écrit même quand tu doutes.",
        "Une trace récente peut éclairer une ancienne.",
        "Rien ne force le sens. Il apparaît.",
        "Ce que tu gardes devient lisible plus tard.",
        "Le temps ne presse pas. Il poursuit.",

        "La répétition est une illusion utile.",
        "Il existe des équilibres qui respirent.",
        "Ce jour a sa propre température.",
        "Une stabilité peut être une transition lente.",
        "Ce qui est stable peut être vivant.",
        "Un détail suffit pour différencier deux journées.",
        "Le temps n’oublie pas : il classe.",
        "Les traces forment un langage discret.",
        "Ce que tu notes aujourd’hui éclaire demain.",
        "Rien n’est figé. Même l’habitude bouge.",
        "Parfois, la constance est une victoire muette.",
        "Une journée tranquille peut être une fondation.",
        "Le temps ne te suit pas : il t’archive.",
        "Le calme est une forme de précision.",
        "Tout s’additionne, même le presque-rien.",
        "Ce jour s’inscrit sans se justifier.",
        "Certaines journées gardent une ligne nette.",
        "Un rythme stable peut cacher une montée lente.",
        "La mémoire commence par une phrase.",
        "Ce que tu vis aujourd’hui reviendra… autrement.",
        "La continuité est une force invisible.",
        "Le temps ne finit pas. Il accumule."
    )

    /**
     * Renvoie une phrase qui change automatiquement.
     *
     * @param intervalMs   vitesse (ex: 12_000L = 12s). Pour test: 2_000L.
     * @param shuffleMode  true = ordre “mystérieux” (saute dans la liste)
     */
    @Composable
    fun rememberAmbientPhrase(
        intervalMs: Long = 20_000L,   // ✅ par défaut plus vivant que 60s
        shuffleMode: Boolean = false
    ): State<String> {

        val phrases = rememberUpdatedState(PHRASES)

        // index persistant (survie aux recompositions)
        val index = rememberSaveable { mutableIntStateOf(0) }

        // ✅ on init avec la liste la plus récente (phrases.value), pas une vieille copie
        val text = remember { mutableStateOf("") }
        LaunchedEffect(phrases.value) {
            if (text.value.isBlank() && phrases.value.isNotEmpty()) {
                val safe = index.intValue.coerceIn(0, phrases.value.lastIndex)
                text.value = phrases.value[safe]
            }
        }

        LaunchedEffect(intervalMs, shuffleMode, phrases.value) {
            if (phrases.value.isEmpty()) return@LaunchedEffect

            while (true) {
                delay(intervalMs)

                val next = if (shuffleMode) {
                    // ✅ pseudo-shuffle stable: saute de +7 (ou +11 si trop petit)
                    val jump = if (phrases.value.size >= 12) 7 else 3
                    (index.intValue + jump) % phrases.value.size
                } else {
                    (index.intValue + 1) % phrases.value.size
                }

                index.intValue = next
                text.value = phrases.value[next]
            }
        }

        return text
    }

    /**
     * Wrapper: renvoie directement une String (pratique pour ton rectangle).
     */
    @Composable
    fun rememberAmbientPhraseRect(
        periodMs: Long = 20_000L,      // ✅ plus rapide
        shuffleMode: Boolean = false
    ): String {
        return rememberAmbientPhrase(
            intervalMs = periodMs,
            shuffleMode = shuffleMode
        ).value
    }
}