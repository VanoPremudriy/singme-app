package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.adapters.PlaylistAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentMyLibraryBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList


class MyLibraryFragment : Fragment(), View.OnClickListener, MenuProvider {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentMyLibraryBinding

    lateinit var myLibraryViewModel: MyLibraryViewModel
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel

    lateinit var trackAdapter: TrackAdapter

    lateinit var searchMyTrackAdapter: TrackAdapter
    lateinit var searchMyAlbumAdapter: AlbumAdapter
    lateinit var searchMyPlaylistAdapter: PlaylistAdapter
    lateinit var searchMyBandAdapter: BandAdapter

    lateinit var optionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        setHasOptionsMenu(true)
        val provider = ViewModelProvider(this)
        val playlistProvider = ViewModelProvider(fragmentActivity)
        myLibraryViewModel = provider[MyLibraryViewModel::class.java]
        playerPlaylistViewModel = playlistProvider[PlayerPlaylistViewModel::class.java]
        Log.e("LifeCycle", "onCreate")
        myLibraryViewModel.getTracks()

        trackAdapter = TrackAdapter(fragmentActivity, this)
        searchMyTrackAdapter = TrackAdapter(fragmentActivity, this)
        searchMyAlbumAdapter = AlbumAdapter(this)
        searchMyPlaylistAdapter = PlaylistAdapter(this)
        searchMyBandAdapter = BandAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentActivity.title = getString(R.string.library)

        Log.e("LifeCycle", "onCreateView")
        binding = FragmentMyLibraryBinding.inflate(layoutInflater)

        buttonSets()
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.rcView.layoutManager = linearLayoutManager

        binding.rvMyMusicInGSInLibrary.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvMyAlbumsInGSInLibrary.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyPlaylistsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyBandsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        playerPlaylistViewModel.isListTrackChanged.observe(viewLifecycleOwner){
            binding.myLibraryProgressLayout.visibility = View.VISIBLE
            myLibraryViewModel.isAlready.value?.put("track", false)
            myLibraryViewModel.isAlready.value?.put("image", false)
            trackAdapter = TrackAdapter(fragmentActivity, this)
            myLibraryViewModel.getTracks()
        }


        myLibraryViewModel.listTrack.observe(viewLifecycleOwner){
            val tracks = ArrayList<Track>(it.values)
            //tracks.sortBy { track -> track.date }
            tracks.reverse()
            trackAdapter.trackList.clear()
            trackAdapter.trackList.addAll(tracks)
            binding.rcView.adapter = trackAdapter
        }

        myLibraryViewModel.listMyTrack.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyMusicInGSInLibrary.visibility = View.GONE
            }
            else {
                binding.llMyMusicInGSInLibrary.visibility = View.VISIBLE
            }
            val tracks = ArrayList<Track>(it.values)
            searchMyTrackAdapter.trackList.clear()
            searchMyTrackAdapter.trackList.addAll(tracks)
            binding.rvMyMusicInGSInLibrary.adapter = searchMyTrackAdapter
        }

        myLibraryViewModel.listMyAlbum.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyAlbumsInGSInLibrary.visibility = View.GONE
            } else {
                binding.llMyAlbumsInGSInLibrary.visibility = View.VISIBLE
            }
            val albums = ArrayList<Album>(it.values)
            searchMyAlbumAdapter.albumList.clear()
            searchMyAlbumAdapter.albumList.addAll(albums)
            binding.rvMyAlbumsInGSInLibrary.adapter = searchMyAlbumAdapter
        }

        myLibraryViewModel.listMyPlaylist.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyPlaylistsInGSInLibrary.visibility = View.GONE
            } else {
                binding.llMyPlaylistsInGSInLibrary.visibility = View.VISIBLE
            }
            val playlists = ArrayList<Album>(it.values)
            searchMyPlaylistAdapter.playlistList.clear()
            searchMyPlaylistAdapter.playlistList.addAll(playlists)
            binding.rvMyPlaylistsInGSInLibrary.adapter = searchMyPlaylistAdapter
        }

        myLibraryViewModel.listMyBand.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyBandsInGSInLibrary.visibility = View.GONE
            } else {
                binding.llMyBandsInGSInLibrary.visibility = View.VISIBLE
            }
            val bands = ArrayList<Band>(it.values)
            searchMyBandAdapter.bandList.clear()
            searchMyBandAdapter.bandList.addAll(bands)
            binding.rvMyBandsInGSInLibrary.adapter = searchMyBandAdapter
        }



        myLibraryViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["track"] == true && it["image"] == true){
                binding.myLibraryProgressLayout.visibility = View.GONE
            }
        }

        myLibraryViewModel.isAlreadySearch.observe(viewLifecycleOwner){
            if (it["myTrack"] == true
                && it["myTrackImage"] == true
                && it["myAlbumImage"] == true
                && it["myPlaylistImage"] == true
                && it["myBandImage"] == true
                && it["myBandBack"] == true){
                binding.myLibraryProgressLayout.visibility = View.GONE
            }
        }
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = MyLibraryFragment()
    }

    override fun onClick(p: View?) {
        when(p?.id) {
            binding.idBands.id -> {
                val bundle = Bundle()
                bundle.putInt("Back", R.id.myLibraryFragment)
                findNavController().navigate(R.id.loveBandsFragment, bundle)
            }
            binding.idPlaylists.id -> {
                //Snackbar.make(p,"Playlist",Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.myPlaylistsFragment)
            }
            binding.idAlbums.id -> {
                val bundle = Bundle()
                bundle.putInt("Back", R.id.myLibraryFragment)
                findNavController().navigate(R.id.loveAlbumsFragment, bundle)
            }
        }
    }

    fun buttonSets(){
        binding.apply {
            idBands.setOnClickListener(this@MyLibraryFragment)
            idPlaylists.setOnClickListener(this@MyLibraryFragment)
            idAlbums.setOnClickListener(this@MyLibraryFragment)
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            R.id.action_search_user -> {

                menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
                    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                        Log.e("Search", "onMenuItemActionExpand")
                        binding.libraryGSLayout.visibility = View.VISIBLE
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                        binding.libraryGSLayout.visibility = View.GONE
                        (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                        return true
                    }

                })

                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = "Type here to search"

                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.e("Search", "onQueryTextSubmit")
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        binding.myLibraryProgressLayout.visibility = View.VISIBLE
                        myLibraryViewModel.isAlreadySearch.value?.forEach {
                            myLibraryViewModel.isAlready.value?.put(it.key, false)
                        }
                        myLibraryViewModel.getMyTracks(newText ?: "")
                        myLibraryViewModel.getMyAlbums(newText ?: "")
                        myLibraryViewModel.getMyPlaylists(newText ?: "")
                        myLibraryViewModel.getMyBands(newText ?: "")
                        return false
                    }

                })

                return true
            }
        }
        return false
    }

}