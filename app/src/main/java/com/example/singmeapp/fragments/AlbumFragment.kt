package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentAlbumBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso

class AlbumFragment : Fragment(), View.OnClickListener {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentAlbumBinding
    lateinit var trackAdapter: TrackAdapter
    lateinit var albumViewModel: AlbumViewModel
    //lateinit var album: Album
    lateinit var albumUuid: String
    lateinit var bandUuid: String
    var isAuthor = false
    var isInLove = false

    fun convert(value: Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentActivity =  activity as AppCompatActivity
        mainActivity = activity as MainActivity
        fragmentActivity.supportActionBar?.hide()

        //album = arguments?.getSerializable("album") as Album
        albumUuid = arguments?.getString("albumUuid").toString()

        val provider = ViewModelProvider(this)
        albumViewModel = provider[AlbumViewModel::class.java]
        albumViewModel.getAlbumData(albumUuid)
        albumViewModel.getTracks(albumUuid)
        albumViewModel.getAuthors(albumUuid)

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

        albumViewModel.bandUuid.observe(viewLifecycleOwner){
            bandUuid = it
        }

        albumViewModel.isAuthor.observe(viewLifecycleOwner){
            isAuthor = it
        }

        albumViewModel.isLove.observe(viewLifecycleOwner){
            isInLove = it
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
                    fragmentActivity.supportActionBar?.show()
                    activity?.onBackPressed()

                } else {
                    fragmentActivity.supportActionBar?.show()
                    findNavController().popBackStack()
                }
            }

            binding.ibAlbumMenu.id -> {
                if (isAuthor){
                    mainActivity.binding.tvDeleteAlbum.visibility = View.VISIBLE
                } else {
                    mainActivity.binding.tvDeleteAlbum.visibility = View.GONE
                }

                if (isInLove){
                    mainActivity.binding.tvDeleteAlbumFromLove.visibility = View.VISIBLE
                    mainActivity.binding.tvAddAlbumInLove.visibility = View.GONE
                } else {
                    mainActivity.binding.tvDeleteAlbumFromLove.visibility = View.GONE
                    mainActivity.binding.tvAddAlbumInLove.visibility = View.VISIBLE
                }

                mainActivity.binding.inAlbumMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }

            mainActivity.binding.tvAddAlbumInLove.id -> {
                albumViewModel.addAlbumInLove(albumUuid)
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }


            mainActivity.binding.tvDeleteAlbumFromLove.id -> {
                albumViewModel.deleteAlbumFromLove(albumUuid)
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }

            mainActivity.binding.tvGoToBandProfileFromAlbum.id -> {
                val bundle = Bundle()
                bundle.putString("bandUuid", bandUuid)
                fragmentActivity.supportActionBar?.show()
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().navigate(R.id.bandFragment, bundle)
            }

            mainActivity.binding.tvDeleteAlbum.id -> {
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

                if (count == 0) {
                    fragmentActivity.supportActionBar?.show()
                    activity?.onBackPressed()
                } else {
                    fragmentActivity.supportActionBar?.show()
                    findNavController().popBackStack()
                }

                albumViewModel.deleteAlbum(albumUuid)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            Log.e("Back", "home")
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                fragmentActivity.supportActionBar?.show()
                activity?.onBackPressed()
            } else {
                fragmentActivity.supportActionBar?.show()
                findNavController().popBackStack()
            }
        }
        return true
    }

    fun buttonSets(){
        val mainActivity = fragmentActivity as MainActivity
        binding.apply {
            ibBack.setOnClickListener(this@AlbumFragment)
            ibBack2.setOnClickListener(this@AlbumFragment)
            ibAlbumMenu.setOnClickListener(this@AlbumFragment)
        }

        mainActivity.binding.tvAddAlbumInLove.setOnClickListener(this@AlbumFragment)
        mainActivity.binding.tvDeleteAlbumFromLove.setOnClickListener(this@AlbumFragment)
        mainActivity.binding.tvGoToBandProfileFromAlbum.setOnClickListener(this@AlbumFragment)
        mainActivity.binding.tvDeleteAlbum.setOnClickListener(this@AlbumFragment)
    }






    companion object {

        @JvmStatic
        fun newInstance() = AlbumFragment()

    }
}