package com.bigwintrophy.lines.presentation.game

import com.bigwintrophy.lines.domain.model.CellPoint
import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.TrophyPair

class BoardEngine(val board: LevelBoard) {

    private val paths = LinkedHashMap<Int, MutableList<CellPoint>>()
    private val owner = HashMap<CellPoint, Int>()

    private var active: Int = NO_PAIR
    private var undoCount: Int = 0

    var moves: Int = 0
        private set

    init {
        reset()
    }

    fun reset() {
        paths.clear()
        owner.clear()
        active = NO_PAIR
        moves = 0
        undoCount = 0
        for (pair in board.pairs) paths[pair.id] = ArrayList()
    }

    fun activePairId(): Int = active

    fun pathOf(pairId: Int): List<CellPoint> = paths[pairId] ?: emptyList()

    fun ownerOf(cell: CellPoint): Int = owner[cell] ?: NO_PAIR

    fun head(): CellPoint? = paths[active]?.lastOrNull()

    fun isComplete(pairId: Int): Boolean {
        val pair = pairById(pairId) ?: return false
        val path = paths[pairId] ?: return false
        if (path.size < 2) return false
        val first = path.first()
        val last = path.last()
        return pair.isEndpoint(first) && pair.isEndpoint(last) && first != last
    }

    fun connectedPairIds(): Set<Int> {
        val out = HashSet<Int>()
        for (pair in board.pairs) if (isComplete(pair.id)) out.add(pair.id)
        return out
    }

    fun connectedCount(): Int = connectedPairIds().size

    fun filledCells(): Int = owner.size

    fun isSolved(): Boolean =
        connectedCount() == board.pairs.size && owner.size == board.cellCount

    fun perfectFill(): Boolean = undoCount == 0

    fun tap(cell: CellPoint): Boolean {
        if (!board.contains(cell)) return false
        val endpointPair = board.pairAtEndpoint(cell)
        if (active == NO_PAIR) {
            if (endpointPair != null) return arm(endpointPair, cell)
            val holder = owner[cell]
            if (holder != null) return armAt(holder, cell)
            return armFirstIncomplete()
        }
        val currentHead = paths[active]?.lastOrNull() ?: return armFirstIncomplete()
        if (cell == currentHead) return false
        if (cell.isAdjacentTo(currentHead)) {
            if (endpointPair != null && endpointPair.id != active) return arm(endpointPair, cell)
            return extendStep(cell)
        }
        if (endpointPair != null) return arm(endpointPair, cell)
        return extendAlongSolution()
    }

    fun undo(): Boolean {
        var target = active
        if (target == NO_PAIR || paths[target].isNullOrEmpty()) {
            target = longestPathPairId()
        }
        if (target == NO_PAIR) return false
        val path = paths[target] ?: return false
        if (path.isEmpty()) return false
        val last = path.removeAt(path.size - 1)
        owner.remove(last)
        active = target
        undoCount += 1
        if (moves > 0) moves -= 1
        return true
    }

    fun applyHint(pair: TrophyPair): Boolean {
        clearPath(pair.id)
        active = pair.id
        val path = paths[pair.id] ?: return false
        var added = 0
        for (cell in pair.solution) {
            if (added >= HINT_CELLS) break
            val holder = owner[cell]
            if (holder != null && holder != pair.id) truncate(holder, cell)
            path.add(cell)
            owner[cell] = pair.id
            added += 1
        }
        moves += added
        return added > 0
    }

    fun disarm() {
        active = NO_PAIR
    }

    private fun armFirstIncomplete(): Boolean {
        for (pair in board.pairs) {
            if (!isComplete(pair.id)) return arm(pair, pair.start)
        }
        return false
    }

    private fun arm(pair: TrophyPair, cell: CellPoint): Boolean {
        clearPath(pair.id)
        active = pair.id
        val path = paths[pair.id] ?: return false
        path.add(cell)
        owner[cell] = pair.id
        return true
    }

    private fun armAt(pairId: Int, cell: CellPoint): Boolean {
        val path = paths[pairId] ?: return false
        val index = path.indexOf(cell)
        if (index < 0) return false
        for (position in path.size - 1 downTo index + 1) {
            owner.remove(path[position])
            path.removeAt(position)
        }
        active = pairId
        return true
    }

    private fun extendStep(cell: CellPoint): Boolean {
        val path = paths[active] ?: return false
        val pair = pairById(active) ?: return false
        val ownIndex = path.indexOf(cell)
        if (ownIndex >= 0) {
            for (position in path.size - 1 downTo ownIndex + 1) {
                owner.remove(path[position])
                path.removeAt(position)
            }
            moves += 1
            return true
        }
        val holder = owner[cell]
        if (holder != null && holder != active) truncate(holder, cell)
        path.add(cell)
        owner[cell] = active
        moves += 1
        if (pair.isEndpoint(cell) && path.size >= 2 && pair.isEndpoint(path.first())) {
            active = NO_PAIR
        }
        return true
    }

    private fun extendAlongSolution(): Boolean {
        val pair = pairById(active) ?: return false
        val path = paths[active] ?: return false
        if (path.isEmpty()) {
            path.add(pair.solution.first())
            owner[pair.solution.first()] = pair.id
            return true
        }
        val currentHead = path.last()
        val index = pair.solution.indexOf(currentHead)
        if (index < 0) {
            clearPath(pair.id)
            active = pair.id
            val restarted = paths[pair.id] ?: return false
            val first = pair.solution.first()
            val holder = owner[first]
            if (holder != null && holder != pair.id) truncate(holder, first)
            restarted.add(first)
            owner[first] = pair.id
            moves += 1
            return true
        }
        if (index + 1 >= pair.solution.size) return false
        return extendStep(pair.solution[index + 1])
    }

    private fun longestPathPairId(): Int {
        var best = NO_PAIR
        var bestSize = 0
        for (entry in paths) {
            if (entry.value.size > bestSize) {
                bestSize = entry.value.size
                best = entry.key
            }
        }
        return best
    }

    private fun truncate(pairId: Int, cell: CellPoint) {
        val path = paths[pairId] ?: return
        val index = path.indexOf(cell)
        if (index < 0) return
        for (position in path.size - 1 downTo index) {
            owner.remove(path[position])
            path.removeAt(position)
        }
    }

    private fun clearPath(pairId: Int) {
        val path = paths[pairId] ?: return
        for (cell in path) owner.remove(cell)
        path.clear()
    }

    private fun pairById(pairId: Int): TrophyPair? = board.pairs.firstOrNull { it.id == pairId }

    companion object {
        const val NO_PAIR = -1
        const val HINT_CELLS = 3
    }
}
