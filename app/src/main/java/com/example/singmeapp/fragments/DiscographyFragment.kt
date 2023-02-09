package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentDiscographyBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.DiscographyViewModel


class DiscographyFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentDiscographyBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var trackAdapter: TrackAdapter
    lateinit var albumAdapter: AlbumAdapter
    lateinit var singleAdapter: AlbumAdapter
    lateinit var discographyViewModel: DiscographyViewModel
    lateinit var band: Band
    lateinit var optionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity =  activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)

        band = arguments?.getSerializable("band") as Band
        val provider = ViewModelProvider(this)
        discographyViewModel = provider[DiscographyViewModel::class.java]
        discographyViewModel.getTracks(band)
        discographyViewModel.getAlbums(band)
        discographyViewModel.getMembers(band)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = band.name

        binding = FragmentDiscographyBinding.inflate(layoutInflater)

        binding.rcViewDiscographyTopTracks.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
        binding.rcViewDiscographyAlbums.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcViewDiscographySingles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        trackAdapter = TrackAdapter(fragmentActivity, this)
        albumAdapter = AlbumAdapter(this)
        singleAdapter = AlbumAdapter(this)


        discographyViewModel.listMemberUuid.observe(viewLifecycleOwner){
            it.forEach { it1 ->
                if (it1 == discographyViewModel.auth.currentUser?.uid.toString()){
                    fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
                }
            }
        }

        discographyViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rcViewDiscographyTopTracks.adapter = trackAdapter
        }

        discographyViewModel.listAlbum.observe(viewLifecycleOwner){
            albumAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcViewDiscographyAlbums.adapter = albumAdapter
        }

        discographyViewModel.listSingle.observe(viewLifecycleOwner){
            singleAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcViewDiscographySingles.adapter = singleAdapter
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

        if (item.itemId == R.id.add_album){
            val bundle = Bundle()
            bundle.putSerializable("currentBand", band)
            findNavController().navigate(R.id.createAlbumFragment, bundle)
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = DiscographyFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.discography_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}