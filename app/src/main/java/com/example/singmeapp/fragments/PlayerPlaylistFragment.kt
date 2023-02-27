package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentPlayerPlaylistBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel

class PlayerPlaylistFragment : Fragment() {
    lateinit var fragActivity: AppCompatActivity
    lateinit var binding: FragmentPlayerPlaylistBinding
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var trackAdapter:TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(fragActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]
        trackAdapter = TrackAdapter(fragActivity, this@PlayerPlaylistFragment)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("PlaylistFrag", this.id.toString())

        binding = FragmentPlayerPlaylistBinding.inflate(layoutInflater)
        binding.rcView.layoutManager = LinearLayoutManager(activity)


        playerPlaylistViewModel.trackList.observe(viewLifecycleOwner){
            Log.e("PlaylistFrag", "change")
            trackAdapter.trackList.clear()
            trackAdapter.trackList.addAll(it as ArrayList<Track>)
            binding.rcView.adapter = trackAdapter
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlayerPlaylistFragment()
    }


}