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

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    lateinit var loginViewModel: LoginViewModel
    private lateinit var fragActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity =activity as AppCompatActivity

        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragActivity.title = getString(R.string.sign_in)

        binding = FragmentLoginBinding.inflate(layoutInflater)

        val provider = ViewModelProvider(this)
        loginViewModel = provider[LoginViewModel::class.java]
        loginViewModel.isSuccess.observe(viewLifecycleOwner){
            if (it) {
                loginViewModel.isVerify.observe(viewLifecycleOwner){it1 ->
                    if (it1) {
                        findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                    } else {
                        Toast.makeText(context, getString(R.string.not_verify), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT).show()
            }
        }
        setButtons()

        // Inflate the layout for this fragment
        return binding.root
    }

    fun setButtons(){
        binding.bSignInLogin.setOnClickListener(this@LoginFragment)
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



            /*if (loginViewModel.isSuccess.value == true){
                if (loginViewModel.isVerify.value == true){
                    findNavController().navigate(R.id.action_loginFragment_to_profileFragment);
                }
                else {
                    Toast.makeText(context, getString(R.string.not_verify), Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT).show()
            }*/
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

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.bSignInLogin.id ->{
                signIn()
            }
        }
    }

}