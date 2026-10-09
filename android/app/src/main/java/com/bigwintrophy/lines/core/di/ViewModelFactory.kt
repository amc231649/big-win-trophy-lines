package com.bigwintrophy.lines.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bigwintrophy.lines.domain.repository.LevelRepository
import com.bigwintrophy.lines.domain.repository.ProgressRepository
import com.bigwintrophy.lines.domain.usecase.GetLevelUseCase
import com.bigwintrophy.lines.domain.usecase.SaveProgressUseCase
import com.bigwintrophy.lines.domain.usecase.ScoreRoundUseCase
import com.bigwintrophy.lines.domain.usecase.SolvePathHintUseCase
import com.bigwintrophy.lines.presentation.game.GameViewModel
import com.bigwintrophy.lines.presentation.gameover.GameOverViewModel
import com.bigwintrophy.lines.presentation.menu.MenuViewModel
import com.bigwintrophy.lines.presentation.splash.SplashViewModel

class ViewModelFactory(
    private val levelRepository: LevelRepository,
    private val progressRepository: ProgressRepository,
    private val getLevel: GetLevelUseCase,
    private val scoreRound: ScoreRoundUseCase,
    private val saveProgress: SaveProgressUseCase,
    private val solveHint: SolvePathHintUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(levelRepository, progressRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(getLevel, scoreRound, saveProgress, solveHint)

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel()
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
