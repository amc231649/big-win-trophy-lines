package com.bigwintrophy.lines.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.core.ext.ViewExt
import com.bigwintrophy.lines.core.navigation.Navigator
import com.bigwintrophy.lines.databinding.FragmentMenuBinding
import com.bigwintrophy.lines.presentation.dialog.SettingsDialog
import com.bigwintrophy.lines.presentation.dialog.TrophyGalleryDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding

    private var adapter: LevelNodeAdapter? = null

    private val viewModel: MenuViewModel by viewModels {
        ServiceLocator.viewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentMenuBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        val nodeAdapter = LevelNodeAdapter { levelId -> openLevel(levelId) }
        adapter = nodeAdapter
        safeBinding.menuLevelMap.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        safeBinding.menuLevelMap.adapter = nodeAdapter
        safeBinding.menuPlayButton.setOnClickListener {
            openLevel(viewModel.state.value.currentLevel)
        }
        safeBinding.menuStatTrophies.setOnClickListener {
            TrophyGalleryDialog().show(parentFragmentManager, TrophyGalleryDialog.TAG)
        }
        safeBinding.menuSettings.setOnClickListener {
            SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
        }
        playEntrance(safeBinding)
        observeState()
    }

    private fun playEntrance(safeBinding: FragmentMenuBinding) {
        ViewExt.playEntrance(safeBinding.menuCeremonyCard, 0)
        ViewExt.playEntrance(safeBinding.menuStatsRow, 1)
        ViewExt.playEntrance(safeBinding.menuHint, 2)
        ViewExt.playEntrance(safeBinding.menuPlayTile, 3)
        ViewExt.playEntrance(safeBinding.menuMapCard, 4)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state -> render(state) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val safeBinding = binding ?: return
        if (!isAdded) return
        safeBinding.menuPointsValue.text =
            getString(R.string.menu_points_format, state.trophyPoints)
        safeBinding.menuLevelLabel.text =
            getString(R.string.menu_level_format, state.currentLevel)
        safeBinding.menuLevelTitle.text = state.currentTitle
        safeBinding.menuCeremonyHeader.setTitle(state.currentTrophyName)
        safeBinding.menuStatLevel.setValue(state.currentLevel.toString())
        safeBinding.menuStatTrophies.setValue(
            getString(
                R.string.menu_trophies_format,
                state.clearedLevels,
                state.totalLevels
            )
        )
        adapter?.submit(state.nodes)
    }

    private fun openLevel(levelId: Int) {
        if (!isAdded) return
        if (!viewModel.isPlayable(levelId)) return
        Navigator.toGame(parentFragmentManager, levelId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        val safeBinding = binding
        if (safeBinding != null) {
            ViewExt.cancelEntrance(safeBinding.menuCeremonyCard)
            ViewExt.cancelEntrance(safeBinding.menuStatsRow)
            ViewExt.cancelEntrance(safeBinding.menuHint)
            ViewExt.cancelEntrance(safeBinding.menuPlayTile)
            ViewExt.cancelEntrance(safeBinding.menuMapCard)
            safeBinding.menuLevelMap.adapter = null
        }
        adapter = null
        _binding = null
        super.onDestroyView()
    }
}
