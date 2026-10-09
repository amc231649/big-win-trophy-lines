package com.bigwintrophy.lines.domain.model

data class GalleryTrophy(
    val levelId: Int,
    val name: String,
    val caption: String,
    val spriteKey: String,
    val isEarned: Boolean
)
