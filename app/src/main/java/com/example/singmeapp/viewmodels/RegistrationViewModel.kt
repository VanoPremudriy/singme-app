package com.example.singmeapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    var uRealName = ""
    var uLastName = ""
    var uEmail = ""
    var uPass = ""
    var uRepPass = ""
    var uSex = -1
    var uBirthday = ""

    var nameValid = MutableLiveData<String>()
    var realNameValid = MutableLiveData<String>()
    val lastNameValid = MutableLiveData<String>()
    val emailValid = MutableLiveData<String>()
    val passValid = MutableLiveData<String>()
    val repPassValid = MutableLiveData<String>()
    val sexValid = MutableLiveData<String>()

    var isSuccess = MutableLiveData<Boolean>()

    fun setValidValues(list: ArrayList<String>){
        uName = list[0]
        uRealName = list[1]
        uLastName = list[2]
        uEmail = list[3]
        uPass = list[4]
        uRepPass = list[5]
        uSex = list[6].toInt()
    }

    fun setRegValues(list: ArrayList<String>){
        uName = list[0]
        uRealName = list[1]
        uLastName = list[2]
        uEmail = list[3]
        uPass = list[4]
        uSex = list[5].toInt()
        uBirthday = list[6]
    }


    fun Valid():Boolean{

        if (uName.isEmpty()){
            nameValid.value = App.getRes().getString(R.string.null_name)
            return false
        }

        if (uRealName.isEmpty()){
            realNameValid.value = App.getRes().getString(R.string.null_real_name)
            return false
        }

        if (uLastName.isEmpty()){
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

        if (uSex == -1) {
            sexValid.value = App.getRes().getString(R.string.choose_sex)
            return false
        }
        return true
    }

    fun clearValid(){
        nameValid.value = ""
        realNameValid.value = ""
        lastNameValid.value = ""
        emailValid.value = ""
        passValid.value = ""
        repPassValid.value = ""
        sexValid.value = ""
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
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/real_name")
                                .setValue(uRealName)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/last_name")
                                .setValue(uLastName)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/email")
                                .setValue(uEmail)
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/password")
                                .setValue(uPass)
                            if (uSex == R.id.rbMale) {
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/sex")
                                    .setValue("male")
                            } else {
                                database.getReference("users/" + auth.currentUser?.uid + "/profile/sex")
                                    .setValue("female")
                            }
                            database.getReference("users/" + auth.currentUser?.uid + "/profile/birthday")
                                .setValue(uBirthday)
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