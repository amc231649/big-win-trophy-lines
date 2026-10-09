package com.bigwintrophy.lines.domain.repository

import com.bigwintrophy.lines.domain.model.AppSettings

interface SettingsRepository {

    fun settings(): AppSettings

    fun update(settings: AppSettings)
}
