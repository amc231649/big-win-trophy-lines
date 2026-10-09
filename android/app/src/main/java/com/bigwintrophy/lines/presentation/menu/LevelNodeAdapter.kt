package com.bigwintrophy.lines.presentation.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigwintrophy.lines.databinding.ItemLevelNodeBinding

class LevelNodeAdapter(
    private val onNodeSelected: (Int) -> Unit
) : RecyclerView.Adapter<LevelNodeAdapter.NodeHolder>() {

    private val items = ArrayList<MenuLevelNode>()

    fun submit(nodes: List<MenuLevelNode>) {
        items.clear()
        items.addAll(nodes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeHolder {
        val binding = ItemLevelNodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NodeHolder(binding, onNodeSelected)
    }

    override fun onBindViewHolder(holder: NodeHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class NodeHolder(
        private val binding: ItemLevelNodeBinding,
        private val onNodeSelected: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(node: MenuLevelNode) {
            binding.itemNode.bind(node.levelId, node.title, node.state)
            binding.itemNode.setOnClickListener {
                onNodeSelected(node.levelId)
            }
        }
    }
}
