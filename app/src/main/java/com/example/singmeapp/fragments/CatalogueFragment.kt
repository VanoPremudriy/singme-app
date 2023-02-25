package com.example.singmeapp.fragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.CataloguePagerAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentCatalogueBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueNewsViewModel
import com.google.android.material.tabs.TabLayoutMediator

class CatalogueFragment : Fragment() {

    lateinit var fragActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueBinding
    lateinit var catalogueNewsViewModel: CatalogueNewsViewModel
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.catalogue)

        binding = FragmentCatalogueBinding.inflate(layoutInflater)

        binding.catalogueViewPager.adapter = CataloguePagerAdapter(this)
        binding.catalogueViewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.catalogueTabLayout, binding.catalogueViewPager){ tab, index ->
            tab.text = when(index){
                0 ->  getString(R.string.news)
                1 -> getString(R.string.popular)
                else -> {throw Resources.NotFoundException("14")}
            }
        }.attach()


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = CatalogueFragment()
    }

}