package com.example.singmeapp.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.SongAdapter
import com.example.singmeapp.databinding.FragmentPlayerPlaylistBinding
import com.example.singmeapp.items.Song
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel

class PlayerPlaylistFragment : Fragment() {
    lateinit var fragActivity: AppCompatActivity
    lateinit var binding: FragmentPlayerPlaylistBinding
    lateinit var playerPlaylistViewModel: PlayerPlaylistViewModel
    lateinit var songAdapter:SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity = activity as AppCompatActivity
        songAdapter = SongAdapter(fragActivity, "")
        binding = FragmentPlayerPlaylistBinding.inflate(layoutInflater)
        val provider = ViewModelProvider(fragActivity)
        playerPlaylistViewModel = provider[PlayerPlaylistViewModel::class.java]
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        playerPlaylistViewModel.songList.observe(viewLifecycleOwner){
            songAdapter.songList = it as ArrayList<Song>
            binding.rcView.adapter = songAdapter
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlayerPlaylistFragment()

    }


}