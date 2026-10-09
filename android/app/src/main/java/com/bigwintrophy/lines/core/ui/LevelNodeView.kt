package com.bigwintrophy.lines.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.databinding.ViewLevelNodeBinding

class LevelNodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class NodeState {
        LOCKED,
        CURRENT,
        CLEARED
    }

    private val binding = ViewLevelNodeBinding.inflate(LayoutInflater.from(context), this)

    private var nodeIndex: Int = 1

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.LevelNodeView)
        nodeIndex = typed.getInt(R.styleable.LevelNodeView_appNodeIndex, 1)
        typed.recycle()
        bind(nodeIndex, "", NodeState.LOCKED)
    }

    fun bind(index: Int, caption: String, state: NodeState) {
        nodeIndex = index
        binding.nodeCaption.text = caption
        when (state) {
            NodeState.LOCKED -> {
                binding.nodeDisc.setBackgroundResource(R.drawable.node_locked)
                binding.nodeBadge.setImageResource(R.drawable.ic_lock)
                binding.nodeBadge.visibility = VISIBLE
                binding.nodeIndex.visibility = GONE
                alpha = LOCKED_ALPHA
                isEnabled = false
            }

            NodeState.CURRENT -> {
                binding.nodeDisc.setBackgroundResource(R.drawable.node_current)
                binding.nodeBadge.visibility = GONE
                binding.nodeIndex.visibility = VISIBLE
                binding.nodeIndex.text = index.toString()
                binding.nodeIndex.setTextColor(
                    ContextCompat.getColor(context, R.color.text_primary)
                )
                alpha = 1f
                isEnabled = true
            }

            NodeState.CLEARED -> {
                binding.nodeDisc.setBackgroundResource(R.drawable.node_cleared)
                binding.nodeBadge.setImageResource(R.drawable.ic_check)
                binding.nodeBadge.visibility = VISIBLE
                binding.nodeIndex.visibility = GONE
                alpha = 1f
                isEnabled = true
            }
        }
        val stateLabel = when (state) {
            NodeState.LOCKED -> context.getString(R.string.state_locked)
            NodeState.CURRENT -> context.getString(R.string.state_current)
            NodeState.CLEARED -> context.getString(R.string.state_cleared)
        }
        contentDescription = context.getString(R.string.cd_node_format, index, stateLabel)
        ViewCompat.setStateDescription(this, stateLabel)
    }

    fun index(): Int = nodeIndex

    private companion object {
        const val LOCKED_ALPHA = 0.38f
    }
}
