package com.example.singmeapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentCreateBandBinding
import com.example.singmeapp.items.Band
import com.example.singmeapp.viewmodels.CreateBandViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.*


class CreateBandFragment : Fragment(), View.OnClickListener {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    lateinit var file: Bitmap
    var avatarRequestBody: RequestBody? = null
    var backRequestBody: RequestBody? = null
    lateinit var avatarExtension: String
    lateinit var backExtension: String
    var rolesArray = ArrayList<String>()
    lateinit var fragmentActivity: AppCompatActivity

    lateinit var binding: FragmentCreateBandBinding
    var band = Band("", "", "", "", "")
    lateinit var createBandViewModel: CreateBandViewModel

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
    val getBandAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val f = File(getRealPathFromURI(imageUri!!))
                avatarExtension = f.extension
                avatarRequestBody = RequestBody.create(MediaType.parse("image/*"), f)


                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivAddBandAvatar)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    val getBandBack = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val f = File(getRealPathFromURI(imageUri!!))
                backExtension = f.extension
                backRequestBody = RequestBody.create(MediaType.parse("image/*"), f)

                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivAddBandBack)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun getRealPathFromURI(contentUri: Uri): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = context?.getContentResolver()?.query(contentUri, proj, null, null, null)!!
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            path = cursor.getString(column_index)
        }
        cursor.close()
        return path
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = ViewModelProvider(this)
        setHasOptionsMenu(true)
        fragmentActivity = activity as AppCompatActivity
        createBandViewModel = provider[CreateBandViewModel::class.java]

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = getString(R.string.create_project)
        binding = FragmentCreateBandBinding.inflate(layoutInflater)

        setButtons()

        createBandViewModel.isExist.observe(viewLifecycleOwner){
            if (it == true){
                Toast.makeText(context, getString(R.string.this_project_already_exist), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }



    companion object {

        @JvmStatic
        fun newInstance() = CreateBandFragment()

    }

    fun setButtons(){
        binding.bAddBandAvatar.setOnClickListener(this@CreateBandFragment)
        binding.bCreateBand.setOnClickListener(this@CreateBandFragment)
        binding.bAddBandBack.setOnClickListener(this@CreateBandFragment)
        binding.bAddRole.setOnClickListener(this@CreateBandFragment)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.bAddBandAvatar.id ->{
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getBandAvatar.launch(photoPickIntent)
            }
            binding.bAddBandBack.id -> {
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getBandBack.launch(photoPickIntent)
            }
            binding.bCreateBand.id -> {
                if (
                    binding.addBandName.text.toString() != ""
                    && avatarRequestBody != null
                    && backRequestBody != null
                    && binding.etBandInfo.text.toString() != ""
                ){
                    band.name = binding.addBandName.text.toString()
                    band.info = binding.etBandInfo.text.toString()
                    binding.llRoles.children.forEach { view ->
                        run {
                            if (view::class.java == EditText::class.java) {
                                if ((view as EditText).text.toString() != "")
                                    rolesArray.add((view as EditText).text.toString())
                            }
                        }
                    }
                    createBandViewModel.createBand(band,avatarRequestBody!!, backRequestBody!!, avatarExtension, backExtension, rolesArray)
                    findNavController().navigate(R.id.profileFragment)
                }
                else {
                    Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            }
            binding.bAddRole.id -> {
                binding.llRoles.addView(EditText(context))
            }
        }
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
}