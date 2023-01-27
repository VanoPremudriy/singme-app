package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class LoveBandsViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listBand = MutableLiveData<List<Band>>()
    val arrayListBand = ArrayList<Band>()
    var url: String = "/users/${auth.currentUser?.uid}/library/love_bands"

    fun getBands(){
        var fbBandImageUrl = ""
        var fbBandBackUrl = ""
        var count = 0
        if (auth.currentUser != null){
            //if (user.)
            database.reference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_bands").children.forEach(
                        Consumer { t ->

                            val bandName = snapshot.child("/bands/${t.value}").child("name").value.toString()
                            var extension = snapshot.child("/bands/${t.value}").child("avatar").value.toString()
                            var info = snapshot.child("/bands/${t.value}").child("info").value.toString()
                            var backExtension = snapshot.child("/bands/${t.key}").child("background").value.toString()

                            fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                            fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                            val band = Band(
                                t.value.toString(),
                                bandName,
                                info,
                            "",
                                ""
                            )

                            arrayListBand.add(band)
                            listBand.value = arrayListBand
                            getFilePath(fbBandImageUrl, "image", count)
                            getFilePath(fbBandBackUrl, "back", count)
                            count++
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getBands(user: User){
        var fbBandImageUrl = ""
        var fbBandBackUrl = ""
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("/users_has_bands/${user.uuid}").children.forEach(
                        Consumer { t ->
                            Log.e("ds", t.toString())
                            val bandName = snapshot.child("/bands/${t.key}").child("name").value.toString()
                            var extension = snapshot.child("/bands/${t.key}").child("avatar").value.toString()
                            var info = snapshot.child("/bands/${t.key}").child("info").value.toString()
                            var backExtension = snapshot.child("/bands/${t.key}").child("background").value.toString()

                            fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                            fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                            val band = Band(
                                t.key.toString(),
                                bandName,
                                info,
                                "",
                            ""
                            )

                            arrayListBand.add(band)
                            listBand.value = arrayListBand
                            getFilePath(fbBandImageUrl, "image", count)
                            getFilePath(fbBandBackUrl, "back", count)
                            count++
                        })

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }



    fun getFilePath(url: String, value: String, index: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
                    Log.e("Track", "Three")
                    val filePath = (response.body() as FileApiModel).public_url
                    getFileUrl(filePath,value, index)
                }

                override fun onFailure(call: Call<FileApiModel>, t: Throwable) {

                }

            })
    }

    fun getFileUrl(url: String, value: String, index: Int){
        mService.getSecondFile(url, authToken)
            .enqueue(object : Callback<SecondFileApiModel> {
                override fun onResponse(
                    call: Call<SecondFileApiModel>,
                    response: Response<SecondFileApiModel>
                ) {
                    val fileUrl = (response.body() as SecondFileApiModel).href
                    setList(fileUrl, value, index)
                }

                override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

                }

            })
    }

    fun setList(url: String, value: String, index: Int){
        when(value){
            "image" -> {
                arrayListBand[index].imageUrl= url
                listBand.value = arrayListBand
            }
            "back" -> {
                arrayListBand[index].backgroundUrl = url
                listBand.value = arrayListBand
            }
        }
    }

}