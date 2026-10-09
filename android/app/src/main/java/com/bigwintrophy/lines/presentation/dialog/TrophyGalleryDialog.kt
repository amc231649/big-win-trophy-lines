package com.bigwintrophy.lines.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bigwintrophy.lines.R
import com.bigwintrophy.lines.core.di.ServiceLocator
import com.bigwintrophy.lines.databinding.DialogTrophyGalleryBinding

class TrophyGalleryDialog : DialogFragment() {

    private var _binding: DialogTrophyGalleryBinding? = null
    private val binding get() = _binding

    private var adapter: GalleryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogTrophyGalleryBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeBinding = binding ?: return
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        safeBinding.galleryHeader.setEyebrow(getString(R.string.gallery_eyebrow))
        safeBinding.galleryHeader.setTitle(getString(R.string.gallery_title))
        val galleryAdapter = GalleryAdapter()
        adapter = galleryAdapter
        safeBinding.galleryList.layoutManager = GridLayoutManager(requireContext(), COLUMNS)
        safeBinding.galleryList.adapter = galleryAdapter
        galleryAdapter.submit(ServiceLocator.galleryUseCase(requireContext()).invoke())
        safeBinding.galleryClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    override fun onDestroyView() {
        val safeBinding = binding
        safeBinding?.galleryList?.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "dialog_trophy_gallery"
        private const val COLUMNS = 2
    }
}
