package com.bigwintrophy.lines.presentation.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.ui.SpriteCatalog
import com.bigwintrophy.lines.databinding.ItemGalleryTrophyBinding
import com.bigwintrophy.lines.domain.model.GalleryTrophy

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.TrophyHolder>() {

    private val items = ArrayList<GalleryTrophy>()

    fun submit(trophies: List<GalleryTrophy>) {
        items.clear()
        items.addAll(trophies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyHolder {
        val binding = ItemGalleryTrophyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrophyHolder(binding)
    }

    override fun onBindViewHolder(holder: TrophyHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TrophyHolder(
        private val binding: ItemGalleryTrophyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trophy: GalleryTrophy) {
            val context = binding.root.context
            binding.gallerySprite.setImageResource(SpriteCatalog.resourceFor(trophy.spriteKey))
            binding.galleryName.text = trophy.name
            if (trophy.isEarned) {
                binding.root.alpha = 1f
                binding.galleryCaption.text = trophy.caption
                binding.gallerySprite.contentDescription = trophy.name
                ViewCompat.setStateDescription(
                    binding.root,
                    context.getString(R.string.state_cleared)
                )
            } else {
                binding.root.alpha = LOCKED_ALPHA
                binding.galleryCaption.setText(R.string.gallery_locked)
                binding.gallerySprite.contentDescription =
                    context.getString(R.string.cd_node_format, trophy.levelId, context.getString(R.string.state_locked))
                ViewCompat.setStateDescription(
                    binding.root,
                    context.getString(R.string.state_locked)
                )
            }
            binding.root.visibility = View.VISIBLE
        }

        private companion object {
            const val LOCKED_ALPHA = 0.38f
        }
    }
}
