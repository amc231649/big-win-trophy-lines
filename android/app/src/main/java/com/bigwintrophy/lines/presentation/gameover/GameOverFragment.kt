package com.bigwintrophy.lines.presentation.gameover

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.config.GameConfig
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.core.navigation.Navigator
import com.bigwintrophy.lines.databinding.FragmentGameOverBinding
import com.bigwintrophy.lines.domain.model.RoundOutcome
import com.bigwintrophy.lines.domain.model.RoundResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameOverBinding? = null
    private val binding get() = _binding

    private var countAnimator: ValueAnimator? = null
    private var burstAnimator: ValueAnimator? = null

    private val viewModel: GameOverViewModel by viewModels {
        ServiceLocator.viewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) viewModel.bind(readResult(args))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameOverBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        safeBinding.overPlayAgain.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            Navigator.replayLevel(parentFragmentManager, viewModel.levelId())
        }
        safeBinding.overMenu.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            Navigator.backToMenu(parentFragmentManager)
        }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    if (state != null) render(state)
                }
            }
        }
    }

    private fun render(state: GameOverUiState) {
        val safeBinding = binding ?: return
        if (!isAdded) return
        val result = state.result
        safeBinding.overHeader.setTitle(result.levelTitle)
        if (result.isVictory) {
            safeBinding.overTitle.setText(R.string.over_victory_title)
            safeBinding.overCaption.setText(R.string.over_victory_caption)
            safeBinding.overHero.setImageResource(R.drawable.sprite_trophy_gold)
            safeBinding.overHero.contentDescription = getString(R.string.cd_hero_trophy)
            safeBinding.overBurst.visibility = View.VISIBLE
            startBurst()
        } else {
            safeBinding.overTitle.setText(R.string.over_called_title)
            safeBinding.overCaption.setText(R.string.over_called_caption)
            safeBinding.overHero.setImageResource(R.drawable.sprite_medal_silver)
            safeBinding.overHero.contentDescription = getString(R.string.cd_trophy_slot)
            safeBinding.overBurst.visibility = View.GONE
        }
        safeBinding.overStatPairs.setValue(
            getString(R.string.over_pairs_format, result.connectedPairs, result.totalPairs)
        )
        safeBinding.overStatMoves.setValue(result.moves.toString())
        safeBinding.overStatPoints.setValue(getString(R.string.over_points_format, result.points))
        updateStatsRowVisibility()
        playHeroEntrance()
        startCountUp(result.points)
    }

    private fun updateStatsRowVisibility() {
        val safeBinding = binding ?: return
        var visible = 0
        if (safeBinding.overStatPairs.hasValue()) visible += 1
        if (safeBinding.overStatMoves.hasValue()) visible += 1
        if (safeBinding.overStatPoints.hasValue()) visible += 1
        safeBinding.overResultCard.visibility = if (visible >= MIN_VISIBLE_STATS) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun playHeroEntrance() {
        val safeBinding = binding ?: return
        safeBinding.overHero.scaleX = HERO_START_SCALE
        safeBinding.overHero.scaleY = HERO_START_SCALE
        safeBinding.overHero.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(GameConfig.COUNT_UP_MS)
            .setInterpolator(OvershootInterpolator(HERO_TENSION))
            .start()
    }

    private fun startCountUp(points: Int) {
        countAnimator?.cancel()
        if (points <= 0) return
        val animator = ValueAnimator.ofInt(1, points)
        animator.duration = GameConfig.COUNT_UP_MS
        animator.addUpdateListener { value ->
            val safeBinding = binding
            if (safeBinding == null || !isAdded) return@addUpdateListener
            val shown = value.animatedValue as Int
            safeBinding.overStatPoints.setValue(getString(R.string.over_points_format, shown))
        }
        countAnimator = animator
        animator.start()
    }

    private fun startBurst() {
        burstAnimator?.cancel()
        val safeBinding = binding ?: return
        safeBinding.overBurst.alpha = 0f
        safeBinding.overBurst.rotation = 0f
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = BURST_MS
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { value ->
            val current = binding ?: return@addUpdateListener
            val fraction = value.animatedValue as Float
            current.overBurst.rotation = QUARTER_TURN * fraction
            current.overBurst.alpha = BURST_ALPHA * fraction
            current.overBurst.scaleX = BURST_MIN_SCALE + BURST_SWING * fraction
            current.overBurst.scaleY = BURST_MIN_SCALE + BURST_SWING * fraction
        }
        burstAnimator = animator
        animator.start()
    }

    override fun onDestroyView() {
        countAnimator?.cancel()
        countAnimator = null
        burstAnimator?.cancel()
        burstAnimator = null
        val safeBinding = binding
        safeBinding?.overHero?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_LEVEL_ID = "arg_level_id"
        private const val ARG_LEVEL_TITLE = "arg_level_title"
        private const val ARG_TROPHY_NAME = "arg_trophy_name"
        private const val ARG_VICTORY = "arg_victory"
        private const val ARG_CONNECTED = "arg_connected"
        private const val ARG_TOTAL = "arg_total"
        private const val ARG_MOVES = "arg_moves"
        private const val ARG_POINTS = "arg_points"
        private const val ARG_PERFECT = "arg_perfect"

        private const val HERO_START_SCALE = 0.8f
        private const val HERO_TENSION = 2f
        private const val BURST_MS = 900L
        private const val BURST_ALPHA = 0.30f
        private const val QUARTER_TURN = 90f
        private const val BURST_MIN_SCALE = 0.86f
        private const val BURST_SWING = 0.14f
        private const val MIN_VISIBLE_STATS = 2

        fun newInstance(result: RoundResult): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putInt(ARG_LEVEL_ID, result.levelId)
            args.putString(ARG_LEVEL_TITLE, result.levelTitle)
            args.putString(ARG_TROPHY_NAME, result.trophyName)
            args.putBoolean(ARG_VICTORY, result.isVictory)
            args.putInt(ARG_CONNECTED, result.connectedPairs)
            args.putInt(ARG_TOTAL, result.totalPairs)
            args.putInt(ARG_MOVES, result.moves)
            args.putInt(ARG_POINTS, result.points)
            args.putBoolean(ARG_PERFECT, result.perfectFill)
            fragment.arguments = args
            return fragment
        }

        private fun readResult(args: Bundle): RoundResult {
            val victory = args.getBoolean(ARG_VICTORY, false)
            return RoundResult(
                levelId = args.getInt(ARG_LEVEL_ID, 1),
                levelTitle = args.getString(ARG_LEVEL_TITLE).orEmpty(),
                trophyName = args.getString(ARG_TROPHY_NAME).orEmpty(),
                outcome = if (victory) RoundOutcome.VICTORY else RoundOutcome.TIME_CALLED,
                connectedPairs = args.getInt(ARG_CONNECTED, 0),
                totalPairs = args.getInt(ARG_TOTAL, 0),
                moves = args.getInt(ARG_MOVES, 0),
                points = args.getInt(ARG_POINTS, 0),
                perfectFill = args.getBoolean(ARG_PERFECT, false)
            )
        }
    }
}
