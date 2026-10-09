package com.bigwintrophy.lines.core.navigation

import androidx.fragment.app.FragmentManager
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.domain.model.RoundResult
import com.bigwintrophy.lines.presentation.game.GameFragment
import com.bigwintrophy.lines.presentation.gameover.GameOverFragment
import com.bigwintrophy.lines.presentation.menu.MenuFragment
import com.bigwintrophy.lines.presentation.splash.SplashFragment

object Navigator {

    const val TAG_SPLASH = "screen_splash"
    const val TAG_MENU = "screen_menu"
    const val TAG_GAME = "screen_game"
    const val TAG_GAME_OVER = "screen_game_over"

    private val CONTAINER = R.id.fragment_container

    fun showSplash(manager: FragmentManager) {
        if (manager.findFragmentByTag(TAG_SPLASH) != null) return
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(CONTAINER, SplashFragment(), TAG_SPLASH)
            .commitAllowingStateLoss()
    }

    fun toMenu(manager: FragmentManager) {
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(CONTAINER, MenuFragment(), TAG_MENU)
            .commitAllowingStateLoss()
    }

    fun toGame(manager: FragmentManager, levelId: Int) {
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out_scale,
                R.anim.scale_in,
                R.anim.slide_out_right
            )
            .replace(CONTAINER, GameFragment.newInstance(levelId), TAG_GAME)
            .addToBackStack(TAG_GAME)
            .commitAllowingStateLoss()
    }

    fun toGameOver(manager: FragmentManager, result: RoundResult) {
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.scale_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out_scale
            )
            .replace(CONTAINER, GameOverFragment.newInstance(result), TAG_GAME_OVER)
            .addToBackStack(TAG_GAME_OVER)
            .commitAllowingStateLoss()
    }

    fun backToMenu(manager: FragmentManager) {
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun replayLevel(manager: FragmentManager, levelId: Int) {
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        toGame(manager, levelId)
    }
}
