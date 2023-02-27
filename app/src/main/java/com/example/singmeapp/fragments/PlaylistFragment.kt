package com.example.singmeapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.PlaylistAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentFriendsBinding
import com.example.singmeapp.databinding.FragmentPlaylistBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException

class PlaylistFragment : Fragment(), View.OnClickListener {

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPlaylistBinding
    lateinit var playlistViewModel: PlaylistViewModel
    lateinit var trackAdapter: TrackAdapter
    lateinit var playlistUuid: String
    lateinit var playlist: Album
    var isEdit = false

    lateinit var coverExtension: String
    lateinit var coverRequestBody: RequestBody

    private fun verifyStoragePermissions() {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (activity != null) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = context?.contentResolver?.query(contentUri, proj, null, null, null)!!
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            path = cursor.getString(column_index)
        }
        cursor.close()
        return path
    }

    private val getPlaylistCover = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val f = File(getRealPathFromURI(imageUri!!))
                coverExtension = f.extension
                coverRequestBody = RequestBody.create(MediaType.parse("image/*"), f)
                playlistViewModel.changeImage(coverRequestBody!!, coverExtension, "cover")
                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivPlaylistCover)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        playlistUuid = arguments?.getString("playlistUuid").toString()
        Log.e("ds", playlistUuid)
        val provider = ViewModelProvider(this)
        playlistViewModel = provider[PlaylistViewModel::class.java]

        trackAdapter = TrackAdapter(activity as AppCompatActivity, this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.hide()
        playlistViewModel.getPlaylistData(playlistUuid)
        playlistViewModel.getTracks(playlistUuid)
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        isEdit = false
        binding.llTopMenu.visibility = View.GONE
        binding.ibEditPlaylistCover.visibility = View.GONE
        binding.ibAddTrackInPlaylist.visibility = View.INVISIBLE
        binding.ibAddOrChangePlaylist.setImageResource(R.drawable.ic_change)

        playlistViewModel.curPlaylist.observe(viewLifecycleOwner){
            playlist = it
            binding.tvPlaylistName.text = it.name
            if (it.isAuthor){
                binding.tvPlaylistAuthorName.text = getString(R.string.my_music)
            } else binding.tvPlaylistAuthorName.text = it.band

            if (it.imageUrl != ""){
                Picasso.get().load(it.imageUrl).fit().into(binding.ivPlaylistCover)
            }

            if (it.isAuthor){
                binding.ibAddOrChangePlaylist.setImageResource(R.drawable.ic_change)
            }

        }

        binding.rcView.layoutManager = LinearLayoutManager(context)
        playlistViewModel.listTrack.observe(viewLifecycleOwner){
            trackAdapter.trackList = it as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
            binding.rcView.adapter = trackAdapter
        }

        binding.ibAddOrChangePlaylist.setOnClickListener(this@PlaylistFragment)
        binding.ibEditPlaylistCover.setOnClickListener(this@PlaylistFragment)
        binding.ibAddTrackInPlaylist.setOnClickListener(this@PlaylistFragment)
        binding.ibPlaylistBack.setOnClickListener(this@PlaylistFragment)
        binding.ibAPlaylistBack2.setOnClickListener(this@PlaylistFragment)
        binding.ibPlaylistMenu.setOnClickListener(this@PlaylistFragment)
        binding.ibPlaylistMenu2.setOnClickListener(this@PlaylistFragment)
        mainActivity.binding.tvDeletePlaylist.setOnClickListener(this@PlaylistFragment)
        mainActivity.binding.tvChangePlaylist.setOnClickListener(this@PlaylistFragment)

        binding.playlistScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.playlistScrollView.scrollY > 150){
                binding.llTopMenu.visibility = View.VISIBLE
            } else {
                binding.llTopMenu.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = PlaylistFragment()
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.ibAddOrChangePlaylist.id -> {
               change()
            }
            binding.ibEditPlaylistCover.id -> {
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getPlaylistCover.launch(photoPickIntent)
                playlistViewModel.getPlaylistData(playlistUuid)
            }
            binding.ibAddTrackInPlaylist.id -> {
                val bundle = Bundle()
                bundle.putString("playlistUuid", playlistUuid)
                fragmentActivity.supportActionBar?.show()
                playlistViewModel.getPlaylistData(playlistUuid)
                findNavController().navigate(R.id.chooseTrackForPlaylistFragment, bundle)
            }

            binding.ibPlaylistBack.id -> {
                back()
            }

            binding.ibAPlaylistBack2.id -> {
                back()
            }

            binding.ibPlaylistMenu.id -> {
                menu()
            }

            binding.ibPlaylistMenu2.id -> {
                menu()
            }

            mainActivity.binding.tvDeletePlaylist.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                playlistViewModel.deletePlaylist(playlistUuid)
                back()
            }
            mainActivity.binding.tvChangePlaylist.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                change()
            }
        }
    }

    fun change() {
        if (playlist.isAuthor) {
            if (!isEdit) {
                isEdit = true
                binding.ibEditPlaylistCover.visibility = View.VISIBLE
                binding.ibAddTrackInPlaylist.visibility = View.VISIBLE
                binding.ibAddOrChangePlaylist.setImageResource(R.drawable.ic_apply)
            }
            else {
                isEdit = false
                binding.ibEditPlaylistCover.visibility = View.GONE
                binding.ibAddTrackInPlaylist.visibility = View.INVISIBLE
                binding.ibAddOrChangePlaylist.setImageResource(R.drawable.ic_change)
            }
        }
    }
    fun menu(){
        if (playlist.isAuthor){
            mainActivity.binding.tvDeletePlaylist.visibility = View.VISIBLE
            mainActivity.binding.tvChangePlaylist.visibility = View.VISIBLE
            mainActivity.binding.tvAddPlaylisToLove.visibility = View.GONE
            mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.GONE
        } else {
            mainActivity.binding.tvDeletePlaylist.visibility = View.GONE
            mainActivity.binding.tvChangePlaylist.visibility = View.GONE
            if (playlist.isInLove){
                mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.VISIBLE
                mainActivity.binding.tvAddPlaylisToLove.visibility = View.GONE
            } else {
                mainActivity.binding.tvDeletePlaylistFromLove.visibility = View.GONE
                mainActivity.binding.tvAddPlaylisToLove.visibility = View.VISIBLE
            }
        }


        mainActivity.binding.playlistMenu.visibility = View.VISIBLE
        mainActivity.binding.view15.visibility = View.VISIBLE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun back(){
        val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

        if (count == 0) {
            fragmentActivity.supportActionBar?.show()
            activity?.onBackPressed()

        } else {
            fragmentActivity.supportActionBar?.show()
            findNavController().popBackStack()
        }
    }

}