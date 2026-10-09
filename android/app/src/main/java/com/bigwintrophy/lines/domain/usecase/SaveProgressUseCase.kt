package com.bigwintrophy.lines.domain.usecase

import com.bigwintrophy.lines.domain.model.RoundResult
import com.bigwintrophy.lines.domain.repository.ProgressRepository

class SaveProgressUseCase(private val progressRepository: ProgressRepository) {

    operator fun invoke(result: RoundResult) {
        progressRepository.recordRound(result)
    }
}
