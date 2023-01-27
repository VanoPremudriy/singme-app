package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentMyProjectsBinding


class MyProjectsFragment : Fragment() {

    lateinit var binding: FragmentMyProjectsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyProjectsBinding.inflate(layoutInflater)



        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = MyProjectsFragment()
    }
}