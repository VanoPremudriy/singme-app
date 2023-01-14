package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentAlbumBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.squareup.picasso.Picasso

class AlbumFragment : Fragment(), View.OnClickListener {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentAlbumBinding
    lateinit var trackAdapter: TrackAdapter
    lateinit var albumViewModel: AlbumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity =  activity as AppCompatActivity
        fragmentActivity.supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.hide()
        binding = FragmentAlbumBinding.inflate(layoutInflater)
        buttonSets()
        val album = arguments?.getSerializable("album") as Album
        binding.tvAlbumName.text = album.name
        binding.tvAlbumBandName.text = album.band
        Picasso.get().load(album.imageUrl).fit().into(binding.ivAlbumCover)
        val provider = ViewModelProvider(this)
        albumViewModel = provider[AlbumViewModel::class.java]
        albumViewModel.getTracks(album)
        buttonSets()
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        trackAdapter = TrackAdapter(fragmentActivity)
        albumViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track>
            binding.rcView.adapter = trackAdapter
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fragmentActivity.supportActionBar?.hide()
    }
    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.ibBack.id -> {
                (activity as AppCompatActivity).supportActionBar?.show()
                arguments?.let { findNavController().navigate(it.getInt("Back")) }
            }
        }
    }

    fun buttonSets(){
        binding.apply {
           ibBack.setOnClickListener(this@AlbumFragment)
        }
    }




    companion object {

        @JvmStatic
        fun newInstance() = AlbumFragment()

    }
}