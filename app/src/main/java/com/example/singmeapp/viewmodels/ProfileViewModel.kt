package com.example.singmeapp.viewmodels

import android.os.Build
import android.os.StrictMode
import android.util.Log
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

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        getData()
    }

    fun getData(){
        if (auth.currentUser != null)
        database.getReference("users/"+auth.currentUser?.uid + "/profile").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imagePath = mService.getFile("storage/users/${auth.currentUser?.uid}/profile/avatar.jpg", authToken).execute().body()?.public_url
                val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                currentUser.value = User(snapshot.child("name").value.toString(), 21, "M", imageUrl )
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        )
    }


}