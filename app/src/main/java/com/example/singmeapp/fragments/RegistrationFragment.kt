package com.example.singmeapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentRegistrationBinding
import com.example.singmeapp.viewmodels.RegistrationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RegistrationFragment : Fragment() {

    lateinit var binding: FragmentRegistrationBinding
    lateinit var regViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragActivity = activity as AppCompatActivity
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragActivity.title = getString(R.string.sign_up)
        setHasOptionsMenu(true)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        val provider = ViewModelProvider(this)
        regViewModel = provider[RegistrationViewModel::class.java]
        binding.bSignUpReg.setOnClickListener {
            regViewModel.clearValid()
            signUp()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegistrationFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUp(){
        if (textValidation()) {
            val uName =binding.etNameInRegistration.text.toString()
            val uRealName = binding.etRealNameInRegistration.text.toString()
            val uLastName = binding.etLastNameInRegistration.text.toString()
            val uEmail = binding.etEmailInRegistration.text.toString()
            val uPassword = binding.etPasswordInRegistration.text.toString()
            val uSex = binding.radioChooseSex.checkedRadioButtonId.toString()
            val year = binding.datePicker1.year
            val month = binding.datePicker1.month
            val day = binding.datePicker1.dayOfMonth
            val uLocalDateTime = LocalDate.parse("$day/$month/$year", DateTimeFormatter.ofPattern("d/M/yyyy")).toString()

            Log.e("a", uSex)
            Log.e("a", uLocalDateTime)
            regViewModel.setRegValues(arrayListOf(uName,uRealName, uLastName, uEmail, uPassword, uSex, uLocalDateTime))
            regViewModel.registration()
            regViewModel.isSuccess.observe(viewLifecycleOwner){
                if (it){
                    Toast.makeText(context, getString(R.string.verify_message), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                }
                else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun textValidation():Boolean{
        val uName =binding.etNameInRegistration.text.toString()
        val uRealName = binding.etRealNameInRegistration.text.toString()
        val uLastName = binding.etLastNameInRegistration.text.toString()
        val uEmail = binding.etEmailInRegistration.text.toString()
        val uPassword = binding.etPasswordInRegistration.text.toString()
        val uRepPassword = binding.etRepeatPasswordInRegistration.text.toString()
        val uSex = binding.radioChooseSex.checkedRadioButtonId.toString()

        regViewModel.setValidValues(arrayListOf(uName, uRealName, uLastName, uEmail, uPassword, uRepPassword, uSex))

        val result =  regViewModel.Valid()
        observeViewModel()
        return result

    }


    fun observeViewModel(){
        regViewModel.nameValid.observe(viewLifecycleOwner){
            binding.tvNameValid.text = it.toString()
        }

        regViewModel.realNameValid.observe(viewLifecycleOwner){
            binding.tvRealNameValid.text = it.toString()
        }

        regViewModel.lastNameValid.observe(viewLifecycleOwner){
            binding.tvLastNameValid.text = it.toString()
        }

        regViewModel.emailValid.observe(viewLifecycleOwner){
            binding.tvEmailValid.text = it.toString()
        }

        regViewModel.passValid.observe(viewLifecycleOwner){
            binding.tvPassValid.text = it.toString()
        }

        regViewModel.repPassValid.observe(viewLifecycleOwner){
            binding.tvRepPassValid.text = it.toString()
        }

        regViewModel.sexValid.observe(viewLifecycleOwner){
            binding.tvSexValid.text = it.toString()
        }

    }

}