package com.bigwintrophy.lines.presentation.menu

import androidx.lifecycle.ViewModel
import com.bigwintrophy.lines.core.ui.LevelNodeView
import com.bigwintrophy.lines.domain.repository.LevelRepository
import com.bigwintrophy.lines.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val levelRepository: LevelRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState.empty())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val total = levelRepository.levelCount()
        val current = progressRepository.currentLevel()
        val nodes = ArrayList<MenuLevelNode>(total)
        for (levelId in 1..total) {
            val nodeState = when {
                progressRepository.isCleared(levelId) -> LevelNodeView.NodeState.CLEARED
                levelId == current -> LevelNodeView.NodeState.CURRENT
                else -> LevelNodeView.NodeState.LOCKED
            }
            nodes.add(MenuLevelNode(levelId, levelRepository.levelTitle(levelId), nodeState))
        }
        _state.value = MenuUiState(
            currentLevel = current,
            currentTitle = levelRepository.levelTitle(current),
            currentTrophyName = levelRepository.levelTrophyName(current),
            trophyPoints = progressRepository.trophyPoints(),
            clearedLevels = progressRepository.clearedLevels(),
            totalLevels = total,
            nodes = nodes
        )
    }

    fun isPlayable(levelId: Int): Boolean {
        if (progressRepository.isCleared(levelId)) return true
        return levelId <= progressRepository.currentLevel()
    }
}
