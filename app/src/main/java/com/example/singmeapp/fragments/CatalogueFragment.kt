package com.example.singmeapp.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.*
import com.example.singmeapp.databinding.FragmentCatalogueBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator

class CatalogueFragment : Fragment(), MenuProvider {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueBinding
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter
    lateinit var optionsMenu: Menu
    lateinit var catalogueViewModel: CatalogueViewModel
    lateinit var globalSearchViewModel: GlobalSearchViewModel

    lateinit var searchMyTrackAdapter: TrackAdapter
    lateinit var searchMyAlbumAdapter: AlbumAdapter
    lateinit var searchMyPlaylistAdapter: PlaylistAdapter
    lateinit var searchMyBandAdapter: BandAdapter
    lateinit var searchAllTrackAdapter: TrackAdapter
    lateinit var searchAllAlbumAdapter: AlbumAdapter
    lateinit var searchAllBandAdapter: BandAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)

        val provider = ViewModelProvider(this)
        val globalSearchProvider = ViewModelProvider(this)

        catalogueViewModel = provider[CatalogueViewModel::class.java]
        globalSearchViewModel = globalSearchProvider[GlobalSearchViewModel::class.java]

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
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentActivity.title = getString(R.string.catalogue)
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)


        binding = FragmentCatalogueBinding.inflate(layoutInflater)

        binding.catalogueProgressLayout.visibility = View.GONE

        binding.rvMyMusicInGSInCatalogue.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvMyAlbumsInGSInCatalogue.layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyPlaylistsInGSInCatalogue.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyBandsInGSInCatalogue.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAllTracksInGSInCatalogue.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvAllAlbumsInGSInCatalogue.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAllBandsInGSInCatalogue.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        observes()

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

    fun observes(){

        globalSearchViewModel.listMyTrack.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyMusicInGSInCatalogue.visibility = View.GONE
            }
            else {
                binding.llMyMusicInGSInCatalogue.visibility = View.VISIBLE
            }
            val tracks = java.util.ArrayList<Track>(it.values)
            searchMyTrackAdapter.trackList.clear()
            searchMyTrackAdapter.trackList.addAll(tracks)
            binding.rvMyMusicInGSInCatalogue.adapter = searchMyTrackAdapter
        }

        globalSearchViewModel.listMyAlbum.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyAlbumsInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llMyAlbumsInGSInCatalogue.visibility = View.VISIBLE
            }
            val albums = java.util.ArrayList<Album>(it.values)
            searchMyAlbumAdapter.albumList.clear()
            searchMyAlbumAdapter.albumList.addAll(albums)
            binding.rvMyAlbumsInGSInCatalogue.adapter = searchMyAlbumAdapter
        }

        globalSearchViewModel.listMyPlaylist.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyPlaylistsInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llMyPlaylistsInGSInCatalogue.visibility = View.VISIBLE
            }
            val playlists = java.util.ArrayList<Album>(it.values)
            searchMyPlaylistAdapter.playlistList.clear()
            searchMyPlaylistAdapter.playlistList.addAll(playlists)
            binding.rvMyPlaylistsInGSInCatalogue.adapter = searchMyPlaylistAdapter
        }

        globalSearchViewModel.listMyBand.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llMyBandsInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llMyBandsInGSInCatalogue.visibility = View.VISIBLE
            }
            val bands = java.util.ArrayList<Band>(it.values)
            searchMyBandAdapter.bandList.clear()
            searchMyBandAdapter.bandList.addAll(bands)
            binding.rvMyBandsInGSInCatalogue.adapter = searchMyBandAdapter
        }

        globalSearchViewModel.listAllTrack.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllTracksInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llAllTracksInGSInCatalogue.visibility = View.VISIBLE
            }
            val tracks = java.util.ArrayList<Track>(it.values)
            searchAllTrackAdapter.trackList.clear()
            searchAllTrackAdapter.trackList.addAll(tracks)
            binding.rvAllTracksInGSInCatalogue.adapter = searchAllTrackAdapter
        }

        globalSearchViewModel.listAllAlbum.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllAlbumsInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llAllAlbumsInGSInCatalogue.visibility = View.VISIBLE
            }
            val albums = java.util.ArrayList<Album>(it.values)
            searchAllAlbumAdapter.albumList.clear()
            searchAllAlbumAdapter.albumList.addAll(albums)
            binding.rvAllAlbumsInGSInCatalogue.adapter = searchAllAlbumAdapter
        }

        globalSearchViewModel.listAllBand.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.llAllBandsInGSInCatalogue.visibility = View.GONE
            } else {
                binding.llAllBandsInGSInCatalogue.visibility = View.VISIBLE
            }
            val bands = java.util.ArrayList<Band>(it.values)
            searchAllBandAdapter.bandList.clear()
            searchAllBandAdapter.bandList.addAll(bands)
            binding.rvAllBandsInGSInCatalogue.adapter = searchAllBandAdapter
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
                binding.catalogueProgressLayout.visibility = View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CatalogueFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
            if (count == 0) {
                (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                activity?.onBackPressed()
            } else {
                (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().popBackStack()
            }
        }

        if (item.itemId == R.id.action_search_user){
            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    binding.catalogueGSLayout.visibility = View.VISIBLE
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    binding.catalogueGSLayout.visibility = View.GONE
                    (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    return true
                }

            })

            val searcView = item.actionView as SearchView
            searcView.queryHint = "Type here to search"

            searcView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    binding.tvAllMyMusicInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "myTracks")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllMyAlbumsInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "myAlbums")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllMyBandsInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "myBands")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllMyPlaylistsInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "myPlaylists")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllAllTracksInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "allTracks")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllAllAlbumsInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "allAlbums")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.tvAllAllBandsInGSInCatalogue.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "allBands")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }

                    binding.catalogueProgressLayout.visibility = View.VISIBLE
                    globalSearchViewModel.isAlreadySearch.value?.forEach {
                        globalSearchViewModel.isAlreadySearch.value?.put(it.key, false)
                    }
                    globalSearchViewModel.getContent(newText ?: "")
                    return true
                }

            })

        }
        return true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }

}