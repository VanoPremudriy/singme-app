package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.PlaylistAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentCatalogueAllBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueAllViewModel
import com.example.singmeapp.viewmodels.DiscographyAllViewModel


class CatalogueAllFragment : Fragment() {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueAllBinding
    lateinit var tracksAdapter: TrackAdapter
    lateinit var albumsAdapter: AlbumAdapter
    lateinit var playlistAdapter: PlaylistAdapter
    lateinit var catalogueAllViewModel: CatalogueAllViewModel
    lateinit var whatIs: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val provider = ViewModelProvider(this)
        catalogueAllViewModel = provider[CatalogueAllViewModel::class.java]

        whatIs = arguments?.getString("whatIs").toString()
        when(whatIs){
            "newTracks" -> {
                fragmentActivity.title = getString(R.string.tracks)
                catalogueAllViewModel.getNewTracks()
                tracksAdapter = TrackAdapter(activity as AppCompatActivity, this)
            }
            "popularTracks" -> {
                fragmentActivity.title = getString(R.string.tracks)
                catalogueAllViewModel.getPopularTracks()
                tracksAdapter = TrackAdapter(activity as AppCompatActivity, this)
            }
            "newAlbums" -> {
                fragmentActivity.title = getString(R.string.albums)
                catalogueAllViewModel.getNewAlbums()
                albumsAdapter = AlbumAdapter(this)
            }
            "popularAlbums" -> {
                fragmentActivity.title = getString(R.string.albums)
                catalogueAllViewModel.getPopularAlbums()
                albumsAdapter = AlbumAdapter(this)
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCatalogueAllBinding.inflate(layoutInflater)

        binding.rvCatalogueAll.layoutManager = LinearLayoutManager(context)

        when (whatIs) {
            "newTracks", "popularTracks" -> {
                catalogueAllViewModel.listTrack.observe(viewLifecycleOwner) {
                    tracksAdapter.trackList.clear()
                    tracksAdapter.trackList.addAll(it as ArrayList<Track>) /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
                    binding.rvCatalogueAll.adapter = tracksAdapter
                }
            }


            "newAlbums", "popularAlbums" -> {
                catalogueAllViewModel.listAlbum.observe(viewLifecycleOwner) {
                    albumsAdapter.albumList.clear()
                    albumsAdapter.albumList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                    binding.rvCatalogueAll.adapter = albumsAdapter
                }
            }

        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CatalogueAllFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
            if (count == 0) {
                activity?.onBackPressed()
            } else {
                findNavController().popBackStack()
            }
        }

        return true
    }


}