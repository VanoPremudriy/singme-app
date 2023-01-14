package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentNotAuthorizedBinding

class NotAuthorizedFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentNotAuthorizedBinding

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