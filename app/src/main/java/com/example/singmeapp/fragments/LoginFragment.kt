package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentLoginBinding
import com.example.singmeapp.viewmodels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import java.security.Provider
import kotlin.math.log

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var fragActivity =activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        fragActivity.title = getString(R.string.sign_in)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        val provider = ViewModelProvider(this)
        loginViewModel = provider[LoginViewModel::class.java]

        binding.bSignInLogin.setOnClickListener {
            signIn()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }


    private fun signIn(){
        if (textValidation()) {
            val uEmail = binding.etEmailInLogin.text.toString()
            val uPassword = binding.etPasswordInLogin.text.toString()
            Log.d("LOG", "qq")
            loginViewModel.setLoginValues(arrayListOf(uEmail, uPassword))
            loginViewModel.logIn()

            if (loginViewModel.isSuccess.value == true){
                if (loginViewModel.isVerify.value == true){
                    findNavController().navigate(R.id.action_loginFragment_to_profileFragment);
                }
                else {
                    Toast.makeText(context, getString(R.string.not_verify), Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            findNavController().navigate(R.id.action_loginFragment_pop);
        }
        return true
    }

    private fun textValidation():Boolean{
        val uEmail = binding.etEmailInLogin.text.toString()
        val uPassword = binding.etPasswordInLogin.text.toString()

        loginViewModel.setValidVaues(arrayListOf(uEmail, uPassword))

        val result =  loginViewModel.valid()
        observeViewModel()
        return result

    }

    fun observeViewModel(){
        loginViewModel.emailValid.observe(viewLifecycleOwner){
            binding.tvEmailValidLogin.text = it.toString()
        }

        loginViewModel.passValid.observe(viewLifecycleOwner){
            binding.tvPasswordValidLogin.text = it.toString()
        }

    }

}