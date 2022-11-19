package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()


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
        auth = FirebaseAuth.getInstance()

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

            auth.signInWithEmailAndPassword(uEmail, uPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.fragmentContainer, ProfileFragment.newInstance())
                                ?.commit()
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.not_verify),
                                Toast.LENGTH_SHORT
                            ).show()
                            auth.signOut()
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, NotAuthorizedFragment.newInstance())?.commit()
        }
        return true
    }

    private fun textValidation():Boolean{
        val uEmail = binding.etEmailInLogin.text.toString()
        val uPassword = binding.etPasswordInLogin.text.toString()

        if (uEmail == ""){
            binding.tvEmailValidLogin.text = getString(R.string.null_email)
            return false
        }

        if (uPassword == ""){
            binding.tvPasswordValidLogin.text = getString(R.string.null_pass)
        }

        if (uEmail.indexOf('@') == -1){
            binding.tvEmailValidLogin.text = getString(R.string.incorrect_email)
            return false
        }

        return true
    }

    private fun clearValid(){

    }

}