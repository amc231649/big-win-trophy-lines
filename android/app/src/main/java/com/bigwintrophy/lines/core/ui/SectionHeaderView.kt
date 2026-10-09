package com.bigwintrophy.lines.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        val typed = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderView)
        val eyebrow = typed.getString(R.styleable.SectionHeaderView_appEyebrow)
        val title = typed.getString(R.styleable.SectionHeaderView_appTitle)
        typed.recycle()
        setEyebrow(eyebrow)
        setTitle(title)
    }

    fun setEyebrow(text: String?) {
        binding.headerEyebrow.text = text.orEmpty()
        binding.headerEyebrow.visibility = if (text.isNullOrEmpty()) GONE else VISIBLE
    }

    fun setTitle(text: String?) {
        binding.headerTitle.text = text.orEmpty()
        binding.headerTitle.visibility = if (text.isNullOrEmpty()) GONE else VISIBLE
    }
}
