package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.App
import com.example.singmeapp.R
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.Period

class ChangeUserDataViewModel: ViewModel() {
    val currentUser = MutableLiveData<User>()
    var auth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    lateinit var user: User
    var realNameValid = MutableLiveData<String>()
    val lastNameValid = MutableLiveData<String>()
    val mutDate = MutableLiveData<LocalDate>()
    var uRealName = ""
    var uLastName = ""

    @SuppressLint("SuspiciousIndentation")
    fun getData(){
        if (auth.currentUser != null)
            database.getReference("users/${auth.currentUser?.uid}/profile").addListenerForSingleValueEvent(object:
                ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()

                    val sex = snapshot.child("sex").value.toString()
                    val realName = snapshot.child("real_name").value.toString()
                    val lastName = snapshot.child("last_name").value.toString()
                    val birthday = snapshot.child("birthday").value.toString()
                    var birthdayDateTime = LocalDate.parse(birthday)

                    var age = Period.between(birthdayDateTime, LocalDate.now()).years


                    user = User(auth.currentUser!!.uid,name, age.toInt(), sex, "", "", "", realName, lastName)
                    currentUser.value = user
                    mutDate.value = birthdayDateTime
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )
    }

    fun setData(realName: String, lastName: String, localDate: String, sex: Int){
        database.getReference("users/${auth.currentUser?.uid}/profile/real_name").setValue(realName)
        database.getReference("users/${auth.currentUser?.uid}/profile/last_name").setValue(lastName)
        if (sex == R.id.rbMale) {
            database.getReference("users/" + auth.currentUser?.uid + "/profile/sex")
                .setValue("male")
        } else {
            database.getReference("users/" + auth.currentUser?.uid + "/profile/sex")
                .setValue("female")
        }
        database.getReference("users/${auth.currentUser?.uid}/profile/birthday").setValue(localDate)
    }

    fun setValidValues(list: ArrayList<String>){
        uRealName = list[0]
        uLastName = list[1]
    }

    fun valid(): Boolean{
        if (uRealName.isEmpty()){
            realNameValid.value = App.getRes().getString(R.string.null_real_name)
            return false
        }

        if (uLastName.isEmpty()){
            lastNameValid.value = App.getRes().getString(R.string.null_last_name)
            return false
        }
        return true
    }

    fun clearValid(){
        realNameValid.value = ""
        lastNameValid.value = ""
    }
}