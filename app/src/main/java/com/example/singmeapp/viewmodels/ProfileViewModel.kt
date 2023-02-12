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
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel: ViewModel() {
    val currentUser = MutableLiveData<User>()
    var auth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    lateinit var user: User

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getData(){
        var fbAvatar: String
        if (auth.currentUser != null)
        database.getReference("users/${auth.currentUser?.uid}/profile").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val extension = snapshot.child("avatar").value.toString()
                val age = snapshot.child("age").value.toString()
                val sex = snapshot.child("sex").value.toString()
                //val imagePath = mService.getFile("storage/users/${auth.currentUser?.uid}/profile/avatar.jpg", authToken).execute().body()?.public_url
                //val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                fbAvatar = "storage/users/${auth.currentUser?.uid}/profile/avatar.${extension}"
                user = User(auth.currentUser!!.uid,name, age.toInt(), sex, "", "", "")
                currentUser.value = user
                getFilePath(fbAvatar, "avatar")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        )
    }

    fun getOtherData(uuid: String){
        var fbAvatar: String
            database.getReference("users/${uuid}/profile").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    val extension = snapshot.child("avatar").value.toString()
                    val age = snapshot.child("age").value.toString()
                    val sex = snapshot.child("sex").value.toString()
                    //val imagePath = mService.getFile("storage/users/${auth.currentUser?.uid}/profile/avatar.jpg", authToken).execute().body()?.public_url
                    //val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                    fbAvatar = "storage/users/${uuid}/profile/avatar.${extension}"
                    user = User(uuid, name, age.toInt(), sex, "", "", "")
                    currentUser.value = user
                    getFilePath(fbAvatar, "avatar")
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }

            )
    }

    fun getFilePath(url: String, value: String){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
                    Log.e("Track", "Three")
                    val filePath = (response.body() as FileApiModel).public_url
                    getFileUrl(filePath,value)
                }

                override fun onFailure(call: Call<FileApiModel>, t: Throwable) {

                }

            })
    }

    fun getFileUrl(url: String, value: String){
        mService.getSecondFile(url, authToken)
            .enqueue(object : Callback<SecondFileApiModel> {
                override fun onResponse(
                    call: Call<SecondFileApiModel>,
                    response: Response<SecondFileApiModel>
                ) {
                    val fileUrl = (response.body() as SecondFileApiModel).href
                    setUser(fileUrl, value)
                }

                override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

                }

            })
    }

    fun setUser(url: String, value: String){
        when(value){
            "avatar" -> {
                Log.e("URL", url)
                user.avatarUrl = url
                currentUser.value = user
            }
        }
    }

}