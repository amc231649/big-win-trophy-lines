package com.bigwintrophy.lines.presentation.menu

data class MenuUiState(
    val currentLevel: Int,
    val currentTitle: String,
    val currentTrophyName: String,
    val trophyPoints: Int,
    val clearedLevels: Int,
    val totalLevels: Int,
    val nodes: List<MenuLevelNode>
) {

    companion object {
        fun empty(): MenuUiState = MenuUiState(
            currentLevel = 1,
            currentTitle = "",
            currentTrophyName = "",
            trophyPoints = 0,
            clearedLevels = 0,
            totalLevels = 0,
            nodes = emptyList()
        )
    }
}
