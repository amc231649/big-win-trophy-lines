package com.bigwintrophy.lines.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = ContextCompat.getColor(context, R.color.trophy_gold)

    init {
        if (background == null) {
            setBackgroundResource(R.drawable.card_surface)
        }
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
        val iconRes = typed.getResourceId(R.styleable.StatCardView_appIcon, 0)
        val value = typed.getString(R.styleable.StatCardView_appValue)
        val label = typed.getString(R.styleable.StatCardView_appLabel)
        val description = typed.getString(R.styleable.StatCardView_appIconDescription)
        accentColor = typed.getColor(R.styleable.StatCardView_appAccent, accentColor)
        typed.recycle()
        if (iconRes != 0) setIcon(iconRes, description)
        setLabel(label)
        setValue(value.orEmpty())
        applyAccent()
    }

    fun setIcon(resId: Int, description: String?) {
        binding.statIcon.setImageResource(resId)
        if (description.isNullOrEmpty()) {
            binding.statIcon.contentDescription = context.getString(R.string.cd_trophy_slot)
        } else {
            binding.statIcon.contentDescription = description
        }
    }

    fun setLabel(text: String?) {
        binding.statLabel.text = text.orEmpty()
    }

    fun setAccent(color: Int) {
        accentColor = color
        applyAccent()
    }

    fun setValue(text: String) {
        binding.statValue.text = text
        visibility = if (isBlankValue(text)) GONE else VISIBLE
    }

    fun hasValue(): Boolean = !isBlankValue(binding.statValue.text.toString())

    private fun isBlankValue(text: String): Boolean =
        text.isEmpty() || text == ZERO || text == PLUS_ZERO

    private fun applyAccent() {
        binding.statValue.setTextColor(accentColor)
        binding.statAccent.backgroundTintList = ColorStateList.valueOf(accentColor)
    }

    private companion object {
        const val ZERO = "0"
        const val PLUS_ZERO = "+0"
    }
}
