package com.example.singmeapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.singmeapp.PathConverter
import com.example.singmeapp.databinding.FragmentCreateAlbumBinding
import com.example.singmeapp.items.Band
import com.example.singmeapp.viewmodels.CreateAlbumViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException


class CreateAlbumFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentCreateAlbumBinding
    var audioFileNamesList = ArrayList<String>()

    lateinit var file: Bitmap
    var albumCoverRequestBody: RequestBody? = null
    lateinit var albumCoverExtension: String
    var genresArray = ArrayList<String>()

    var audioRequestBodyList = ArrayList<RequestBody>()

    var albumFormat: String? = null

    lateinit var currentBand: Band

    lateinit var createAlbumViewModel: CreateAlbumViewModel

    private val pathConverter = PathConverter()


    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions() {
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

    @RequiresApi(Build.VERSION_CODES.P)
    val getAlbumCover = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val path = pathConverter.getPath(requireContext(), imageUri!!)
                val f = File(path)
                albumCoverExtension = f.extension
                albumCoverRequestBody = RequestBody.create(MediaType.parse("image/*"), f)


                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivAddAlbumCover)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    val getAudio = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.e("Code", it.resultCode.toString())
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var fileUri = it.data?.data
                if (fileUri != null) {
                    var path = pathConverter.getPath(requireContext(), fileUri)
                    var f= File(path)
                    audioRequestBodyList.add(RequestBody.create(MediaType.parse("audio/*"), f))
                    audioFileNamesList.add(f.name)
                    var tv = TextView(context)
                    tv.text = f.name.substring(0, f.name.length - 4)
                    binding.llTracks.addView(tv)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val provider = ViewModelProvider(this)
        createAlbumViewModel = provider[CreateAlbumViewModel::class.java]
        currentBand = arguments?.getSerializable("currentBand") as Band
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAlbumBinding.inflate(layoutInflater)

        setButtons()

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateAlbumFragment()
    }

    fun setButtons(){
        binding.apply {
            bAddAlbumCover.setOnClickListener(this@CreateAlbumFragment)
            bAddGenre.setOnClickListener(this@CreateAlbumFragment)
            bAddTrack.setOnClickListener(this@CreateAlbumFragment)
            bCreateAlbum.setOnClickListener(this@CreateAlbumFragment)
            rbAlbum.setOnClickListener(this@CreateAlbumFragment)
            rbSingleEp.setOnClickListener(this@CreateAlbumFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.bAddAlbumCover.id -> {
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getAlbumCover.launch(photoPickIntent)
            }

            binding.bAddGenre.id -> {
                binding.llGenres.addView(EditText(context))
            }

            binding.bAddTrack.id -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*"
                verifyStoragePermissions()
                getAudio.launch(intent)
            }

            binding.rbAlbum.id -> {
                albumFormat = "Album"
            }

            binding.rbSingleEp.id -> {
                albumFormat = "Single/EP"
            }

            binding.bCreateAlbum.id -> {
                createAlbumViewModel.createAlbum(binding.addAlbumName.text.toString(), currentBand, albumFormat!!, albumCoverRequestBody!!, albumCoverExtension, audioRequestBodyList, audioFileNamesList)
            }
        }
    }
}