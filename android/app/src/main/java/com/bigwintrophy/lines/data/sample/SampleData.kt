package com.bigwintrophy.lines.data.sample

import com.bigwintrophy.lines.domain.model.LevelBlueprint

object SampleData {

    const val SPRITE_TROPHY = "sprite_trophy_gold"
    const val SPRITE_MEDAL = "sprite_medal_silver"
    const val SPRITE_STAR = "sprite_star_ruby"
    const val SPRITE_CROWN = "sprite_crown_royal"
    const val SPRITE_CUP = "sprite_cup_azure"
    const val SPRITE_RIBBON = "sprite_ribbon_rose"

    val spriteKeys: List<String> = listOf(
        SPRITE_TROPHY,
        SPRITE_MEDAL,
        SPRITE_STAR,
        SPRITE_CROWN,
        SPRITE_CUP,
        SPRITE_RIBBON
    )

    val levels: List<LevelBlueprint> = listOf(
        LevelBlueprint(
            1, "OPENING GALA", 5, 5, false, listOf(6, 7, 6, 6),
            "Opening Gala Cup", "The night began with four quiet cables and a full house."
        ),
        LevelBlueprint(
            2, "RED CARPET", 5, 5, true, listOf(5, 6, 7, 7),
            "Red Carpet Medal", "Wired the whole carpet before the first camera flash."
        ),
        LevelBlueprint(
            3, "FIRST OVATION", 5, 5, false, listOf(5, 5, 5, 5, 5),
            "First Ovation Star", "Five even runs, five standing rows, not one crossing."
        ),
        LevelBlueprint(
            4, "GOLDEN PODIUM", 6, 6, true, listOf(8, 10, 9, 9),
            "Golden Podium Cup", "The podium lit up the moment the last cable landed."
        ),
        LevelBlueprint(
            5, "SILVER SALUTE", 6, 6, false, listOf(7, 8, 7, 7, 7),
            "Silver Salute Medal", "A salute of five lines, held steady to the final cell."
        ),
        LevelBlueprint(
            6, "CRYSTAL CIRCUIT", 6, 6, true, listOf(6, 6, 6, 6, 6, 6),
            "Crystal Circuit Star", "Six equal circuits, cut like facets across the case."
        ),
        LevelBlueprint(
            7, "RUBY SPOTLIGHT", 7, 7, false, listOf(12, 13, 12, 12),
            "Ruby Spotlight Crown", "The spotlight found every corner of the trophy case."
        ),
        LevelBlueprint(
            8, "AZURE OVATION", 7, 7, true, listOf(9, 10, 10, 10, 10),
            "Azure Ovation Cup", "Azure cable laid end to end while the hall was still clapping."
        ),
        LevelBlueprint(
            9, "VELVET ROPE", 7, 7, false, listOf(8, 8, 8, 8, 8, 9),
            "Velvet Rope Ribbon", "Six ropes, no tangles, and the queue never stopped moving."
        ),
        LevelBlueprint(
            10, "BRONZE ENCORE", 6, 6, true, listOf(9, 9, 9, 9),
            "Bronze Encore Medal", "An encore run that filled the case on the second try."
        ),
        LevelBlueprint(
            11, "MARQUEE LIGHTS", 6, 6, false, listOf(6, 8, 8, 7, 7),
            "Marquee Lights Star", "Every bulb on the marquee fed from a single clean run."
        ),
        LevelBlueprint(
            12, "STANDING ROOM", 7, 7, true, listOf(11, 13, 13, 12),
            "Standing Room Crown", "Not a free cell left in the house by the final bow."
        ),
        LevelBlueprint(
            13, "LAUREL PARADE", 7, 7, false, listOf(8, 9, 8, 8, 8, 8),
            "Laurel Parade Cup", "Six laurels paraded across the board without a collision."
        ),
        LevelBlueprint(
            14, "CONFETTI FALL", 6, 6, true, listOf(7, 7, 8, 7, 7),
            "Confetti Fall Ribbon", "The cables were done before the first confetti hit the floor."
        ),
        LevelBlueprint(
            15, "ENCORE STAGE", 7, 7, false, listOf(10, 10, 10, 9, 10),
            "Encore Stage Medal", "Stage rewired twice and still cleared in one sitting."
        ),
        LevelBlueprint(
            16, "PLATINUM TOAST", 7, 7, true, listOf(12, 12, 13, 12),
            "Platinum Toast Trophy", "A platinum run poured straight through the trophy case."
        ),
        LevelBlueprint(
            17, "SPOTLIGHT DUEL", 6, 6, false, listOf(6, 6, 6, 6, 6, 6),
            "Spotlight Duel Star", "Two spotlights, six cables, and nobody blinked."
        ),
        LevelBlueprint(
            18, "GALA FINALE", 7, 7, true, listOf(8, 8, 8, 8, 8, 9),
            "Gala Finale Crown", "The gala closed with every award wired to its twin."
        ),
        LevelBlueprint(
            19, "CHAMPAGNE TOWER", 7, 7, false, listOf(9, 10, 10, 10, 10),
            "Champagne Tower Cup", "Poured from the top and the tower never wobbled."
        ),
        LevelBlueprint(
            20, "MIDNIGHT TROPHY", 7, 7, true, listOf(12, 13, 12, 12),
            "Midnight Trophy", "Cleared at midnight with the house lights already down."
        ),
        LevelBlueprint(
            21, "AMBER CURTAIN", 7, 7, false, listOf(8, 8, 8, 8, 8, 9),
            "Amber Curtain Ribbon", "The curtain fell on a case filled corner to corner."
        ),
        LevelBlueprint(
            22, "EMERALD ROW", 7, 7, true, listOf(9, 10, 10, 10, 10),
            "Emerald Row Medal", "The front row stayed lit from the first cable to the last."
        ),
        LevelBlueprint(
            23, "CROWN FINALE", 7, 7, false, listOf(8, 8, 8, 8, 8, 9),
            "Crown Finale", "Six crowns connected while the orchestra held its note."
        ),
        LevelBlueprint(
            24, "HALL OF FAME", 7, 7, true, listOf(8, 8, 8, 8, 8, 9),
            "Hall Of Fame Trophy", "The last case in the hall, wired without a single undo."
        )
    )
}
