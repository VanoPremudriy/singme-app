package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentMyLibraryBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList


class MyLibraryFragment : Fragment(), View.OnClickListener {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentMyLibraryBinding

    lateinit var myLibraryViewModel: MyLibraryViewModel
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel

    lateinit var trackAdapter: TrackAdapter

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragmentActivity.title = getString(R.string.albums)

        Log.e("LifeCycle", "onCreateView")
        binding = FragmentMyLibraryBinding.inflate(layoutInflater)

        buttonSets()
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.rcView.layoutManager = linearLayoutManager


        playerPlaylistViewModel.isListTrackChanged.observe(viewLifecycleOwner){
            binding.myLibraryProgressLayout.visibility = View.VISIBLE
            myLibraryViewModel.isAlready.value?.put("track", false)
            myLibraryViewModel.isAlready.value?.put("image", false)
            trackAdapter = TrackAdapter(fragmentActivity, this)
            myLibraryViewModel.getTracks()
        }


        myLibraryViewModel.listTrack.observe(viewLifecycleOwner){
            Log.e("MyLibrary", "change")
            trackAdapter.trackList.clear()
            trackAdapter.trackList.addAll(it as ArrayList<Track>)
            binding.rcView.adapter = trackAdapter
        }

        myLibraryViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["track"] == true && it["image"] == true){
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

    override fun onResume() {
        super.onResume()
        Log.e("LifeCycle", "OnResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("LifeCycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("LifeCycle", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("LifeCycle", "onDestroy")

    }

}