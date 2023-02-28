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

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun getBands(){
        var fbBandImageUrl: String
        var fbBandBackUrl: String
        var fbBandImageUrls = HashMap<String, String>()
        var fbBandBackUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListBand.clear()
                    listBand.value = arrayListBand
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_bands").children.forEach{ t ->

                        val bandName = snapshot.child("/bands/${t.value}").child("name").value.toString()
                        var extension = snapshot.child("/bands/${t.value}").child("avatar").value.toString()
                        var info = snapshot.child("/bands/${t.value}").child("info").value.toString()
                        var backExtension = snapshot.child("/bands/${t.value}").child("background").value.toString()

                        fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                        fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                        fbBandBackUrls.put(t.value.toString(), fbBandBackUrl)
                        fbBandImageUrls.put(t.value.toString(), fbBandImageUrl)

                        val band = Band(
                            t.value.toString(),
                            bandName,
                            info,
                        "",
                            ""
                        )

                        arrayListBand.add(band)

                    }

                    if (arrayListBand.size != 0) {
                        listBand.value = arrayListBand
                        arrayListBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.uuid)!!, "image", count)
                            getFilePath(fbBandBackUrls.get(it.uuid)!!, "back", count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("image", true)
                        isAlready.value?.put("back", true)
                        isAlready.value = isAlready.value
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getBands(user: User){
        var fbBandImageUrl: String
        var fbBandBackUrl: String
        var fbBandImageUrls = HashMap<String, String>()
        var fbBandBackUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("/users_has_bands/${user.uuid}").children.forEach{ t ->

                            val bandName = snapshot.child("/bands/${t.key}").child("name").value.toString()
                            var extension = snapshot.child("/bands/${t.key}").child("avatar").value.toString()
                            var info = snapshot.child("/bands/${t.key}").child("info").value.toString()
                            var backExtension = snapshot.child("/bands/${t.key}").child("background").value.toString()

                            fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                            fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                            fbBandBackUrls.put(t.key.toString(), fbBandBackUrl)
                            fbBandImageUrls.put(t.key.toString(), fbBandImageUrl)

                            val band = Band(
                                t.key.toString(),
                                bandName,
                                info,
                                "",
                            ""
                            )

                            arrayListBand.add(band)
                        }

                    if (arrayListBand.size != 0) {
                        listBand.value = arrayListBand
                        arrayListBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.uuid)!!, "image", count)
                            getFilePath(fbBandBackUrls.get(it.uuid)!!, "back", count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("image", true)
                        isAlready.value?.put("back", true)
                        isAlready.value = isAlready.value
                    }

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
                    Log.e("Track", url)
                    if (response.body() != null) {
                        val filePath = (response.body() as FileApiModel).public_url
                        getFileUrl(filePath, value, index)
                    }
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
                    if (response.body() != null){
                        val fileUrl = (response.body() as SecondFileApiModel).href
                        setList(fileUrl, value, index)
                    }
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

        if (index == arrayListBand.size -1 && value == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListBand.size -1 && value == "back"){
            isAlready.value?.put("back", true)
            isAlready.value = isAlready.value
        }
    }

}