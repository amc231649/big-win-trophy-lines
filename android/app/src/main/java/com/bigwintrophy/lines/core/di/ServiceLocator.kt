package com.bigwintrophy.lines.core.di

import android.content.Context
import com.bigwintrophy.lines.data.local.PreferencesStorage
import com.bigwintrophy.lines.data.repository.LevelRepositoryImpl
import com.bigwintrophy.lines.data.repository.ProgressRepositoryImpl
import com.bigwintrophy.lines.data.repository.SettingsRepositoryImpl
import com.bigwintrophy.lines.domain.repository.LevelRepository
import com.bigwintrophy.lines.domain.repository.ProgressRepository
import com.bigwintrophy.lines.domain.repository.SettingsRepository
import com.bigwintrophy.lines.domain.usecase.GetGalleryUseCase
import com.bigwintrophy.lines.domain.usecase.GetLevelUseCase
import com.bigwintrophy.lines.domain.usecase.SaveProgressUseCase
import com.bigwintrophy.lines.domain.usecase.ScoreRoundUseCase
import com.bigwintrophy.lines.domain.usecase.SolvePathHintUseCase

object ServiceLocator {

    private var storage: PreferencesStorage? = null
    private var levels: LevelRepository? = null
    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var factory: ViewModelFactory? = null

    fun init(context: Context) {
        if (factory != null) return
        val applicationContext = context.applicationContext
        val preferences = PreferencesStorage(applicationContext)
        val levelRepository = LevelRepositoryImpl()
        val progressRepository = ProgressRepositoryImpl(preferences, levelRepository.levelCount())
        val settingsRepository = SettingsRepositoryImpl(preferences)
        storage = preferences
        levels = levelRepository
        progress = progressRepository
        settings = settingsRepository
        factory = ViewModelFactory(
            levelRepository = levelRepository,
            progressRepository = progressRepository,
            getLevel = GetLevelUseCase(levelRepository),
            scoreRound = ScoreRoundUseCase(),
            saveProgress = SaveProgressUseCase(progressRepository),
            solveHint = SolvePathHintUseCase()
        )
    }

    fun viewModelFactory(context: Context): ViewModelFactory {
        init(context)
        return factory ?: ViewModelFactory(
            levelRepository = LevelRepositoryImpl(),
            progressRepository = ProgressRepositoryImpl(
                PreferencesStorage(context.applicationContext),
                LevelRepositoryImpl().levelCount()
            ),
            getLevel = GetLevelUseCase(LevelRepositoryImpl()),
            scoreRound = ScoreRoundUseCase(),
            saveProgress = SaveProgressUseCase(
                ProgressRepositoryImpl(
                    PreferencesStorage(context.applicationContext),
                    LevelRepositoryImpl().levelCount()
                )
            ),
            solveHint = SolvePathHintUseCase()
        )
    }

    fun levelRepository(context: Context): LevelRepository {
        init(context)
        return levels ?: LevelRepositoryImpl()
    }

    fun progressRepository(context: Context): ProgressRepository {
        init(context)
        return progress ?: ProgressRepositoryImpl(
            PreferencesStorage(context.applicationContext),
            LevelRepositoryImpl().levelCount()
        )
    }

    fun settingsRepository(context: Context): SettingsRepository {
        init(context)
        return settings ?: SettingsRepositoryImpl(PreferencesStorage(context.applicationContext))
    }

    fun galleryUseCase(context: Context): GetGalleryUseCase {
        init(context)
        return GetGalleryUseCase(levelRepository(context), progressRepository(context))
    }

    fun preferences(context: Context): PreferencesStorage {
        init(context)
        return storage ?: PreferencesStorage(context.applicationContext)
    }
}
