package com.bigwintrophy.lines.domain.model

data class CellPoint(val col: Int, val row: Int) {

    fun isAdjacentTo(other: CellPoint): Boolean {
        val dc = if (col > other.col) col - other.col else other.col - col
        val dr = if (row > other.row) row - other.row else other.row - row
        return dc + dr == 1
    }
}
