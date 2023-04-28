package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentNotAuthorizedBinding


class NotAuthorizedFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentNotAuthorizedBinding
    private lateinit var fragActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragActivity.title = getString(R.string.home)

        binding = FragmentNotAuthorizedBinding.inflate(layoutInflater)

        setButtons()

        // Inflate the layout for this fragment
        return binding.root
    }

    fun setButtons(){
        binding.bSignIn2.setOnClickListener(this@NotAuthorizedFragment)
        binding.bSignUp2.setOnClickListener(this@NotAuthorizedFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotAuthorizedFragment()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.bSignIn2.id -> {
                findNavController().navigate(R.id.action_notAuthorizedFragment_to_loginFragment)
            }
            binding.bSignUp2.id ->{
                findNavController().navigate(R.id.action_notAuthorizedFragment_to_registrationFragment)
            }
        }
    }
}