package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.ChooseTrackForPlaylistAdapter
import com.example.singmeapp.databinding.FragmentChooseTrackForPlaylistBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.ChooseTrackForPlaylistViewModel

class ChooseTrackForPlaylistFragment : Fragment() {

    lateinit var binding: FragmentChooseTrackForPlaylistBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var chooseTrackForPlaylistViewModel: ChooseTrackForPlaylistViewModel
    lateinit var chooseTrackForPlaylistAdapter: ChooseTrackForPlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fragmentActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(this)
        chooseTrackForPlaylistViewModel = provider[ChooseTrackForPlaylistViewModel::class.java]
        chooseTrackForPlaylistViewModel.getTracks()
        chooseTrackForPlaylistViewModel.playlistUuid.value = arguments?.getString("playlistUuid")
        chooseTrackForPlaylistAdapter = ChooseTrackForPlaylistAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentChooseTrackForPlaylistBinding.inflate(layoutInflater)

        binding.rcChooseTrackForPlaylist.layoutManager = LinearLayoutManager(context)

        chooseTrackForPlaylistViewModel.listTrack.observe(viewLifecycleOwner){
            chooseTrackForPlaylistAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rcChooseTrackForPlaylist.adapter = chooseTrackForPlaylistAdapter
        }

        chooseTrackForPlaylistViewModel.isExist.observe(viewLifecycleOwner){
            if (it){
                Log.e("track", "exist")
                Toast.makeText(context, "Этот трек уже есть в плейлисте", Toast.LENGTH_SHORT).show()
                chooseTrackForPlaylistViewModel.isExist.value = false
            }
            findNavController().popBackStack()
        }

        // Inflate the layout for this fragment
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
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = ChooseTrackForPlaylistFragment()
    }
}