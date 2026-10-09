package com.bigwintrophy.lines.domain.usecase

import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.repository.LevelRepository

class GetLevelUseCase(private val levelRepository: LevelRepository) {

    operator fun invoke(levelId: Int): LevelBoard? {
        if (levelId < 1) return null
        return levelRepository.loadBoard(levelId)
    }
}
