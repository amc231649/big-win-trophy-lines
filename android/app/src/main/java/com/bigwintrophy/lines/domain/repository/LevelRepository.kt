package com.bigwintrophy.lines.domain.repository

import com.bigwintrophy.lines.domain.model.LevelBoard

interface LevelRepository {

    fun levelCount(): Int

    fun loadBoard(levelId: Int): LevelBoard?

    fun levelTitle(levelId: Int): String

    fun levelCaption(levelId: Int): String

    fun levelTrophyName(levelId: Int): String

    fun levelSpriteKey(levelId: Int): String
}
