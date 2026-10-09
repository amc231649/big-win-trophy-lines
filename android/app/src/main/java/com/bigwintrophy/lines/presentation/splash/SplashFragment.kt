package com.bigwintrophy.lines.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.core.navigation.Navigator
import com.bigwintrophy.lines.databinding.FragmentSplashBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding

    private var animator: SplashAnimator? = null
    private var navigated = false

    private val viewModel: SplashViewModel by viewModels {
        ServiceLocator.viewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentSplashBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        val splashAnimator = SplashAnimator(safeBinding)
        animator = splashAnimator
        splashAnimator.prepare()
        splashAnimator.playEntrance()
        splashAnimator.startLoops()
        observeTimer()
    }

    private fun observeTimer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectProgress() }
                launch { collectReady() }
            }
        }
    }

    private suspend fun collectProgress() {
        viewModel.percent.collectLatest { value ->
            val safeBinding = binding ?: return@collectLatest
            if (!isAdded) return@collectLatest
            val fraction = value / PERCENT_SCALE
            safeBinding.splashStatus.alpha = STATUS_MIN + STATUS_RANGE * fraction
        }
    }

    private suspend fun collectReady() {
        viewModel.ready.collectLatest { isReady ->
            if (!isReady) return@collectLatest
            if (!isAdded || binding == null) return@collectLatest
            if (navigated) return@collectLatest
            navigated = true
            Navigator.toMenu(parentFragmentManager)
        }
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PERCENT_SCALE = 100f
        const val STATUS_MIN = 0.45f
        const val STATUS_RANGE = 0.45f
    }
}
