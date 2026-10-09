package com.bigwintrophy.lines.presentation.gameover

import androidx.lifecycle.ViewModel
import com.bigwintrophy.lines.domain.model.RoundResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel : ViewModel() {

    private val _state = MutableStateFlow<GameOverUiState?>(null)
    val state: StateFlow<GameOverUiState?> = _state.asStateFlow()

    fun bind(result: RoundResult) {
        if (_state.value != null) return
        _state.value = GameOverUiState.from(result)
    }

    fun levelId(): Int = _state.value?.result?.levelId ?: 1
}
