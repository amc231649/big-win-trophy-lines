package com.bigwintrophy.lines.domain.usecase

import com.bigwintrophy.lines.domain.model.GalleryTrophy
import com.bigwintrophy.lines.domain.repository.LevelRepository
import com.bigwintrophy.lines.domain.repository.ProgressRepository

class GetGalleryUseCase(
    private val levelRepository: LevelRepository,
    private val progressRepository: ProgressRepository
) {

    operator fun invoke(): List<GalleryTrophy> {
        val total = levelRepository.levelCount()
        val out = ArrayList<GalleryTrophy>(total)
        for (levelId in 1..total) {
            out.add(
                GalleryTrophy(
                    levelId = levelId,
                    name = levelRepository.levelTrophyName(levelId),
                    caption = levelRepository.levelCaption(levelId),
                    spriteKey = levelRepository.levelSpriteKey(levelId),
                    isEarned = progressRepository.isCleared(levelId)
                )
            )
        }
        return out
    }
}
