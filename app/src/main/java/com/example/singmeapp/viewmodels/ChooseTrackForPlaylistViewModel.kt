package com.example.singmeapp.viewmodels

import android.os.Build
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

class ChooseTrackForPlaylistViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var playlistUuid = MutableLiveData<String>()

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack = MutableLiveData<List<Track>>()
    lateinit var arrayListTrack: ArrayList<Track>
    var url: String = "/users/Vtkal2hD2uRkpWBJfigYnvShhJu1/library/love_tracks"

    var isExist = MutableLiveData<Boolean>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())


    fun setTrackToPlaylist(trackUuid: String){
        database.reference.child("albums/${playlistUuid.value}/tracks").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                var tracksUuids = ArrayList<String>()
                snapshot.children.forEach {
                    tracksUuids.add(it.value.toString())
                }
                if (!tracksUuids.contains(trackUuid)) {
                    database.reference.child("albums/${playlistUuid.value}/tracks/${count}")
                        .setValue(trackUuid)
                    isExist.value = false
                } else {
                    isExist.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getTracks() {
        var fbTrackUrl: String
        var fbImageUrl: String

        var fbTrackUrls = HashMap<String, String>()
        var fbImageUrls = HashMap<String, String>()

        if (auth.currentUser != null)
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                var count = 0
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {

                    arrayListTrack  = ArrayList<Track>()
                    listTrack.value = arrayListTrack
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").children.forEach(
                        Consumer {

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

                            arrayListTrack.add(track)
                        })

                    if (arrayListTrack.size != 0) {
                        arrayListTrack.reverse()
                        listTrack.value = arrayListTrack
                        arrayListTrack.forEach {
                            getFilePath(fbImageUrls[it.uuid]!!, "image", count)
                            getFilePath(fbTrackUrls[it.uuid]!!, "track", count)
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
            }
            "track" -> {
                arrayListTrack[index.toInt()].trackUrl = url
                listTrack.value = arrayListTrack
            }
        }

        if (index == arrayListTrack.size -1 && value == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

    }
}