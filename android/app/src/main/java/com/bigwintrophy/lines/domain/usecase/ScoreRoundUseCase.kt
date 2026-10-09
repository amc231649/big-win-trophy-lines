package com.bigwintrophy.lines.domain.usecase

import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.RoundOutcome
import com.bigwintrophy.lines.domain.model.RoundResult

class ScoreRoundUseCase {

    operator fun invoke(
        board: LevelBoard,
        outcome: RoundOutcome,
        connectedPairs: Int,
        moves: Int,
        perfectFill: Boolean
    ): RoundResult {
        val overMoves = moves - board.optimalMoves
        val penalty = if (overMoves > 0) overMoves * PENALTY_PER_MOVE else 0
        val bonus = if (perfectFill) PERFECT_BONUS else 0
        val raw = BASE_POINTS + connectedPairs * POINTS_PER_PAIR - penalty + bonus
        val points = if (raw < FLOOR_POINTS) FLOOR_POINTS else raw
        return RoundResult(
            levelId = board.id,
            levelTitle = board.title,
            trophyName = board.trophyName,
            outcome = outcome,
            connectedPairs = connectedPairs,
            totalPairs = board.pairs.size,
            moves = moves,
            points = points,
            perfectFill = perfectFill
        )
    }

    private companion object {
        const val BASE_POINTS = 120
        const val POINTS_PER_PAIR = 20
        const val PENALTY_PER_MOVE = 2
        const val PERFECT_BONUS = 40
        const val FLOOR_POINTS = 40
    }
}
