package com.bigwintrophy.lines.data.repository

import com.bigwintrophy.lines.data.local.PreferencesStorage
import com.bigwintrophy.lines.domain.model.RoundResult
import com.bigwintrophy.lines.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val storage: PreferencesStorage,
    private val levelCount: Int
) : ProgressRepository {

    override fun currentLevel(): Int {
        val cleared = storage.readIntSet(PreferencesStorage.KEY_CLEARED_LEVELS)
        var candidate = 1
        while (candidate <= levelCount && cleared.contains(candidate)) candidate += 1
        return if (candidate > levelCount) levelCount else candidate
    }

    override fun clearedLevels(): Int =
        storage.readIntSet(PreferencesStorage.KEY_CLEARED_LEVELS).size

    override fun bestStreak(): Int =
        storage.readInt(PreferencesStorage.KEY_BEST_STREAK, 0)

    override fun trophyPoints(): Int =
        storage.readInt(PreferencesStorage.KEY_TROPHY_POINTS, 0)

    override fun isCleared(levelId: Int): Boolean =
        storage.readIntSet(PreferencesStorage.KEY_CLEARED_LEVELS).contains(levelId)

    override fun recordRound(result: RoundResult) {
        val points = storage.readInt(PreferencesStorage.KEY_TROPHY_POINTS, 0) + result.points
        storage.writeInt(PreferencesStorage.KEY_TROPHY_POINTS, points)
        if (result.isVictory) {
            val cleared = HashSet(storage.readIntSet(PreferencesStorage.KEY_CLEARED_LEVELS))
            cleared.add(result.levelId)
            storage.writeIntSet(PreferencesStorage.KEY_CLEARED_LEVELS, cleared)
            val streak = storage.readInt(PreferencesStorage.KEY_CURRENT_STREAK, 0) + 1
            storage.writeInt(PreferencesStorage.KEY_CURRENT_STREAK, streak)
            val best = storage.readInt(PreferencesStorage.KEY_BEST_STREAK, 0)
            if (streak > best) storage.writeInt(PreferencesStorage.KEY_BEST_STREAK, streak)
        } else {
            storage.writeInt(PreferencesStorage.KEY_CURRENT_STREAK, 0)
        }
    }

    override fun resetProgress() {
        storage.clearAll()
    }
}
