package com.bigwintrophy.lines.presentation.gameover

import com.bigwintrophy.lines.domain.model.RoundResult

data class GameOverUiState(
    val result: RoundResult,
    val showBurst: Boolean
) {

    companion object {
        fun from(result: RoundResult): GameOverUiState =
            GameOverUiState(result = result, showBurst = result.isVictory)
    }
}
