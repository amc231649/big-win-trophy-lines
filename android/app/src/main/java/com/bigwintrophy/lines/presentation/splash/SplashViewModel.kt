package com.bigwintrophy.lines.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigwintrophy.lines.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private val _percent = MutableStateFlow(0)
    val percent: StateFlow<Int> = _percent.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            var elapsed = 0L
            while (elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(TICK_MS)
                elapsed += TICK_MS
                if (elapsed > GameConfig.LOADER_DURATION_MS) {
                    elapsed = GameConfig.LOADER_DURATION_MS
                }
                _elapsedMillis.value = elapsed
                _percent.value = computePercent(elapsed)
            }
            _ready.value = true
        }
    }

    private fun computePercent(elapsed: Long): Int {
        if (GameConfig.LOADER_DURATION_MS <= 0L) return FULL_PERCENT
        val scaled = elapsed * FULL_PERCENT / GameConfig.LOADER_DURATION_MS
        val value = scaled.toInt()
        if (value < 0) return 0
        if (value > FULL_PERCENT) return FULL_PERCENT
        return value
    }

    fun isReadyNow(): Boolean = _ready.value

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val TICK_MS = 200L
        const val FULL_PERCENT = 100
    }
}
