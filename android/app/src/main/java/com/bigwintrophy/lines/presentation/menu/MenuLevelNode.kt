package com.bigwintrophy.lines.presentation.menu

import com.bigwintrophy.lines.core.ui.LevelNodeView

data class MenuLevelNode(
    val levelId: Int,
    val title: String,
    val state: LevelNodeView.NodeState
)
