package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.databinding.FragmentLoveBandsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.viewmodels.LoveAlbumsViewModel
import com.example.singmeapp.viewmodels.LoveBandsViewModel


class LoveBandsFragment : Fragment() {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentLoveBandsBinding
    lateinit var loveBandsViewModel: LoveBandsViewModel
    lateinit var bandAdapter: BandAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        fragmentActivity.title = getString(R.string.Bands)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoveBandsBinding.inflate(layoutInflater)
        val provider = ViewModelProvider(this)
        loveBandsViewModel= provider[LoveBandsViewModel::class.java]
        loveBandsViewModel.getBands()
        binding.rcView.layoutManager = GridLayoutManager(context, 3)
        bandAdapter = BandAdapter(this)
        loveBandsViewModel.listBand.observe(viewLifecycleOwner){
            bandAdapter.bandList = it as ArrayList<Band> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcView.adapter = bandAdapter
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
        fun newInstance() = LoveBandsFragment()
    }
}