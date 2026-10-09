package com.bigwintrophy.lines.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.config.GameConfig
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.core.ext.ViewExt
import com.bigwintrophy.lines.core.navigation.Navigator
import com.bigwintrophy.lines.core.ui.TrophyBoardView
import com.bigwintrophy.lines.databinding.FragmentGameBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameFragment : Fragment(), TrophyBoardView.OnBoardEvent {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding

    private var levelId = 1
    private var attachedToken = -1
    private var navigated = false

    private val viewModel: GameViewModel by viewModels {
        ServiceLocator.viewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        levelId = arguments?.getInt(ARG_LEVEL_ID, 1) ?: 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        safeBinding.gameBoard.listener = this
        safeBinding.gameBoard.highContrast =
            ServiceLocator.settingsRepository(requireContext()).settings().highContrastLines
        safeBinding.gameBack.setOnClickListener { leaveToPrevious() }
        safeBinding.gameUndo.setOnClickListener { onUndoPressed() }
        safeBinding.gameHintButton.setOnClickListener { onHintPressed() }
        safeBinding.gameReset.setOnClickListener { onResetPressed() }
        safeBinding.gameRetry.setOnClickListener { viewModel.retry(levelId) }
        observeState()
        viewModel.start(levelId)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state -> render(state) }
            }
        }
    }

    private fun render(state: GameUiState) {
        val safeBinding = binding ?: return
        if (!isAdded) return
        when (state) {
            is GameUiState.Loading -> {
                safeBinding.gameErrorCard.visibility = View.GONE
                safeBinding.gameBoard.alpha = LOADING_ALPHA
            }

            is GameUiState.Playing -> {
                safeBinding.gameErrorCard.visibility = View.GONE
                safeBinding.gameBoard.visibility = View.VISIBLE
                safeBinding.gameBoard.alpha = 1f
                if (attachedToken != state.boardToken) {
                    attachedToken = state.boardToken
                    safeBinding.gameBoard.setBoard(state.board)
                }
                safeBinding.gameTitle.text =
                    getString(R.string.game_title_format, state.board.id, state.board.title)
                safeBinding.gameMoves.text = getString(R.string.game_moves_format, state.moves)
                safeBinding.gameHint.text = getString(state.statusRes)
                val percent = if (state.total > 0) state.connected * FULL_PERCENT / state.total else 0
                safeBinding.gamePairProgress.progress = percent
                ViewCompat.setStateDescription(
                    safeBinding.gamePairProgress,
                    getString(R.string.state_pairs_format, state.connected, state.total)
                )
                ViewExt.setEnabledAppearance(safeBinding.gameHintButton, state.hintsLeft > 0)
                ViewExt.setEnabledAppearance(safeBinding.gameUndo, state.moves > 0)
            }

            is GameUiState.Finished -> {
                if (navigated) return
                navigated = true
                Navigator.toGameOver(parentFragmentManager, state.result)
            }

            is GameUiState.Error -> {
                safeBinding.gameErrorCard.visibility = View.VISIBLE
                safeBinding.gameBoard.visibility = View.INVISIBLE
            }
        }
    }

    private fun onUndoPressed() {
        val safeBinding = binding ?: return
        val changed = safeBinding.gameBoard.undoMove()
        if (!changed) return
        pushBoardState()
    }

    private fun onHintPressed() {
        val safeBinding = binding ?: return
        val engine = safeBinding.gameBoard.engineOrNull() ?: return
        val pair = viewModel.requestHint(engine.connectedPairIds())
        if (pair == null) {
            Snackbar.make(
                safeBinding.root,
                getString(R.string.game_no_hints),
                Snackbar.LENGTH_SHORT
            ).setDuration(GameConfig.SNACK_MS.toInt()).show()
            return
        }
        safeBinding.gameBoard.applyHint(pair)
        pushBoardState()
    }

    private fun onResetPressed() {
        val safeBinding = binding ?: return
        safeBinding.gameBoard.resetBoard()
        viewModel.resetRound()
    }

    private fun leaveToPrevious() {
        if (!isAdded) return
        parentFragmentManager.popBackStack()
    }

    private fun pushBoardState() {
        val safeBinding = binding ?: return
        val engine = safeBinding.gameBoard.engineOrNull() ?: return
        viewModel.onBoardChanged(engine.moves, engine.connectedCount(), engine.perfectFill())
    }

    override fun onMoveMade(moves: Int, connected: Int, total: Int) {
        if (!isAdded) return
        val safeBinding = binding ?: return
        val engine = safeBinding.gameBoard.engineOrNull() ?: return
        viewModel.onBoardChanged(moves, connected, engine.perfectFill())
    }

    override fun onPairClosed(connected: Int, total: Int) {
        if (!isAdded) return
        val safeBinding = binding ?: return
        safeBinding.gameHint.text = getString(R.string.game_hint_closed)
    }

    override fun onSolved() {
        if (!isAdded) return
        viewModel.onSolved()
    }

    override fun onDestroyView() {
        val safeBinding = binding
        if (safeBinding != null) {
            safeBinding.gameBoard.listener = null
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_LEVEL_ID = "arg_level_id"
        private const val LOADING_ALPHA = 0.3f
        private const val FULL_PERCENT = 100

        fun newInstance(levelId: Int): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putInt(ARG_LEVEL_ID, levelId)
            fragment.arguments = args
            return fragment
        }
    }
}
