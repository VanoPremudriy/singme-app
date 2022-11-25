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
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentRegistrationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegistrationFragment : Fragment() {

    lateinit var binding: FragmentRegistrationBinding
    lateinit var auth: FirebaseAuth
    var database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragActivity = activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragActivity.title = getString(R.string.sign_up)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        binding.bSignUpReg.setOnClickListener {
            clearValid()
            signUp()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegistrationFragment()
    }

    private fun signUp(){
        if (textValidation()) {
            val uName =binding.etNameInRegistration.text.toString()
            val uLastName = binding.etLastNameInRegistration.text.toString()
            val uEmail = binding.etEmailInRegistration.text.toString()
            val uPassword = binding.etPasswordInRegistration.text.toString()
            auth.createUserWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener { it1 ->
                if (it1.isSuccessful) {
                    auth.signInWithEmailAndPassword(uEmail, uPassword)
                        .addOnCompleteListener { it2 ->
                            if (it2.isSuccessful) {
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/name")
                                    .setValue(uName)
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/last_name")
                                    .setValue(uLastName)
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/email")
                                    .setValue(uEmail)
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/password")
                                    .setValue(uPassword)
                                auth.currentUser?.sendEmailVerification()
                            }
                        }
                    Toast.makeText(context, getString(R.string.verify_message), Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    findNavController().navigate(R.id.action_registrationFragment_to_loginFragment);
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            findNavController().navigate(R.id.action_registrationFragment_pop);
        }
        return true
    }

    private fun textValidation():Boolean{
        val uName =binding.etNameInRegistration.text.toString()
        val uLastName = binding.etLastNameInRegistration.text.toString()
        val uEmail = binding.etEmailInRegistration.text.toString()
        val uPassword = binding.etPasswordInRegistration.text.toString()
        val uRepPassword = binding.etRepeatPasswordInRegistration.text.toString()
        if (uName == ""){
            binding.tvNameValid.text = getString(R.string.null_name)
            return false
        }

        if (uLastName == ""){
            binding.tvLastNameValid.text = getString(R.string.null_last_name)
            return false
        }

        if (uEmail == ""){
            binding.tvEmailValid.text = getString(R.string.null_email)
            return false
        }

        if (uPassword == ""){
            binding.tvPassValid.text = getString(R.string.null_pass)
            return false
        }

        if (uPassword != uRepPassword){
            binding.tvPassValid.text = getString(R.string.not_equals_pass)
            binding.tvRepPassValid.text = getString(R.string.not_equals_pass)
            return false
        }

        if (uEmail.indexOf('@') == -1){
            binding.tvEmailValid.text = getString(R.string.incorrect_email)
            return false
        }

        if (uPassword.length < 8){
            binding.tvPassValid.text = getString(R.string.small_pass)
            return false
        }
        return true
    }

    private fun clearValid(){
        binding.apply {
            tvNameValid.text = ""
            tvLastNameValid.text = ""
            tvEmailValid.text = ""
            tvPassValid.text = ""
            tvRepPassValid.text = ""
        }
    }

}