package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance()

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

        auth = FirebaseAuth.getInstance()
        database.getReference("users/"+auth.currentUser?.uid + "/profile/name").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
            }

        }

        )


        binding.button3.setOnClickListener {
            auth.signOut()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, NotAuthorizedFragment.newInstance())?.commit()
        }


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }



}