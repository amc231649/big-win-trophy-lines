package com.bigwintrophy.lines.data.repository

import com.bigwintrophy.lines.data.local.PreferencesStorage
import com.bigwintrophy.lines.domain.model.AppSettings
import com.bigwintrophy.lines.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val storage: PreferencesStorage) : SettingsRepository {

    override fun settings(): AppSettings {
        val defaults = AppSettings.defaults()
        return AppSettings(
            soundEnabled = storage.readBoolean(PreferencesStorage.KEY_SOUND, defaults.soundEnabled),
            hapticsEnabled = storage.readBoolean(
                PreferencesStorage.KEY_HAPTICS,
                defaults.hapticsEnabled
            ),
            highContrastLines = storage.readBoolean(
                PreferencesStorage.KEY_CONTRAST,
                defaults.highContrastLines
            )
        )
    }

    override fun update(settings: AppSettings) {
        storage.writeBoolean(PreferencesStorage.KEY_SOUND, settings.soundEnabled)
        storage.writeBoolean(PreferencesStorage.KEY_HAPTICS, settings.hapticsEnabled)
        storage.writeBoolean(PreferencesStorage.KEY_CONTRAST, settings.highContrastLines)
    }
}
