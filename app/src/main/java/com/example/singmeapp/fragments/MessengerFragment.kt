package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentMessengerBinding

class MessengerFragment : Fragment() {

    lateinit var binding: FragmentMessengerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragActivity = activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.messenger)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessengerBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MessengerFragment()
    }
}