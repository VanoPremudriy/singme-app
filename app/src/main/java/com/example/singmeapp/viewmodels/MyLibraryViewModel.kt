package com.example.singmeapp.viewmodels

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
import java.util.function.Consumer


class MyLibraryViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack = MutableLiveData<List<Track>>()
    var arrayListTrack =  ArrayList<Track>()
    //var url: String = "/users/Vtkal2hD2uRkpWBJfigYnvShhJu1/library/love_tracks"

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
                                isTrackInLove
                            )
                            Log.e("Is In Love", isTrackInLove.toString())

                            //arrayListTrack.add(0,track)
                        arrayListTrack.add(track)

                        }

                    arrayListTrack.reverse()
                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbImageUrls.get(it.uuid)!!, "image", count)
                        getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                        count++
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    fun getFilePath(url: String, value: String, index: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
                    Log.e("Track", "Three")
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
                arrayListTrack[index.toInt()].imageUrl = url
                listTrack.value = arrayListTrack
                //(listTrack.value as ArrayList<Track>)[index].imageUrl = url//arrayListTrack
            }
            "track" -> {
                arrayListTrack[index.toInt()].trackUrl = url
                listTrack.value = arrayListTrack
                //(listTrack.value as ArrayList<Track>)[index].trackUrl = url
            }
        }

    }

}