package com.bigwintrophy.lines.data.repository

import com.bigwintrophy.lines.data.sample.SampleData
import com.bigwintrophy.lines.domain.model.CellPoint
import com.bigwintrophy.lines.domain.model.LevelBlueprint
import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.TrophyPair
import com.bigwintrophy.lines.domain.repository.LevelRepository

class LevelRepositoryImpl : LevelRepository {

    private val blueprints: List<LevelBlueprint> = SampleData.levels

    override fun levelCount(): Int = blueprints.size

    override fun loadBoard(levelId: Int): LevelBoard? {
        val blueprint = blueprintFor(levelId) ?: return null
        val order = snakeOrder(blueprint.cols, blueprint.rows, blueprint.columnMajor)
        val lengths = splitLengths(order.size, blueprint.weights)
        val pairs = ArrayList<TrophyPair>(lengths.size)
        var cursor = 0
        for (index in lengths.indices) {
            if (cursor >= order.size) break
            var length = lengths[index]
            if (cursor + length > order.size) length = order.size - cursor
            if (length < MIN_PATH) break
            val path = ArrayList<CellPoint>(length)
            for (step in 0 until length) path.add(order[cursor + step])
            cursor += length
            pairs.add(
                TrophyPair(
                    id = index,
                    spriteKey = SampleData.spriteKeys[index % SampleData.spriteKeys.size],
                    colorIndex = index % SampleData.spriteKeys.size,
                    start = path.first(),
                    end = path.last(),
                    solution = path
                )
            )
        }
        if (pairs.isEmpty()) return null
        return LevelBoard(
            id = blueprint.id,
            title = blueprint.title,
            cols = blueprint.cols,
            rows = blueprint.rows,
            pairs = pairs,
            optimalMoves = order.size,
            trophyName = blueprint.trophyName
        )
    }

    override fun levelTitle(levelId: Int): String =
        blueprintFor(levelId)?.title ?: blueprints.first().title

    override fun levelCaption(levelId: Int): String =
        blueprintFor(levelId)?.caption ?: blueprints.first().caption

    override fun levelTrophyName(levelId: Int): String =
        blueprintFor(levelId)?.trophyName ?: blueprints.first().trophyName

    override fun levelSpriteKey(levelId: Int): String {
        val index = if (levelId > 0) levelId - 1 else 0
        return SampleData.spriteKeys[index % SampleData.spriteKeys.size]
    }

    private fun blueprintFor(levelId: Int): LevelBlueprint? =
        blueprints.firstOrNull { it.id == levelId }

    private fun snakeOrder(cols: Int, rows: Int, columnMajor: Boolean): List<CellPoint> {
        val out = ArrayList<CellPoint>(cols * rows)
        if (columnMajor) {
            for (col in 0 until cols) {
                if (col % 2 == 0) {
                    for (row in 0 until rows) out.add(CellPoint(col, row))
                } else {
                    for (row in rows - 1 downTo 0) out.add(CellPoint(col, row))
                }
            }
        } else {
            for (row in 0 until rows) {
                if (row % 2 == 0) {
                    for (col in 0 until cols) out.add(CellPoint(col, row))
                } else {
                    for (col in cols - 1 downTo 0) out.add(CellPoint(col, row))
                }
            }
        }
        return out
    }

    private fun splitLengths(total: Int, weights: List<Int>): List<Int> {
        if (weights.isEmpty()) return listOf(total)
        var parts = weights.size
        val maxParts = total / MIN_PATH
        if (parts > maxParts) parts = if (maxParts > 0) maxParts else 1
        var weightSum = 0
        for (index in 0 until parts) weightSum += if (weights[index] > 0) weights[index] else 1
        if (weightSum <= 0) weightSum = parts
        val result = IntArray(parts)
        for (index in 0 until parts) {
            val weight = if (weights[index] > 0) weights[index] else 1
            val share = total * weight / weightSum
            result[index] = if (share < MIN_PATH) MIN_PATH else share
        }
        var assigned = 0
        for (value in result) assigned += value
        var delta = total - assigned
        var guard = 0
        while (delta != 0 && guard < GUARD_LIMIT) {
            for (index in 0 until parts) {
                if (delta == 0) break
                if (delta > 0) {
                    result[index] = result[index] + 1
                    delta -= 1
                } else if (result[index] > MIN_PATH) {
                    result[index] = result[index] - 1
                    delta += 1
                }
            }
            guard += 1
        }
        return result.toList()
    }

    private companion object {
        const val MIN_PATH = 2
        const val GUARD_LIMIT = 512
    }
}
