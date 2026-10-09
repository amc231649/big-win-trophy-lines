package com.bigwintrophy.lines.domain.model

data class LevelBoard(
    val id: Int,
    val title: String,
    val cols: Int,
    val rows: Int,
    val pairs: List<TrophyPair>,
    val optimalMoves: Int,
    val trophyName: String
) {

    val cellCount: Int get() = cols * rows

    fun contains(cell: CellPoint): Boolean =
        cell.col in 0 until cols && cell.row in 0 until rows

    fun pairAtEndpoint(cell: CellPoint): TrophyPair? = pairs.firstOrNull { it.isEndpoint(cell) }
}
