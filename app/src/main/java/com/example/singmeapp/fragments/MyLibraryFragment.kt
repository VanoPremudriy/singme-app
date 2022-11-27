package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentMyLibraryBinding
import com.google.android.material.snackbar.Snackbar


class MyLibraryFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentMyLibraryBinding

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
        buttonSets()


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
        }
    }

    fun buttonSets(){
        binding.apply {
            idBands.setOnClickListener(this@MyLibraryFragment)
            idPlaylists.setOnClickListener(this@MyLibraryFragment)
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