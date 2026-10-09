package com.bigwintrophy.lines.domain.model

data class LevelBlueprint(
    val id: Int,
    val title: String,
    val cols: Int,
    val rows: Int,
    val columnMajor: Boolean,
    val weights: List<Int>,
    val trophyName: String,
    val caption: String
)
