package com.bigwintrophy.lines.core.ext

import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import com.bigwintrophy.lines.core.config.GameConfig

object ViewExt {

    fun prepareForEntrance(view: View, offsetDp: Float) {
        view.alpha = 0f
        view.translationY = offsetDp * view.resources.displayMetrics.density
    }

    fun playEntrance(view: View, index: Int) {
        view.animate().cancel()
        view.alpha = 0f
        view.translationY = ENTRANCE_OFFSET_DP * view.resources.displayMetrics.density
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(index * GameConfig.ENTRANCE_STAGGER_MS)
            .setDuration(GameConfig.ENTRANCE_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    fun cancelEntrance(view: View) {
        view.animate().cancel()
        view.alpha = 1f
        view.translationY = 0f
    }

    fun setState(view: View, state: String) {
        ViewCompat.setStateDescription(view, state)
    }

    fun setEnabledAppearance(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        view.alpha = if (enabled) 1f else DISABLED_ALPHA
    }

    private const val ENTRANCE_OFFSET_DP = 28f
    private const val DISABLED_ALPHA = 0.38f
}
