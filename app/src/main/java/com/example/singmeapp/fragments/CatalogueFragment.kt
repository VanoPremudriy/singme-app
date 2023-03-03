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
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.adapters.CataloguePagerAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentCatalogueBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.CatalogueNewsViewModel
import com.example.singmeapp.viewmodels.CatalogueViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator

class CatalogueFragment : Fragment(), MenuProvider {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentCatalogueBinding
    lateinit var newTrackAdapter: TrackAdapter
    lateinit var newAlbumAdapter: AlbumAdapter
    lateinit var optionsMenu: Menu
    lateinit var catalogueViewModel: CatalogueViewModel
    lateinit var trackAdapter: TrackAdapter
    lateinit var albumAdapter: AlbumAdapter
    lateinit var bandAdapter: BandAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)
        trackAdapter = TrackAdapter(fragmentActivity, this)
        val provider = ViewModelProvider(this)
        catalogueViewModel = provider[CatalogueViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentActivity.title = getString(R.string.catalogue)
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)


        binding = FragmentCatalogueBinding.inflate(layoutInflater)

        binding.rvCatalogueSearchTracks.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rvCatalogueSearchAlbums.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCatalogueSearchBands.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        catalogueViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter = TrackAdapter(fragmentActivity, this)
            trackAdapter.trackList.clear()
            val tracks = ArrayList<Track>(it.values)
            tracks.sortBy { track -> track.date }
            tracks.reverse()
            trackAdapter.trackList.addAll(it.values)
            binding.rvCatalogueSearchTracks.adapter = trackAdapter
        }

        catalogueViewModel.listAlbum.observe(viewLifecycleOwner){
            albumAdapter = AlbumAdapter(this)
            albumAdapter.albumList.clear()
            val albums = ArrayList<Album>(it.values)
            albums.sortBy { album -> album.date }
            albums.reverse()
            albumAdapter.albumList.addAll(albums)
            binding.rvCatalogueSearchAlbums.adapter = albumAdapter
        }

        catalogueViewModel.listBand.observe(viewLifecycleOwner){
            bandAdapter = BandAdapter(this)
            bandAdapter.bandList.clear()
            val bands = ArrayList<Band>(it.values)
            bandAdapter.bandList.addAll(bands)
            binding.rvCatalogueSearchBands.adapter = bandAdapter
        }

        catalogueViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["trackImage"] == true
                && it["track"] == true
                && it["albumImage"] == true
                && it["bandImage"] == true
                && it["bandBack"] ==  true){
                binding.catalogueProgressLayout.visibility = View.GONE
            }
        }

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
                    Log.e("Search", "onMenuItemActionExpand")
                    binding.catalogueSearchLayout.visibility = View.VISIBLE
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    binding.catalogueSearchLayout.visibility = View.GONE
                    (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    return true
                }

            })

            val searcView = item.actionView as SearchView
            searcView.queryHint = "Type here to search"

            searcView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.e("Search", "onQueryTextSubmit")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.tvAllSearchTracks.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putString("whatIs", "searchTracks")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }

                    binding.tvAllSearchAlbums.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("whatIs", "searchAlbums")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }

                    binding.tvAllSearchBands.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("whatIs", "searchBands")
                        bundle.putString("search", newText)
                        findNavController().navigate(R.id.catalogueAllFragment, bundle)
                    }
                    binding.catalogueProgressLayout.visibility = View.VISIBLE
                    catalogueViewModel.isAlready.value?.forEach {
                        catalogueViewModel.isAlready.value?.put(it.key, false)
                    }
                    Log.e("Search", "onQueryTextChange ${newText}")
                    catalogueViewModel.getTracks(newText ?: "")
                    catalogueViewModel.getAlbums(newText ?: "")
                    catalogueViewModel.getBands(newText ?: "")
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

}