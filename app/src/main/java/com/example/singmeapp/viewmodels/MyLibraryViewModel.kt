package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
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
import com.example.singmeapp.items.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.function.Consumer


class MyLibraryViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listTrack = MutableLiveData<HashMap<String, Track>>()
    var arrayListTrack =  HashMap<String, Track>()



    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())



    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }



    fun getTracks() {
        var fbTrackUrl: String
        var fbImageUrl: String
        var fbTrackUrls = HashMap<String, String>()
        var fbImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null)
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    //snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").childrenCount - 1
                    arrayListTrack.clear()
                    listTrack.value = arrayListTrack
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").children.forEach{
                            val band =
                                snapshot.child("/tracks/${it.value}").child("band").value.toString()
                            val album = snapshot.child("/tracks/${it.value}")
                                .child("album").value.toString()

                            val trackName =
                                snapshot.child("/tracks/${it.value}").child("name").value.toString()
                            val bandName =
                                snapshot.child("/bands/${band}").child("name").value.toString()
                            val albumName =
                                snapshot.child("/albums/${album}").child("name").value.toString()
                            val extension = snapshot.child("/albums/${album}").child("cover").value.toString()
                            Log.e("sv", extension)
                            var isTrackInLove = false

                        val date = snapshot.child("/tracks/${it.value}/created_at").value.toString()
                        var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.parse(date)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }

                            snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach(
                                Consumer { it1 ->
                                    if (it1.value.toString() == it1.value.toString()) isTrackInLove = true
                                })


                            fbTrackUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"
                            fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"

                            fbTrackUrls.put(it.value.toString(), fbTrackUrl)
                            fbImageUrls.put(it.value.toString(), fbImageUrl)


                            val track = Track(
                                it.value.toString(),
                                trackName,
                                bandName,
                                albumName,
                                band,
                                album,
                                "",
                                "",
                                isTrackInLove,
                                date = localDateTime
                            )
                            Log.e("Is In Love", isTrackInLove.toString())

                            //arrayListTrack.add(0,track)
                        arrayListTrack.put(it.value.toString(), track)

                        }

                    if (arrayListTrack.size != 0) {
                        listTrack.value = arrayListTrack
                        arrayListTrack.forEach {
                            getFilePath(fbImageUrls.get(it.key)!!, "image", it.key, count)
                            getFilePath(fbTrackUrls.get(it.key)!!, "track", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("image", true)
                        isAlready.value?.put("track", true)
                        isAlready.value = isAlready.value
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }




    fun getFilePath(url: String, value: String,index: String, count: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
                    Log.e("Track", "Three")
                    if (response.body() != null) {
                        val filePath = (response.body() as FileApiModel).public_url
                        getFileUrl(filePath, value, index, count)
                    }
                }

                override fun onFailure(call: Call<FileApiModel>, t: Throwable) {

                }

            })
    }



    fun getFileUrl(url: String, value: String, index: String, count: Int){
        mService.getSecondFile(url, authToken)
            .enqueue(object : Callback<SecondFileApiModel> {
                override fun onResponse(
                    call: Call<SecondFileApiModel>,
                    response: Response<SecondFileApiModel>
                ) {
                    if (response.body() != null){
                        val fileUrl = (response.body() as SecondFileApiModel).href
                        setList(fileUrl, value, index, count)
                    }
                }

                override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

                }

            })
    }

    fun setList(url: String, value: String, index: String, count: Int){
        when(value){
            "image" -> {
                arrayListTrack[index]?.imageUrl = url
                listTrack.value = arrayListTrack
            }
            "track" -> {
                arrayListTrack[index]?.trackUrl = url
                listTrack.value = arrayListTrack
            }

        }

        if (count == arrayListTrack.size -1 && value == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }
        if (count == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

    }

}