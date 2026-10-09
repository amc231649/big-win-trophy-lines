package com.bigwintrophy.lines.presentation.splash

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.bigwintrophy.lines.core.config.GameConfig
import com.bigwintrophy.lines.databinding.FragmentSplashBinding

class SplashAnimator(private val binding: FragmentSplashBinding) {

    private val running = ArrayList<ValueAnimator>()
    private val animatedViews = ArrayList<View>()

    fun prepare() {
        binding.splashEmblem.alpha = 0f
        binding.splashEmblem.scaleX = EMBLEM_START_SCALE
        binding.splashEmblem.scaleY = EMBLEM_START_SCALE
        binding.splashTitleTop.alpha = 0f
        binding.splashTitleTop.translationY = titleOffset()
        binding.splashTitleBottom.alpha = 0f
        binding.splashTitleBottom.translationY = titleOffset()
        binding.splashSubtitle.alpha = 0f
        binding.splashStatus.alpha = 0f
        binding.splashRing.alpha = 0f
        binding.splashRing.scaleX = 1f
        binding.splashRing.scaleY = 1f
    }

    fun playEntrance() {
        track(binding.splashEmblem)
        binding.splashEmblem.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(EMBLEM_MS)
            .setInterpolator(OvershootInterpolator(EMBLEM_TENSION))
            .start()

        track(binding.splashTitleTop)
        binding.splashTitleTop.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(TITLE_DELAY_MS)
            .setDuration(TITLE_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()

        track(binding.splashTitleBottom)
        binding.splashTitleBottom.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(TITLE_DELAY_MS + SUBTITLE_STEP_MS)
            .setDuration(TITLE_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()

        track(binding.splashSubtitle)
        binding.splashSubtitle.animate()
            .alpha(1f)
            .setStartDelay(TITLE_DELAY_MS + SUBTITLE_STEP_MS * 2)
            .setDuration(TITLE_MS)
            .start()

        track(binding.splashStatus)
        binding.splashStatus.animate()
            .alpha(STATUS_ALPHA)
            .setStartDelay(TITLE_DELAY_MS + SUBTITLE_STEP_MS * 3)
            .setDuration(TITLE_MS)
            .start()
    }

    fun startLoops() {
        startRingPulse()
        startChevronDrift(binding.splashChevronOne, CHEVRON_ONE_MS, CHEVRON_ONE_RANGE)
        startChevronDrift(binding.splashChevronTwo, CHEVRON_TWO_MS, CHEVRON_TWO_RANGE)
        startChevronDrift(binding.splashChevronThree, CHEVRON_THREE_MS, CHEVRON_THREE_RANGE)
    }

    private fun startRingPulse() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.RING_PULSE_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { value ->
            val fraction = value.animatedValue as Float
            val scale = RING_MIN_SCALE + (RING_MAX_SCALE - RING_MIN_SCALE) * fraction
            binding.splashRing.scaleX = scale
            binding.splashRing.scaleY = scale
            binding.splashRing.alpha = RING_MAX_ALPHA * (1f - fraction)
        }
        running.add(animator)
        animator.start()
    }

    private fun startChevronDrift(target: View, duration: Long, rangeDp: Float) {
        val range = rangeDp * target.resources.displayMetrics.density
        val animator = ValueAnimator.ofFloat(range, -range)
        animator.duration = duration
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { value ->
            target.translationY = value.animatedValue as Float
        }
        running.add(animator)
        animator.start()
    }

    private fun track(view: View) {
        if (!animatedViews.contains(view)) animatedViews.add(view)
    }

    private fun titleOffset(): Float =
        TITLE_OFFSET_DP * binding.root.resources.displayMetrics.density

    fun cancel() {
        for (animator in running) animator.cancel()
        running.clear()
        for (view in animatedViews) view.animate().cancel()
        animatedViews.clear()
    }

    private companion object {
        const val EMBLEM_START_SCALE = 0.78f
        const val EMBLEM_MS = 620L
        const val EMBLEM_TENSION = 1.4f
        const val TITLE_MS = 520L
        const val TITLE_DELAY_MS = 180L
        const val SUBTITLE_STEP_MS = 120L
        const val TITLE_OFFSET_DP = 24f
        const val STATUS_ALPHA = 0.7f
        const val RING_MIN_SCALE = 1.0f
        const val RING_MAX_SCALE = 1.18f
        const val RING_MAX_ALPHA = 0.5f
        const val CHEVRON_ONE_MS = 5200L
        const val CHEVRON_TWO_MS = 6400L
        const val CHEVRON_THREE_MS = 7200L
        const val CHEVRON_ONE_RANGE = 18f
        const val CHEVRON_TWO_RANGE = 26f
        const val CHEVRON_THREE_RANGE = 22f
    }
}
