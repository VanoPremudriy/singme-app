package com.example.singmeapp.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.MemberAdapter
import com.example.singmeapp.databinding.FragmentBandBinding
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Member
import com.example.singmeapp.viewmodels.BandViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException


class BandFragment : Fragment(), View.OnClickListener, MenuProvider {

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    lateinit var binding: FragmentBandBinding
    lateinit var memberAdapter: MemberAdapter
    lateinit var bandViewModel: BandViewModel
    lateinit var band: Band
    lateinit var bandUuid:String
    lateinit var fragmentActivity: AppCompatActivity
    var isMenuProvider = false
    lateinit var optionsMenu: Menu

    lateinit var file: Bitmap
    var avatarRequestBody: RequestBody? = null
    var backRequestBody: RequestBody? = null
    lateinit var avatarExtension: String
    lateinit var backExtension: String
    var progress: ProgressDialog? = null

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

    @RequiresApi(Build.VERSION_CODES.P)
    val getBandAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val f = File(getRealPathFromURI(imageUri!!))
                avatarExtension = f.extension
                avatarRequestBody = RequestBody.create(MediaType.parse("image/*"), f)
                bandViewModel.changeImage(binding.tvBandNameBandFragment.text.toString(), bandUuid, avatarRequestBody!!, avatarExtension, "avatar")
                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivBandAvatar)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private val getBandBack = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                var imageUri = it.data?.data
                val f = File(getRealPathFromURI(imageUri!!))
                backExtension = f.extension
                backRequestBody = RequestBody.create(MediaType.parse("image/*"), f)
                bandViewModel.changeImage(binding.tvBandNameBandFragment.text.toString(), bandUuid,  backRequestBody!!, backExtension, "back")
                Picasso.get().load(imageUri).fit().noPlaceholder().noFade().centerCrop().into(binding.ivBandBack)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity

        setHasOptionsMenu(true)

        bandUuid = arguments?.getString("bandUuid").toString()
        val provider = ViewModelProvider(this)
        bandViewModel = provider[BandViewModel::class.java]
        bandViewModel.getMembers(bandUuid)
        bandViewModel.getBandDate(bandUuid)

    }
    private fun convert(value: Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)



        binding = FragmentBandBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        setButtons()

       /* binding.tvBandNameBandFragment.text = band.name
        if (band.imageUrl != ""){
            Picasso.get().load(band.imageUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivBandAvatar)
        }
        if (band.backgroundUrl != ""){
            Picasso.get().load(band.backgroundUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivBandBack)
        }

        binding.tvBandInfoInBandFragment.text = band.info*/

        binding.rcVievMembers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        memberAdapter = MemberAdapter(this)
        bandViewModel.listMember.observe(viewLifecycleOwner){
            memberAdapter.memberList =it as ArrayList<Member> /* = java.util.ArrayList<com.example.singmeapp.items.Member> */
            binding.rcVievMembers.adapter = memberAdapter
            it.forEach { it1 ->
                Log.e("RC", "OBSERVE")
                Log.e("RC", (it1.uuid == bandViewModel.auth.currentUser?.uid).toString())
                Log.e("RC", (!isMenuProvider).toString())
                if (it1.uuid == bandViewModel.auth.currentUser?.uid && !isMenuProvider){
                    fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
                    isMenuProvider = true
                }
            }
        }

        bandViewModel.currentBand.observe(viewLifecycleOwner){
            band = it
            binding.tvBandNameBandFragment.text = it.name
            binding.tvBandInfoInBandFragment.text = it.info
            fragmentActivity.title = it.name
            if (it.imageUrl != ""){
                Picasso.get().load(it.imageUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivBandAvatar)
            }
            if (it.backgroundUrl != ""){
                Picasso.get().load(it.backgroundUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivBandBack)
            }
        }


        bandViewModel.editText.observe(viewLifecycleOwner){
            if (binding.tvBandInfoInBandFragment.text != it){
                bandViewModel.editBandInfo(bandUuid)
            }
        }

        bandViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["avatar"] == true && it["bandAvatar"] == true && it["bandBack"] == true){
                binding.bandProgressLayout.visibility = View.GONE
            }
        }


        return binding.root
    }

    override fun onPause() {
        super.onPause()
        isMenuProvider = false
    }

    fun setButtons(){
        binding.ibWrapBandInfo.setOnClickListener(this@BandFragment)
        binding.ibWrapBandMembers.setOnClickListener(this@BandFragment)
        binding.idDiscography.setOnClickListener(this@BandFragment)
        binding.ibEditBandInfo.setOnClickListener(this@BandFragment)
        binding.ibEditBandBackInBandFragment.setOnClickListener(this@BandFragment)
        binding.ibEditBandAvatarInBandFragment.setOnClickListener(this@BandFragment)
        binding.ibAddNewMember.setOnClickListener(this@BandFragment)

    }

    companion object {
        @JvmStatic
        fun newInstance() = BandFragment()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.ibWrapBandInfo.id -> {
                wrap(binding.llBandInfo)
            }

            binding.ibWrapBandMembers.id -> {
                wrap(binding.llBandMembers)
            }

            binding.ibAddBand.id -> {
                Toast.makeText(context, "Band added", Toast.LENGTH_SHORT).show()
            }

            binding.ibSubscribeBand.id ->{
                Toast.makeText(context, "Subscribed", Toast.LENGTH_SHORT).show()
            }
            binding.idDiscography.id ->{
                val bundle = Bundle()
                bundle.putInt("Back", R.id.bandFragment)
                bundle.putSerializable("band", band)
                Log.e("band", band.name)
                isMenuProvider = false
                findNavController().navigate(R.id.discographyFragment, bundle)
            }
            binding.ibEditBandInfo.id ->{
                editBandInfoClick()
            }
            binding.ibEditBandBackInBandFragment.id ->{
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getBandBack.launch(photoPickIntent)
            }
            binding.ibEditBandAvatarInBandFragment.id ->{
                val photoPickIntent = Intent(Intent.ACTION_PICK)
                photoPickIntent.type = "image/"
                verifyStoragePermissions()
                getBandAvatar.launch(photoPickIntent)
            }
            binding.ibAddNewMember.id -> {
                bandViewModel.isEdit.value = false
                isMenuProvider = false
                val bundle = Bundle()
                bundle.putSerializable("band", band)
                findNavController().navigate(R.id.chooseMemberFragment, bundle)
            }
        }
    }

    private fun wrap(wrapItem: View){
        if (wrapItem.height == convert(24)) {
            Log.e("efs", "YES")
            val params = wrapItem.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.rcVievMembers.adapter = memberAdapter
            wrapItem.layoutParams = params
        }
        else {
            val params = wrapItem.layoutParams
            params.height = convert(24)
            wrapItem.layoutParams = params
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
                if (count == 0) {
                    activity?.onBackPressed()
                } else {
                    isMenuProvider = false
                    bandViewModel.isEdit.value = false
                    findNavController().popBackStack()
                }
        }
        if (item.itemId == R.id.bandEdit){
            bandViewModel.isEdit.value = true
            item.isVisible = false
            optionsMenu.getItem(1).isVisible = true
            binding.ibAddNewMember.visibility = View.VISIBLE
            binding.ibEditBandInfo.visibility = View.VISIBLE
            binding.ibEditBandAvatarInBandFragment.visibility = View.VISIBLE
            binding.ibEditBandBackInBandFragment.visibility = View.VISIBLE
        }

        if (item.itemId == R.id.bandEditApply){
            if (binding.etBandInfoInBandFragment.visibility == View.VISIBLE){
                Toast.makeText(context, getString(R.string.finish_editing_text), Toast.LENGTH_SHORT).show()
            }
            else {
                bandViewModel.isEdit.value = false
                item.isVisible = false
                optionsMenu.getItem(0).isVisible = true
                binding.ibAddNewMember.visibility = View.GONE
                binding.ibEditBandInfo.visibility = View.GONE
                binding.ibEditBandAvatarInBandFragment.visibility = View.GONE
                binding.ibEditBandBackInBandFragment.visibility = View.GONE
            }
        }
        return true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.band_editor_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }



    private fun editBandInfoClick(){
        if (binding.etBandInfoInBandFragment.visibility == View.GONE){
          beginEditBandInfo()
        }
        else {
            endEditBandInfo()
        }
    }

    private fun beginEditBandInfo(){
        binding.etBandInfoInBandFragment.setText(binding.tvBandInfoInBandFragment.text.toString(), TextView.BufferType.EDITABLE)
        binding.etBandInfoInBandFragment.visibility = View.VISIBLE
        binding.tvBandInfoInBandFragment.visibility = View.GONE
        binding.ibEditBandInfo.setImageResource(android.R.drawable.checkbox_on_background)
    }

    private fun endEditBandInfo(){
        bandViewModel.editText.value = binding.etBandInfoInBandFragment.text.toString()
        binding.tvBandInfoInBandFragment.text = bandViewModel.editText.value
        binding.etBandInfoInBandFragment.visibility = View.GONE
        binding.tvBandInfoInBandFragment.visibility = View.VISIBLE
        binding.ibEditBandInfo.setImageResource(android.R.drawable.ic_menu_edit)
    }

}