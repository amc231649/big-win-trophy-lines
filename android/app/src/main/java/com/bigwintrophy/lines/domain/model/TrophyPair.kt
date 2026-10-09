package com.bigwintrophy.lines.domain.model

data class TrophyPair(
    val id: Int,
    val spriteKey: String,
    val colorIndex: Int,
    val start: CellPoint,
    val end: CellPoint,
    val solution: List<CellPoint>
) {

    fun partnerOf(cell: CellPoint): CellPoint? = when (cell) {
        start -> end
        end -> start
        else -> null
    }

    fun isEndpoint(cell: CellPoint): Boolean = cell == start || cell == end
}
