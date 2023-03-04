package com.example.singmeapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentProfileBinding
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.processNextEventInCurrentThread


class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var fragActivity: AppCompatActivity
    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentProfileBinding
    lateinit var profileViewModel: ProfileViewModel
    val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        val provider = ViewModelProvider(this)
        profileViewModel = provider[ProfileViewModel::class.java]
        if (arguments?.getSerializable("otherUser") != null){
            profileViewModel.getOtherData((requireArguments().getSerializable("otherUser") as User).uuid.toString())
        }
        else profileViewModel.getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.profile)
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)

        buttonSets()
        observe()

        return binding.root
    }

    private fun observe(){

        profileViewModel.currentUser.observe(viewLifecycleOwner){
            if (it.avatarUrl != "")
            Picasso.get().load(it.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivProfileAvatar)
            bundle.putSerializable("curUser", it)
            binding.textView.text = it.name
            if (it.uuid != profileViewModel.auth.uid.toString()){
                binding.tvMyBands.text = getString(R.string.projects)
                binding.tvMyFriends.text = getString(R.string.friends)
            }
        }

        profileViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["avatar"] == true){
                binding.profileProgressLayout.visibility = View.GONE
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun buttonSets(){
        binding.imageButton.setOnClickListener(this@ProfileFragment)
        binding.idMyBands.setOnClickListener(this@ProfileFragment)
        binding.idMyFriends.setOnClickListener(this@ProfileFragment)
        mainActivity.binding.tvChangeAvatarInProfile.setOnClickListener(this@ProfileFragment)
        mainActivity.binding.tvProfileExitInProfile.setOnClickListener(this@ProfileFragment)
    }




    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onClick(p0: View?) {
        when (p0?.id){

            binding.imageButton.id -> {
                mainActivity.binding.profileMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
            binding.idMyBands.id -> {
                findNavController().navigate(R.id.loveBandsFragment, bundle)
            }
            binding.idMyFriends.id ->{
                findNavController().navigate(R.id.friendsFragment, bundle)
            }
            mainActivity.binding.tvChangeAvatarInProfile.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }
            mainActivity.binding.tvProfileExitInProfile.id -> {
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                profileViewModel.auth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_notAuthorizedFragment)
            }
        }
    }

}