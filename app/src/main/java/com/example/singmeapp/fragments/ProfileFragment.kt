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
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentProfileBinding
import com.example.singmeapp.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.processNextEventInCurrentThread


class ProfileFragment : Fragment(), View.OnTouchListener {

    lateinit var binding: FragmentProfileBinding
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragActivity = activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.profile)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.imageButton.setOnClickListener {
            binding.profileMenu.visibility  = View.VISIBLE
        }
        binding.profileMenu.setOnTouchListener(this@ProfileFragment)
        binding.profileLayout.setOnTouchListener(this@ProfileFragment)

        val provider = ViewModelProvider(this)
        profileViewModel = provider[ProfileViewModel::class.java]

        observe()


        binding.button3.setOnClickListener {
            profileViewModel.auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_notAuthorizedFragment)
        }

        return binding.root
    }

    fun observe(){
        profileViewModel.currentUser.observe(viewLifecycleOwner){
            Picasso.get().load(it.avatarUrl).fit().into(binding.imageView2)
            binding.textView.text = it.name
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

        val x = p1?.x
        val y = p1?.y
        if (p1?.action == MotionEvent.ACTION_DOWN){
            Log.e("qe", p0.toString())
            if (p0?.id != binding.profileMenu.id) binding.profileMenu.visibility = View.GONE
        }

        return true
    }


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

}