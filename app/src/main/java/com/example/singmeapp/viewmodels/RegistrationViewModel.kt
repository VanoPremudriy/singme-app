package com.example.singmeapp.viewmodels

import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.App
import com.example.singmeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationViewModel: ViewModel() {
    lateinit var auth: FirebaseAuth
    var database = Firebase.database

    init {
        auth = FirebaseAuth.getInstance()
    }

    var uName = ""
    var uLastName = ""
    var uEmail = ""
    var uPass = ""
    var uRepPass = ""

    var nameValid = MutableLiveData<String>()
    val lastNameValid = MutableLiveData<String>()
    val emailValid = MutableLiveData<String>()
    val passValid = MutableLiveData<String>()
    val repPassValid = MutableLiveData<String>()

    var isSuccess = MutableLiveData<Boolean>()

    fun setValidValues(list: ArrayList<String>){
        uName = list[0]
        uLastName = list[1]
        uEmail = list[2]
        uPass = list[3]
        uRepPass = list[4]
    }

    fun setRegValues(list: ArrayList<String>){
        uName = list[0]
        uLastName = list[1]
        uEmail = list[2]
        uPass = list[3]
    }


    fun Valid():Boolean{

        if (uName == ""){
            nameValid.value = App.getRes().getString(R.string.null_name)
            return false
        }

        if (uLastName == ""){
            lastNameValid.value = App.getRes().getString(R.string.null_last_name)
            return false
        }

        if (uEmail == ""){
            emailValid.value = App.getRes().getString(R.string.null_email)
            return false
        }

        if (uPass == ""){
            passValid.value = App.getRes().getString(R.string.null_pass)
            return false
        }

        if (uPass != uRepPass){
            passValid.value = App.getRes().getString(R.string.not_equals_pass)
            repPassValid.value = App.getRes().getString(R.string.not_equals_pass)
            return false
        }

        if (uEmail.indexOf('@') == -1){
            emailValid.value = App.getRes().getString(R.string.incorrect_email)
            return false
        }

        if (uPass.length < 8){
            passValid.value = App.getRes().getString(R.string.small_pass)
            return false
        }
        return true
    }

    fun clearValid(){
        nameValid.value = ""
        lastNameValid.value = ""
        emailValid.value = ""
        passValid.value = ""
        repPassValid.value = ""
    }

    fun registration() {
        auth.createUserWithEmailAndPassword(uEmail, uPass).addOnCompleteListener { it1 ->
            Log.d("REG", "create user")
            if (it1.isSuccessful) {
                Log.d("REG", "success")
                auth.signInWithEmailAndPassword(uEmail, uPass)
                    .addOnCompleteListener { it2 ->
                        Log.d("REG", "sign in")
                        if (it2.isSuccessful) {
                            Log.d("REG", "success")
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/name")
                                .setValue(uName)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/last_name")
                                .setValue(uLastName)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/email")
                                .setValue(uEmail)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/password")
                                .setValue(uPass)
                            auth.currentUser?.sendEmailVerification()
                            isSuccess.value = true
                        }
                        else isSuccess.value = false
                        auth.signOut()
                    }
            }
            else isSuccess.value = false
        }
    }

}