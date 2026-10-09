package com.bigwintrophy.lines.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.config.GameConfig
import com.bigwintrophy.lines.domain.model.LevelBoard
import com.bigwintrophy.lines.domain.model.RoundOutcome
import com.bigwintrophy.lines.domain.model.TrophyPair
import com.bigwintrophy.lines.domain.usecase.GetLevelUseCase
import com.bigwintrophy.lines.domain.usecase.SaveProgressUseCase
import com.bigwintrophy.lines.domain.usecase.ScoreRoundUseCase
import com.bigwintrophy.lines.domain.usecase.SolvePathHintUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val getLevel: GetLevelUseCase,
    private val scoreRound: ScoreRoundUseCase,
    private val saveProgress: SaveProgressUseCase,
    private val solveHint: SolvePathHintUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var board: LevelBoard? = null
    private var hintsLeft = GameConfig.HINTS_PER_LEVEL
    private var moves = 0
    private var connected = 0
    private var perfectFill = true
    private var statusRes = R.string.game_hint_idle
    private var boardToken = 0
    private var finishing = false

    private var boardStartedAt = 0L
    private var lastInteractionAt = 0L
    private var backstopJob: Job? = null
    private var finishJob: Job? = null

    fun start(levelId: Int) {
        if (board != null) return
        val loaded = getLevel(levelId)
        if (loaded == null) {
            _state.value = GameUiState.Error
            return
        }
        board = loaded
        resetCounters()
        boardToken += 1
        publishPlaying()
        armBackstop()
    }

    fun retry(levelId: Int) {
        board = null
        finishing = false
        start(levelId)
    }

    fun resetRound() {
        if (board == null) return
        resetCounters()
        boardToken += 1
        publishPlaying()
        armBackstop()
    }

    fun onBoardChanged(nextMoves: Int, nextConnected: Int, undoFree: Boolean) {
        if (finishing) return
        moves = nextMoves
        connected = nextConnected
        perfectFill = undoFree
        lastInteractionAt = SystemClock.elapsedRealtime()
        statusRes = when {
            nextConnected > 0 && nextConnected == (board?.pairs?.size ?: 0) ->
                R.string.game_hint_closed

            nextMoves > 0 -> R.string.game_hint_drawing
            else -> R.string.game_hint_idle
        }
        publishPlaying()
    }

    fun requestHint(connectedIds: Set<Int>): TrophyPair? {
        if (hintsLeft <= 0) return null
        val current = board ?: return null
        val pair = solveHint(current, connectedIds) ?: return null
        hintsLeft -= 1
        perfectFill = false
        lastInteractionAt = SystemClock.elapsedRealtime()
        publishPlaying()
        return pair
    }

    fun hintsRemaining(): Int = hintsLeft

    fun onSolved() {
        finishRound(RoundOutcome.VICTORY, GameConfig.VICTORY_DELAY_MS)
    }

    fun currentBoard(): LevelBoard? = board

    private fun resetCounters() {
        hintsLeft = GameConfig.HINTS_PER_LEVEL
        moves = 0
        connected = 0
        perfectFill = true
        finishing = false
        statusRes = R.string.game_hint_idle
    }

    private fun publishPlaying() {
        val current = board ?: return
        if (finishing) return
        _state.value = GameUiState.Playing(
            board = current,
            moves = moves,
            connected = connected,
            total = current.pairs.size,
            hintsLeft = hintsLeft,
            statusRes = statusRes,
            boardToken = boardToken
        )
    }

    private fun armBackstop() {
        backstopJob?.cancel()
        val now = SystemClock.elapsedRealtime()
        boardStartedAt = now
        lastInteractionAt = now
        backstopJob = viewModelScope.launch {
            while (true) {
                delay(GameConfig.BACKSTOP_TICK_MS)
                if (finishing) return@launch
                val stamp = SystemClock.elapsedRealtime()
                val sinceStart = stamp - boardStartedAt
                val sinceInteraction = stamp - lastInteractionAt
                val capped = sinceStart >= GameConfig.BACKSTOP_MAX_MS
                val idled = sinceStart >= GameConfig.BACKSTOP_FLOOR_MS &&
                    sinceInteraction >= GameConfig.BACKSTOP_IDLE_MS
                if (capped || idled) {
                    finishRound(RoundOutcome.TIME_CALLED, GameConfig.TIME_CALLED_DELAY_MS)
                    return@launch
                }
            }
        }
    }

    private fun finishRound(outcome: RoundOutcome, holdMillis: Long) {
        if (finishing) return
        val current = board ?: return
        finishing = true
        backstopJob?.cancel()
        backstopJob = null
        finishJob?.cancel()
        finishJob = viewModelScope.launch {
            val result = scoreRound(current, outcome, connected, moves, perfectFill)
            saveProgress(result)
            delay(holdMillis)
            _state.value = GameUiState.Finished(result)
        }
    }

    override fun onCleared() {
        backstopJob?.cancel()
        backstopJob = null
        finishJob?.cancel()
        finishJob = null
        super.onCleared()
    }
}
