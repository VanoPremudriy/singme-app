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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
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
    lateinit var bandAdapter: BandAdapter
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

        initContent()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.title = getString(R.string.catalogue)
        binding = FragmentCatalogueAllBinding.inflate(layoutInflater)

        binding.rvCatalogueAll.layoutManager = LinearLayoutManager(context)

        observe()


        return binding.root
    }

    fun initContent(){

        if (whatIs.contains("Tracks")){
            fragmentActivity.title = getString(R.string.tracks)
            tracksAdapter = TrackAdapter(activity as AppCompatActivity, this)
        } else if (whatIs.contains("Albums")){
            fragmentActivity.title = getString(R.string.albums)
            albumsAdapter = AlbumAdapter(this)
        } else if (whatIs.contains("Bands")){
            fragmentActivity.title = getString(R.string.Bands)
            bandAdapter = BandAdapter(this)
        } else if (whatIs.contains("Playlists")){
            fragmentActivity.title = getString(R.string.Playlists)
            playlistAdapter = PlaylistAdapter(this)
        }


        when(whatIs){
            "newTracks" -> {
                catalogueAllViewModel.getNewTracks()
            }
            "popularTracks" -> {
                catalogueAllViewModel.getPopularTracks()
            }
            "myTracks" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchTracks(search ?: "", "user")
            }
            "allTracks" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchTracks(search ?: "", "all")
            }
            "newAlbums" -> {
                catalogueAllViewModel.getNewAlbums()
            }
            "popularAlbums" -> {
                catalogueAllViewModel.getPopularAlbums()
            }
            "myAlbums" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchAlbums(search ?: "", "user")
            }
            "allAlbums" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchAlbums(search ?: "", "all")
            }
            "newBands" -> {
                catalogueAllViewModel.getNewBands()
            }
            "myBands" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchBands(search ?: "", "user")
            }
            "allBands" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchBands(search ?: "", "all")
            }
            "myPlaylists" -> {
                val search = arguments?.getString("search")
                catalogueAllViewModel.getSearchPlaylists(search ?: "")
            }
        }
    }

    fun observe(){
        when (whatIs) {
            "newTracks" -> observeTrack()
            "popularTracks" -> observeTrack()
            "myTracks" ->  observeTrack()
            "allTracks" ->  observeTrack()
            "newAlbums" -> observeAlbum()
            "popularAlbums" -> observeAlbum()
            "myAlbums" -> observeAlbum()
            "allAlbums" -> observeAlbum()
            "newBands" -> observeBand()
            "myBands" ->  observeBand()
            "allBands" ->  observeBand()
            "myPlaylists" -> observePlaylist()
        }
    }

    fun observeTrack(){
        catalogueAllViewModel.listTrack.observe(viewLifecycleOwner) {
            tracksAdapter.trackList.clear()
            tracksAdapter.trackList.addAll(it as ArrayList<Track>) /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rvCatalogueAll.adapter = tracksAdapter
        }

        catalogueAllViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["track"] == true && it["trackImage"] == true){
                binding.catalogueAllProgressLayout.visibility = View.GONE
            }
        }
    }

    fun observeAlbum(){
        catalogueAllViewModel.listAlbum.observe(viewLifecycleOwner) {
            albumsAdapter.albumList.clear()
            albumsAdapter.albumList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCatalogueAll.adapter = albumsAdapter
        }

        catalogueAllViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["albumImage"] == true){
                binding.catalogueAllProgressLayout.visibility = View.GONE
            }
        }
    }

    fun observeBand(){
        binding.rvCatalogueAll.layoutManager = GridLayoutManager(context, 3)
        catalogueAllViewModel.listBand.observe(viewLifecycleOwner){
            bandAdapter.bandList.clear()
            bandAdapter.bandList.addAll(it as ArrayList<Band>)
            binding.rvCatalogueAll.adapter = bandAdapter
        }

        catalogueAllViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["bandImage"] == true && it["bandBack"] == true){
                binding.catalogueAllProgressLayout.visibility = View.GONE
            }
        }
    }

    fun observePlaylist(){
        catalogueAllViewModel.listPlaylist.observe(viewLifecycleOwner) {
            playlistAdapter.playlistList.clear()
            playlistAdapter.playlistList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCatalogueAll.adapter = playlistAdapter
        }

        catalogueAllViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["playlistImage"] == true){
                binding.catalogueAllProgressLayout.visibility = View.GONE
            }
        }
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