package com.bigwintrophy.lines.presentation.game

import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.RoundResult

sealed class GameUiState {

    object Loading : GameUiState()

    data class Playing(
        val board: LevelBoard,
        val moves: Int,
        val connected: Int,
        val total: Int,
        val hintsLeft: Int,
        val statusRes: Int,
        val boardToken: Int
    ) : GameUiState()

    data class Finished(val result: RoundResult) : GameUiState()

    object Error : GameUiState()
}
