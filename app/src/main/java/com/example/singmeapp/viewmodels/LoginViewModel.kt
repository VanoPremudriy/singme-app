package com.example.singmeapp.viewmodels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.App
import com.example.singmeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel: ViewModel() {
    var email = ""
    var pass = ""

    var emailValid = MutableLiveData<String>()
    var passValid = MutableLiveData<String>()

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    var isSuccess = MutableLiveData<Boolean>()
    var isVerify = MutableLiveData<Boolean>()

    private var database = FirebaseDatabase.getInstance()
    fun setValidVaues(list: ArrayList<String>){
        email = list[0]
        pass = list[1]
    }

    fun valid(): Boolean{
        if (email == ""){
            emailValid.value = App.getRes().getString(R.string.null_email)
            return false
        }

        if (pass == ""){
            passValid.value = App.getRes().getString(R.string.null_pass)
            return false
        }

        if (email.indexOf('@') == -1){
            emailValid.value = App.getRes().getString(R.string.incorrect_email)
            return false
        }

        return true
    }


    fun setLoginValues(list: ArrayList<String>){
        email = list[0]
        pass = list[1]
    }

    fun logIn(){
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                isSuccess.value = true
                if (auth.currentUser?.isEmailVerified == true) {
                    isVerify.value = true
                } else {
                    isVerify.value = false
                    auth.signOut()
                }
            }
            else {
                isSuccess.value = false
                auth.signOut()
            }
        }
    }




}