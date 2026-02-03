package com.maxjth.tracememoire.ui.tags

/* ------------------------------
TIERS
-------------------------------- */

enum class Tier {
    FREE,
    PREMIUM,
    PREMIUM_PLUS
}

/* ------------------------------
CATÉGORIE DE TAGS
-------------------------------- */

data class TagCategory(
    val id: String,
    val title: String,
    val foundation: String,
    val tags: List<String>,
    val tier: Tier
)

/* ------------------------------
RÈGLE D’ACCÈS PAR TIER
-------------------------------- */

fun tierAllowed(
    tier: Tier,
    hasPremium: Boolean,
    hasPremiumPlus: Boolean
): Boolean {
    return when (tier) {
        Tier.FREE -> true
        Tier.PREMIUM -> hasPremium || hasPremiumPlus
        Tier.PREMIUM_PLUS -> hasPremiumPlus
    }
}

/* ------------------------------
GROUPES OFFICIELS — ÉCRAN 2
-------------------------------- */

val TAG_GROUPS_OFFICIAL = listOf(

    TagCategory(
        id = "humeur",
        title = "Humeur globale",
        foundation = "Quelle est la tonalité dominante de la journée ?",
        tier = Tier.FREE,
        tags = listOf(
            "calme",
            "tendu",
            "stable",
            "agité",
            "lourd",
            "léger",
            "équilibré",
            "joyeux",
            "sombre"
        )
    ),

    TagCategory(
        id = "energie",
        title = "Énergie / Rythme",
        foundation = "Comment l’énergie circulait aujourd’hui ?",
        tier = Tier.FREE,
        tags = listOf(
            "fluide",
            "lent",
            "rapide",
            "haché",
            "épuisé",
            "constant"
        )
    ),

    TagCategory(
        id = "corps",
        title = "Corps / Sensations",
        foundation = "Quelles sensations corporelles dominaient ?",
        tier = Tier.FREE,
        tags = listOf(
            "détendu",
            "tendu",
            "lourd",
            "léger",
            "ancré",
            "agité"
        )
    ),

    TagCategory(
        id = "presence",
        title = "Présence / Attention",
        foundation = "Où se situait l’attention la plupart du temps ?",
        tier = Tier.FREE,
        tags = listOf(
            "présent",
            "distrait",
            "concentré",
            "dispersé",
            "attentif",
            "absent",
            "ancré",
            "flottant"
        )
    ),

    TagCategory(
        id = "journee",
        title = "Type de journée",
        foundation = "Comment qualifier globalement cette journée ?",
        tier = Tier.FREE,
        tags = listOf(
            "productive",
            "chargée",
            "calme",
            "chaotique",
            "vide",
            "équilibrée"
        )
    ),

    // ---------------- PREMIUM ----------------

    TagCategory(
        id = "motifs",
        title = "Motifs / Cycles",
        foundation = "Des schémas récurrents étaient-ils présents ?",
        tier = Tier.PREMIUM,
        tags = listOf(
            "répétition",
            "rupture",
            "montée",
            "descente",
            "stagnation"
        )
    ),

    TagCategory(
        id = "environnement",
        title = "Environnement",
        foundation = "Quel impact avait l’environnement ?",
        tier = Tier.PREMIUM,
        tags = listOf(
            "bruyant",
            "calme",
            "stimulant",
            "oppressant",
            "neutre"
        )
    ),

    TagCategory(
        id = "clarte",
        title = "Clarté mentale",
        foundation = "Le mental était-il clair ou embrouillé ?",
        tier = Tier.PREMIUM,
        tags = listOf(
            "clair",
            "confus",
            "lucide",
            "surchargé"
        )
    ),

    TagCategory(
        id = "charge",
        title = "Charge émotionnelle",
        foundation = "Quelle intensité émotionnelle était présente ?",
        tier = Tier.PREMIUM,
        tags = listOf(
            "faible",
            "modérée",
            "forte",
            "envahissante"
        )
    )
)