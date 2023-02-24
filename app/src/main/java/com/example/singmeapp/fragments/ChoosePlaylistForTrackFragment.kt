package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.ChoosePlaylistForTrackAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentChoosePlaylistForTrackBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.ChoosePlaylistForTrackViewModel


class ChoosePlaylistForTrackFragment : Fragment() {

    lateinit var fragmentActivity: AppCompatActivity

    lateinit var binding: FragmentChoosePlaylistForTrackBinding
    lateinit var choosePlaylistForTrackAdapter: ChoosePlaylistForTrackAdapter
    lateinit var choosePlaylistForTrackViewModel: ChoosePlaylistForTrackViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fragmentActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(this)
        choosePlaylistForTrackViewModel = provider[ChoosePlaylistForTrackViewModel::class.java]
        choosePlaylistForTrackViewModel.trackUuid.value = arguments?.getString("trackUuid")
        choosePlaylistForTrackViewModel.getPlaylists()
        choosePlaylistForTrackAdapter = ChoosePlaylistForTrackAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentChoosePlaylistForTrackBinding.inflate(layoutInflater)
        binding.rcChoosePlaylistForTrack.layoutManager = LinearLayoutManager(context)

        choosePlaylistForTrackViewModel.listPlaylists.observe(viewLifecycleOwner){
            choosePlaylistForTrackAdapter.playlistList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcChoosePlaylistForTrack.adapter = choosePlaylistForTrackAdapter
        }

        choosePlaylistForTrackViewModel.isExist.observe(viewLifecycleOwner){
            if (it){
                Toast.makeText(context, "Этот трек уже есть в плейлисте", Toast.LENGTH_SHORT).show()
                choosePlaylistForTrackViewModel.isExist.value = false
            }
            findNavController().popBackStack()
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
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoosePlaylistForTrackFragment()
    }
}