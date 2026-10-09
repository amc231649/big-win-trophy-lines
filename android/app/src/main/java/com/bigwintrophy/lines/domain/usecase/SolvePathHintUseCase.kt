package com.bigwintrophy.lines.domain.usecase

import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.TrophyPair

class SolvePathHintUseCase {

    operator fun invoke(board: LevelBoard, connectedPairIds: Set<Int>): TrophyPair? =
        board.pairs.firstOrNull { !connectedPairIds.contains(it.id) }
}
