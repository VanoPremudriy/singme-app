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
import com.example.singmeapp.viewmodels.GlobalSearchViewModel
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MyLibraryFragment : Fragment(), View.OnClickListener, MenuProvider {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentMyLibraryBinding
    var fragment = this

    lateinit var myLibraryViewModel: MyLibraryViewModel
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var globalSearchViewModel: GlobalSearchViewModel

    lateinit var trackAdapter: TrackAdapter

    lateinit var searchMyTrackAdapter: TrackAdapter
    lateinit var searchMyAlbumAdapter: AlbumAdapter
    lateinit var searchMyPlaylistAdapter: PlaylistAdapter
    lateinit var searchMyBandAdapter: BandAdapter
    lateinit var searchAllTrackAdapter: TrackAdapter
    lateinit var searchAllAlbumAdapter: AlbumAdapter
    lateinit var searchAllBandAdapter: BandAdapter
    lateinit var optionsMenu: Menu

    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        setHasOptionsMenu(true)

        val provider = ViewModelProvider(this)
        val playlistProvider = ViewModelProvider(fragmentActivity)
        val globalSearchProvider = ViewModelProvider(this)

        myLibraryViewModel = provider[MyLibraryViewModel::class.java]
        playerPlaylistViewModel = playlistProvider[PlayerPlaylistViewModel::class.java]
        globalSearchViewModel = globalSearchProvider[GlobalSearchViewModel::class.java]


        myLibraryViewModel.getTracks()



        searchMyTrackAdapter = TrackAdapter(fragmentActivity, this)
        searchMyAlbumAdapter = AlbumAdapter(this)
        searchMyPlaylistAdapter = PlaylistAdapter(this)
        searchMyBandAdapter = BandAdapter(this)

        searchAllTrackAdapter = TrackAdapter(fragmentActivity, this)
        searchAllAlbumAdapter = AlbumAdapter(this)
        searchAllBandAdapter = BandAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)

        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentActivity.title = getString(R.string.library)

        binding = FragmentMyLibraryBinding.inflate(layoutInflater)

        buttonSets()

        binding.rcView.layoutManager = LinearLayoutManager(activity)

        binding.rvMyMusicInGSInLibrary.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvMyAlbumsInGSInLibrary.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyPlaylistsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyBandsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAllTracksInGSInLibrary.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvAllAlbumsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAllBandsInGSInLibrary.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        observes()


        return binding.root
    }

    fun observes(){
        playerPlaylistViewModel.isListTrackChanged.observe(viewLifecycleOwner){
            binding.myLibraryProgressLayout.visibility = View.VISIBLE
            myLibraryViewModel.isAlready.value?.put("track", false)
            myLibraryViewModel.isAlready.value?.put("image", false)
            trackAdapter = TrackAdapter(fragmentActivity, this)
            myLibraryViewModel.getTracks()
        }


        myLibraryViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.initList(ArrayList(it.values))
            binding.rcView.adapter = trackAdapter
        }

        globalSearchViewModel.listMyTrack.observe(viewLifecycleOwner){
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

        globalSearchViewModel.listMyAlbum.observe(viewLifecycleOwner){
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

        globalSearchViewModel.listMyPlaylist.observe(viewLifecycleOwner){
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

        globalSearchViewModel.listMyBand.observe(viewLifecycleOwner){
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

        globalSearchViewModel.listAllTrack.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllTracksInGSInLibrary.visibility = View.GONE
            } else {
                binding.llAllTracksInGSInLibrary.visibility = View.VISIBLE
            }
            val tracks = ArrayList<Track>(it.values)
            searchAllTrackAdapter.trackList.clear()
            searchAllTrackAdapter.trackList.addAll(tracks)
            binding.rvAllTracksInGSInLibrary.adapter = searchAllTrackAdapter
        }

        globalSearchViewModel.listAllAlbum.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllAlbumsInGSInLibrary.visibility = View.GONE
            } else {
                binding.llAllAlbumsInGSInLibrary.visibility = View.VISIBLE
            }
            val albums = ArrayList<Album>(it.values)
            searchAllAlbumAdapter.albumList.clear()
            searchAllAlbumAdapter.albumList.addAll(albums)
            binding.rvAllAlbumsInGSInLibrary.adapter = searchAllAlbumAdapter
        }

        globalSearchViewModel.listAllBand.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllBandsInGSInLibrary.visibility = View.GONE
            } else {
                binding.llAllBandsInGSInLibrary.visibility = View.VISIBLE
            }
            val bands = ArrayList<Band>(it.values)
            searchAllBandAdapter.bandList.clear()
            searchAllBandAdapter.bandList.addAll(bands)
            binding.rvAllBandsInGSInLibrary.adapter = searchAllBandAdapter
        }



        myLibraryViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["track"] == true && it["image"] == true){
                binding.myLibraryProgressLayout.visibility = View.GONE
            }
        }

        globalSearchViewModel.isAlreadySearch.observe(viewLifecycleOwner){
            if (it["myTrack"] == true
                && it["myTrackImage"] == true
                && it["myAlbumImage"] == true
                && it["myPlaylistImage"] == true
                && it["myBandImage"] == true
                && it["myBandBack"] == true
                && it["allTrackImage"] == true
                && it["allTrack"] == true
                && it["allAlbumImage"] == true
                && it["allBandImage"] == true
                && it["allBandBack"] == true){
                binding.myLibraryProgressLayout.visibility = View.GONE
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = MyLibraryFragment()
    }

    fun doWhenSort(){
        binding.rcView.adapter = trackAdapter
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(p: View?) {
        when(p?.id) {
            binding.idBands.id -> {
                val bundle = Bundle()
                bundle.putInt("Back", R.id.myLibraryFragment)
                findNavController().navigate(R.id.loveBandsFragment, bundle)
            }
            binding.idPlaylists.id -> {
                findNavController().navigate(R.id.myPlaylistsFragment)
            }
            binding.idAlbums.id -> {
                val bundle = Bundle()
                bundle.putInt("Back", R.id.myLibraryFragment)
                findNavController().navigate(R.id.loveAlbumsFragment, bundle)
            }
            binding.tvSortByInLibrary.id -> {
                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.tvSortByAlbum.visibility = View.VISIBLE
                mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
            mainActivity.binding.tvSortByDefault.id -> {
                trackAdapter.sortByDefault()
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                trackAdapter.sortByName()
                doWhenSort()
            }
            mainActivity.binding.tvSortByBand.id -> {
                trackAdapter.sortByBand()
                doWhenSort()
            }
            mainActivity.binding.tvSortByAlbum.id -> {
                trackAdapter.sortByAlbum()
                doWhenSort()
            }
            mainActivity.binding.tvSortByDate.id ->{
                trackAdapter.sortByDate()
                doWhenSort()
            }
        }
    }

    fun buttonSets(){
        binding.apply {
            idBands.setOnClickListener(this@MyLibraryFragment)
            idPlaylists.setOnClickListener(this@MyLibraryFragment)
            idAlbums.setOnClickListener(this@MyLibraryFragment)
            tvSortByInLibrary.setOnClickListener(this@MyLibraryFragment)
        }
        mainActivity.binding.apply {
            tvSortByAlbum.setOnClickListener(this@MyLibraryFragment)
            tvSortByBand.setOnClickListener(this@MyLibraryFragment)
            tvSortByDate.setOnClickListener(this@MyLibraryFragment)
            tvSortByName.setOnClickListener(this@MyLibraryFragment)
            tvSortByDefault.setOnClickListener(this@MyLibraryFragment)
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
                        binding.libraryGSLayout.visibility = View.VISIBLE
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                        binding.myLibraryProgressLayout.visibility = View.VISIBLE
                        binding.libraryGSLayout.visibility = View.GONE
                        trackAdapter = TrackAdapter(fragmentActivity, fragment)
                        myLibraryViewModel.getTracks()
                        (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                        return true
                    }

                })

                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.type_here_to_search)

                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        binding.tvAllMyMusicInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "myTracks")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllMyAlbumsInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "myAlbums")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllMyBandsInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "myBands")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllMyPlaylistsInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "myPlaylists")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllAllTracksInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "allTracks")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllAllAlbumsInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "allAlbums")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }
                        binding.tvAllAllBandsInGSInLibrary.setOnClickListener{
                            val bundle = Bundle()
                            bundle.putString("whatIs", "allBands")
                            bundle.putString("search", newText)
                            findNavController().navigate(R.id.catalogueAllFragment, bundle)
                        }

                        binding.myLibraryProgressLayout.visibility = View.VISIBLE
                        globalSearchViewModel.isAlreadySearch.value?.forEach {
                            globalSearchViewModel.isAlreadySearch.value?.put(it.key, false)
                        }
                        globalSearchViewModel.getContent(newText ?: "")

                        return false
                    }

                })

                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }

}