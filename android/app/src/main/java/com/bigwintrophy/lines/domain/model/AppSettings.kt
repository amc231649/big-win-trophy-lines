package com.bigwintrophy.lines.domain.model

data class AppSettings(
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean,
    val highContrastLines: Boolean
) {

    companion object {
        fun defaults(): AppSettings = AppSettings(
            soundEnabled = true,
            hapticsEnabled = true,
            highContrastLines = false
        )
    }
}
