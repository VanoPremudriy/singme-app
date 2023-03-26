package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ProfileViewModel: ViewModel() {
    val currentUser = MutableLiveData<User>()
    var auth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    lateinit var user: User

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getData(){
        var fbAvatar: String
        if (auth.currentUser != null)
        database.getReference("users/${auth.currentUser?.uid}/profile").addListenerForSingleValueEvent(object:
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val extension = snapshot.child("avatar").value.toString()

                val sex = snapshot.child("sex").value.toString()
                val realName = snapshot.child("real_name").value.toString()
                val lastName = snapshot.child("last_name").value.toString()
                val birthday = snapshot.child("birthday").value.toString()
                var birthdayDateTime = LocalDate.parse(birthday)

                var age = Period.between(birthdayDateTime, LocalDate.now()).years
                //val imagePath = mService.getFile("storage/users/${auth.currentUser?.uid}/profile/avatar.jpg", authToken).execute().body()?.public_url
                //val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                if (extension != "null") {
                    fbAvatar = "storage/users/${auth.currentUser?.uid}/profile/avatar.${extension}"
                } else {
                    fbAvatar = "storage/default_images/cover.png"
                }
                user = User(auth.currentUser!!.uid,name, age.toInt(), sex, "", "", "", realName, lastName)
                currentUser.value = user
                getFilePath(fbAvatar, "avatar")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        )
    }

    @SuppressLint("SuspiciousIndentation")
    fun getOtherData(uuid: String){
        var fbAvatar: String
            database.getReference("users/${uuid}/profile").addListenerForSingleValueEvent(object:
                ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    val extension = snapshot.child("avatar").value.toString()
                    val sex = snapshot.child("sex").value.toString()
                    val realName = snapshot.child("real_name").value.toString()
                    val lastName = snapshot.child("last_name").value.toString()

                    val birthday = snapshot.child("birthday").value.toString()
                    var birthdayDateTime = LocalDate.parse(birthday)

                    var age = Period.between(birthdayDateTime, LocalDate.now()).years

                    //val imagePath = mService.getFile("storage/users/${auth.currentUser?.uid}/profile/avatar.jpg", authToken).execute().body()?.public_url
                    //val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                    fbAvatar = "storage/users/${uuid}/profile/avatar.${extension}"
                    user = User(uuid, name, age.toInt(), sex, "", "", "", realName, lastName)
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
        if (value == "avatar"){
            isAlready.value?.put("avatar", true)
            isAlready.value = isAlready.value
        }
    }


    fun changeImage(file: RequestBody, extension: String, image: String){
        if (image == "avatar") {
            getFileUrl("storage/users/${auth.currentUser?.uid}/profile/avatar.${extension}", file)
            database.reference.child("users/${auth.currentUser?.uid}/profile/avatar").setValue(extension)
        }
    }

    fun getFileUrl(url: String, file: RequestBody){
        mService.getUrlForReUpload(url, "true", authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200 || response.code() == 409) {
                    mService.addFile(response.body()!!.href, file, "image/*", "*/*").enqueue(object: Callback<ResponseBody>{
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            publicFile(url)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })

                }
                else Log.e("Responce", "URL not Getted")
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

            }

        })
    }


    fun publicFile(url: String){
        mService.publishFile(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200)
                    Log.e("CreateBand", "File public")
                else Log.e("CreateBand", response.code().toString())
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}