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
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentDiscographyAllBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.DiscographyAllViewModel


class DiscographyAllFragment : Fragment() {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var  binding: FragmentDiscographyAllBinding
    lateinit var tracksAdapter: TrackAdapter
    lateinit var albumsAdapter: AlbumAdapter
    lateinit var singlesAdapter: AlbumAdapter
    lateinit var discographyAllViewModel: DiscographyAllViewModel
    lateinit var band: Band
    lateinit var whatIs: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity =  activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        band = arguments?.getSerializable("band") as Band
        val provider = ViewModelProvider(this)
        discographyAllViewModel = provider[DiscographyAllViewModel::class.java]
        discographyAllViewModel.getMembers(band)
        whatIs = arguments?.getString("whatIs").toString()
        when(whatIs){
            "tracks" -> {
                fragmentActivity.title = getString(R.string.tracks)
                discographyAllViewModel.getTracks(band)
                tracksAdapter = TrackAdapter(activity as AppCompatActivity, this)
            }
            "albums" -> {
                fragmentActivity.title = getString(R.string.albums)
                discographyAllViewModel.getAlbums(band)
                albumsAdapter = AlbumAdapter(this)
            }
            "singles" -> {
                fragmentActivity.title = getString(R.string.singles_ep)
                discographyAllViewModel.getSingles(band)
                singlesAdapter = AlbumAdapter(this)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscographyAllBinding.inflate(layoutInflater)

        binding.rcDiscographyAll.layoutManager = LinearLayoutManager(context)

        when(whatIs){
            "tracks" -> {
                discographyAllViewModel.listTrack.observe(viewLifecycleOwner){
                    tracksAdapter.trackList.clear()
                    tracksAdapter.trackList.addAll(it as ArrayList<Track>) /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
                    binding.rcDiscographyAll.adapter = tracksAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["track"] == true && it["trackImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
            "albums" -> {
                discographyAllViewModel.listAlbum.observe(viewLifecycleOwner){
                    albumsAdapter.albumList.clear()
                    albumsAdapter.albumList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                    binding.rcDiscographyAll.adapter = albumsAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["albumImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
            "singles" -> {
                discographyAllViewModel.listSingle.observe(viewLifecycleOwner){
                    singlesAdapter.albumList.clear()
                    singlesAdapter.albumList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                    binding.rcDiscographyAll.adapter = singlesAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["singleImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
        }
        return binding.root
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

    companion object {

        @JvmStatic
        fun newInstance() = DiscographyAllFragment()
    }
}