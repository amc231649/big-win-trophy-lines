package com.bigwintrophy.lines.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.databinding.DialogSettingsBinding
import com.bigwintrophy.lines.domain.model.AppSettings
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding

    private var confirmArmed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogSettingsBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        safeBinding.settingsHeader.setEyebrow(getString(R.string.settings_eyebrow))
        safeBinding.settingsHeader.setTitle(getString(R.string.settings_title))
        val repository = ServiceLocator.settingsRepository(requireContext())
        val current = repository.settings()
        applyState(safeBinding.settingsSound, current.soundEnabled)
        applyState(safeBinding.settingsHaptics, current.hapticsEnabled)
        applyState(safeBinding.settingsContrast, current.highContrastLines)
        safeBinding.settingsSound.setOnCheckedChangeListener { button, checked ->
            applyState(button as MaterialSwitch, checked)
            persist()
        }
        safeBinding.settingsHaptics.setOnCheckedChangeListener { button, checked ->
            applyState(button as MaterialSwitch, checked)
            persist()
        }
        safeBinding.settingsContrast.setOnCheckedChangeListener { button, checked ->
            applyState(button as MaterialSwitch, checked)
            persist()
        }
        safeBinding.settingsReset.setOnClickListener { onResetPressed() }
        safeBinding.settingsClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun applyState(target: MaterialSwitch, checked: Boolean) {
        target.isChecked = checked
        val label = if (checked) {
            getString(R.string.state_on)
        } else {
            getString(R.string.state_off)
        }
        ViewCompat.setStateDescription(target, label)
        target.alpha = if (checked) 1f else DIMMED_ALPHA
    }

    private fun persist() {
        val safeBinding = binding ?: return
        if (!isAdded) return
        ServiceLocator.settingsRepository(requireContext()).update(
            AppSettings(
                soundEnabled = safeBinding.settingsSound.isChecked,
                hapticsEnabled = safeBinding.settingsHaptics.isChecked,
                highContrastLines = safeBinding.settingsContrast.isChecked
            )
        )
    }

    private fun onResetPressed() {
        val safeBinding = binding ?: return
        if (!confirmArmed) {
            confirmArmed = true
            safeBinding.settingsReset.setText(R.string.btn_reset_confirm)
            return
        }
        ServiceLocator.progressRepository(requireContext()).resetProgress()
        confirmArmed = false
        safeBinding.settingsReset.setText(R.string.btn_reset_progress)
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "dialog_settings"
        private const val DIMMED_ALPHA = 0.6f
    }
}
