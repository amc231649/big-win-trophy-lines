package com.bigwintrophy.lines.core.ui

import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.data.sample.SampleData

object SpriteCatalog {

    fun resourceFor(key: String): Int = when (key) {
        SampleData.SPRITE_TROPHY -> R.drawable.sprite_trophy_gold
        SampleData.SPRITE_MEDAL -> R.drawable.sprite_medal_silver
        SampleData.SPRITE_STAR -> R.drawable.sprite_star_ruby
        SampleData.SPRITE_CROWN -> R.drawable.sprite_crown_royal
        SampleData.SPRITE_CUP -> R.drawable.sprite_cup_azure
        SampleData.SPRITE_RIBBON -> R.drawable.sprite_ribbon_rose
        else -> R.drawable.sprite_trophy_gold
    }
}
