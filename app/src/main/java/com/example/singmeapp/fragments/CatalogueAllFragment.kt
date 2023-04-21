package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.*
import com.example.singmeapp.databinding.FragmentCatalogueAllBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.CatalogueAllViewModel
import com.example.singmeapp.viewmodels.DiscographyAllViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class CatalogueAllFragment : Fragment(), MenuProvider, OnClickListener {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueAllBinding
    lateinit var tracksAdapter: TrackAdapter
    lateinit var albumsAdapter: AlbumAdapter
    lateinit var bandAdapter: BandAdapter
    lateinit var playlistAdapter: PlaylistAdapter
    lateinit var catalogueAllViewModel: CatalogueAllViewModel
    lateinit var whatIs: String
    lateinit var optionsMenu: Menu
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        fragmentActivity.supportActionBar?.show()

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
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        binding.rvCatalogueAll.layoutManager = LinearLayoutManager(context)

        observe()
        setButtons()

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
            tracksAdapter.initList(it) /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
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
            albumsAdapter.initList(it) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
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
            bandAdapter.initList(it)
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
            playlistAdapter.initList(it) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rvCatalogueAll.adapter = playlistAdapter
        }

        catalogueAllViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["playlistImage"] == true){
                binding.catalogueAllProgressLayout.visibility = View.GONE
            }
        }
    }

    fun doWhenSort(){
        initAdapter(returnAdapter())
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun setButtons(){
        mainActivity.binding.tvAddPlaylist.setOnClickListener(this@CatalogueAllFragment)
        mainActivity.binding.tvSortByDefault.setOnClickListener(this@CatalogueAllFragment)
        mainActivity.binding.tvSortByDate.setOnClickListener(this@CatalogueAllFragment)
        mainActivity.binding.tvSortByName.setOnClickListener(this@CatalogueAllFragment)
        mainActivity.binding.tvSortByBand.setOnClickListener(this@CatalogueAllFragment)
        mainActivity.binding.tvSortByAlbum.setOnClickListener(this@CatalogueAllFragment)
        binding.tvSortByInCatalogueAll.setOnClickListener(this@CatalogueAllFragment)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CatalogueAllFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            android.R.id.home -> {
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
                if (count == 0) {
                    activity?.onBackPressed()
                } else {
                    findNavController().popBackStack()
                }

                return true
            }

            R.id.action_search_user -> {
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.type_here_to_search)


                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        sortBySearch(returnAdapter(), newText ?: "")
                        ///albumAdapter.sortBySearch(newText ?: "")
                        initAdapter(returnAdapter())
                        return true
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

    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.tvSortByInCatalogueAll.id -> {
                if (whatIs.contains("Tracks")){
                    mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                    mainActivity.binding.tvSortByAlbum.visibility = View.VISIBLE
                } else if (whatIs.contains("Albums")){
                    mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                    mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                } else if (whatIs.contains("Bands")){
                    mainActivity.binding.tvSortByBand.visibility = View.GONE
                    mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                } else if (whatIs.contains("Playlists")){
                    mainActivity.binding.tvSortByBand.visibility = View.GONE
                    mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                }

                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
            mainActivity.binding.tvSortByDefault.id -> {
                sortByDefault(returnAdapter())
                doWhenSort()
            }
            mainActivity.binding.tvSortByDate.id -> {
                sortByDate(returnAdapter())
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                sortByName(returnAdapter())
                doWhenSort()
            }
            mainActivity.binding.tvSortByBand.id -> {
                sortByBand(returnAdapter())
                doWhenSort()
            }
            mainActivity.binding.tvSortByAlbum.id -> {
                sortByAlbum(returnAdapter())
                doWhenSort()
            }

        }
    }

    fun sortByDefault(adapter: SortInAdapter) = adapter.sortByDefault()

    fun sortByName(adapter: SortInAdapter) =adapter.sortByName()

    fun sortByDate(adapter: SortInAdapter) =  adapter.sortByDate()

    fun sortByAlbum(adapter: SortInAdapter) = adapter.sortByAlbum()

    fun sortByBand(adapter: SortInAdapter) = adapter.sortByBand()

    fun sortBySearch(adapter: SortInAdapter, search: String) = adapter.sortBySearch(search)

    fun returnAdapter(): SortInAdapter{
        if (whatIs.contains("Tracks")){
            return tracksAdapter
        } else if (whatIs.contains("Albums")){
           return albumsAdapter
        } else if (whatIs.contains("Bands")){
           return  bandAdapter
        } else if (whatIs.contains("Playlists")){
            return playlistAdapter
        }
        return tracksAdapter
    }

    fun initAdapter(sortInAdapter: SortInAdapter){
        if (whatIs.contains("Tracks")){
            binding.rvCatalogueAll.adapter = tracksAdapter
        } else if (whatIs.contains("Albums")){
            binding.rvCatalogueAll.adapter = albumsAdapter
        } else if (whatIs.contains("Bands")){
            binding.rvCatalogueAll.adapter = bandAdapter
        } else if (whatIs.contains("Playlists")){
            binding.rvCatalogueAll.adapter = playlistAdapter
        }
    }
}