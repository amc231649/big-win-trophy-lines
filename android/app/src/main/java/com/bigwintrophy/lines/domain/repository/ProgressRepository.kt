package com.bigwintrophy.lines.domain.repository

import com.bigwintrophy.lines.domain.model.RoundResult

interface ProgressRepository {

    fun currentLevel(): Int

    fun clearedLevels(): Int

    fun bestStreak(): Int

    fun trophyPoints(): Int

    fun isCleared(levelId: Int): Boolean

    fun recordRound(result: RoundResult)

    fun resetProgress()
}
