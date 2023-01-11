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
import com.example.singmeapp.MainActivity
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentMyLibraryBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar


class MyLibraryFragment : Fragment(), View.OnClickListener {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentMyLibraryBinding
    lateinit var myLibraryViewModel: MyLibraryViewModel
    lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("LifeCycle", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("LifeCycle", "onCreateView")
        binding = FragmentMyLibraryBinding.inflate(layoutInflater)
        fragmentActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(this)
        myLibraryViewModel = provider[MyLibraryViewModel::class.java]
        myLibraryViewModel.getTracks()
        buttonSets()
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        trackAdapter = TrackAdapter(fragmentActivity, myLibraryViewModel.url)
        myLibraryViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track>
            binding.rcView.adapter = trackAdapter
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
                Snackbar.make(p,"Bands",Snackbar.LENGTH_SHORT).show()
            }
            binding.idPlaylists.id -> {
                Snackbar.make(p,"Playlist",Snackbar.LENGTH_SHORT).show()
            }
            binding.button.id -> {
                (activity as MainActivity).bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    fun buttonSets(){
        binding.apply {
            idBands.setOnClickListener(this@MyLibraryFragment)
            idPlaylists.setOnClickListener(this@MyLibraryFragment)
            button.setOnClickListener(this@MyLibraryFragment)
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