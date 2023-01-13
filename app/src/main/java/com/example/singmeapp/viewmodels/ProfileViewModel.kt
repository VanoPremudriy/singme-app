package com.example.singmeapp.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel: ViewModel() {
    val currentUser = MutableLiveData<User>()
    var auth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    fun getData(){
        if (auth.currentUser != null)
        database.getReference("users/"+auth.currentUser?.uid + "/profile").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imagePath = "storage/users/${auth.currentUser?.uid}"
                currentUser.value?.avatarUrl = "https://getfile.dokpub.com/yandex/get/${auth.currentUser?.uid}/avatar.jpg"
                currentUser.value?.name = snapshot.child("name").toString()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        )
    }


}