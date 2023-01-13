package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.databinding.FragmentLoveAlbumsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.LoveAlbumsViewModel
import com.example.singmeapp.viewmodels.MyLibraryViewModel


class LoveAlbumsFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentLoveAlbumsBinding
    lateinit var loveAlbumsViewModel: LoveAlbumsViewModel
    lateinit var albumAdapter: AlbumAdapter
    lateinit var fragmentActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        fragmentActivity.title = getString(R.string.albums)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoveAlbumsBinding.inflate(layoutInflater)
        val provider = ViewModelProvider(this)
        loveAlbumsViewModel = provider[LoveAlbumsViewModel::class.java]
        loveAlbumsViewModel.getAlbums()
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        albumAdapter = AlbumAdapter(this)
        loveAlbumsViewModel.listAlbum.observe(viewLifecycleOwner){
            albumAdapter.albumList = it as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcView.adapter = albumAdapter
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
           findNavController().navigate(R.id.myLibraryFragment)
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoveAlbumsFragment()
    }

    override fun onClick(p0: View?) {

    }
}