package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
    //lateinit var album: Album
    lateinit var albumUuid: String

    fun convert(value: Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentActivity =  activity as AppCompatActivity
        fragmentActivity.supportActionBar?.hide()

        //album = arguments?.getSerializable("album") as Album
        albumUuid = arguments?.getString("albumUuid").toString()

        val provider = ViewModelProvider(this)
        albumViewModel = provider[AlbumViewModel::class.java]
        albumViewModel.getAlbumData(albumUuid)
        albumViewModel.getTracks(albumUuid)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(layoutInflater)
        buttonSets()

       /* binding.tvAlbumName.text = album.name
        binding.tvAlbumBandName.text = album.band
        if (album.imageUrl != "")
        Picasso.get().load(album.imageUrl).fit().into(binding.ivAlbumCover)*/

        binding.rcView.layoutManager = LinearLayoutManager(activity)
        trackAdapter = TrackAdapter(fragmentActivity, this)
        albumViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track>
            binding.rcView.adapter = trackAdapter
        }

        albumViewModel.currentAlbum.observe(viewLifecycleOwner){
            binding.tvAlbumName.text = it.name
            binding.tvAlbumBandName.text = it.band
            if (it.imageUrl != "")
                Picasso.get().load(it.imageUrl).fit().into(binding.ivAlbumCover)
            binding.textView9.text = it.name
        }

       // binding.textView9.text = album.name
        binding.albumScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.albumScrollView.scrollY > 150){
                binding.llTopMenu.visibility = View.VISIBLE
            } else {
                binding.llTopMenu.visibility = View.GONE
            }
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
                //(activity as AppCompatActivity).supportActionBar?.show()
                //arguments?.let { findNavController().navigate(it.getInt("Back")) }

                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

                if (count == 0) {
                    (activity as AppCompatActivity).supportActionBar?.show()
                    activity?.onBackPressed()

                } else {
                    (activity as AppCompatActivity).supportActionBar?.show()
                    findNavController().popBackStack()
                }
            }

            binding.ibBack2.id ->{
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

                if (count == 0) {
                    (activity as AppCompatActivity).supportActionBar?.show()
                    activity?.onBackPressed()

                } else {
                    (activity as AppCompatActivity).supportActionBar?.show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            Log.e("Back", "home")
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                (activity as AppCompatActivity).supportActionBar?.show()
                activity?.onBackPressed()
            } else {
                (activity as AppCompatActivity).supportActionBar?.show()
                findNavController().popBackStack()
            }
        }
        return true
    }

    fun buttonSets(){
        binding.apply {
           ibBack.setOnClickListener(this@AlbumFragment)
            ibBack2.setOnClickListener(this@AlbumFragment)
        }
    }






    companion object {

        @JvmStatic
        fun newInstance() = AlbumFragment()

    }
}