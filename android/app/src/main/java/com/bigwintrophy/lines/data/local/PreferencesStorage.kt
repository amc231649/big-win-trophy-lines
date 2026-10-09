package com.bigwintrophy.lines.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readIntSet(key: String): Set<Int> {
        val stored = prefs.getStringSet(key, null) ?: return emptySet()
        val out = HashSet<Int>(stored.size)
        for (entry in stored) {
            val parsed = entry.toIntOrNull()
            if (parsed != null) out.add(parsed)
        }
        return out
    }

    fun writeIntSet(key: String, values: Set<Int>) {
        val encoded = HashSet<String>(values.size)
        for (value in values) encoded.add(value.toString())
        prefs.edit().putStringSet(key, encoded).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val STORE_NAME = "big_win_trophy_lines_store"
        const val KEY_CLEARED_LEVELS = "cleared_levels"
        const val KEY_TROPHY_POINTS = "trophy_points"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_CURRENT_STREAK = "current_streak"
        const val KEY_SOUND = "setting_sound"
        const val KEY_HAPTICS = "setting_haptics"
        const val KEY_CONTRAST = "setting_contrast"
    }
}
