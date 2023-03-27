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
import java.util.TreeSet

class CataloguePopularViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listPopularTrack = MutableLiveData<List<Track>>()
    var arrayListPopularTrack = ArrayList<Track>()

    var listPopularAlbum = MutableLiveData<List<Album>>()
    var arrayListPopularAlbum = ArrayList<Album>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun getTracks(){
        var fbTrackUrl = ""
        var fbTrackImageUrl = ""
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("tracks").children.forEach{ t ->

                    val trackName = t.child("name").value.toString()
                    val trackAlbumUuid = t.child("album").value.toString()
                    val trackAlbumName = snapshot.child("/albums/${trackAlbumUuid}/name").value.toString()
                    val bandUuid = t.child("band").value.toString()
                    val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                    val extension = snapshot.child("/albums/${trackAlbumUuid}/cover").value.toString()
                    val date = t.child("created_at").value.toString()


                    var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.parse(date)
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }

                    val listeningCounter = t.child("listening_counter").value.toString()
                    if (listeningCounter != "null"){
                        listeningCounters.put(t.key.toString(), listeningCounter.toInt())
                    } else {
                        listeningCounters.put(t.key.toString(), 0)
                    }
                    //Log.e("listening counter", listeningCounters.get(t.key.toString()).toString())

                    var isInLove = false
                    snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                        if (it1.value.toString() == t.value.toString()) isInLove = true
                    }

                    fbTrackUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/${trackName}.mp3"
                    fbTrackImageUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/cover.${extension}"
                    fbTrackUrls.put(t.key.toString(), fbTrackUrl)
                    fbTrackImageUrls.put(t.key.toString(), fbTrackImageUrl)

                    val track = Track(
                        t.key.toString(),
                        trackName,
                        bandName,
                        trackAlbumName,
                        bandUuid,
                        trackAlbumUuid,
                        "",
                        "",
                        isInLove,
                        localDateTime
                    )


                    arrayListPopularTrack.add(track)
                }

                arrayListPopularTrack.sortBy { track -> listeningCounters.get(track.uuid) }
                arrayListPopularTrack = arrayListPopularTrack.reversed() as ArrayList<Track>
                if (arrayListPopularTrack.size > 12) arrayListPopularTrack = ArrayList(arrayListPopularTrack.subList(0, 12))
                listPopularTrack.value = arrayListPopularTrack
                arrayListPopularTrack.forEach {
                    getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                    getFilePath(fbTrackImageUrls.get(it.uuid)!!, "trackImage", count)
                    count++
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getAlbums(){
        var fbAlbumImageUrl: String
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbAlbumImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("albums").children.forEach{ t ->
                    val format = t.child("/format").value.toString()
                    if (format == "Album" || format == "Single/EP"){
                        val albumName = t.child("name").value.toString()
                        val bandUuid = t.child("band").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val year = t.child("year").value?.toString()?.toInt()
                        val extension = t.child("cover").value.toString()
                        val date = t.child("created_at").value.toString()
                        if (albumName != "null" && year != null && extension != "null" && format != "null") {

                            val listeningCounter = t.child("listening_counter").value.toString()
                            if (listeningCounter != "null"){
                                listeningCounters.put(t.key.toString(), listeningCounter.toInt())
                            } else {
                                listeningCounters.put(t.key.toString(), 0)
                            }

                            var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(date)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }

                            fbAlbumImageUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                            fbAlbumImageUrls.put(t.key.toString(), fbAlbumImageUrl)

                            var isInLove = false
                            snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                if (it1.value.toString() == t.value.toString()) isInLove = true
                            }

                            val isAuthor =
                                snapshot.child("bands_has_users/${bandUuid}/${auth.currentUser?.uid}").value != null

                            val album = Album(
                                t.key.toString(),
                                albumName,
                                bandName,
                                year,
                                isInLove,
                                isAuthor,
                                "",
                                localDateTime
                            )
                            arrayListPopularAlbum.add(album)

                        }
                    }
                }

                arrayListPopularAlbum.sortBy { album -> listeningCounters.get(album.uuid) }
                arrayListPopularAlbum = arrayListPopularAlbum.reversed() as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                if (arrayListPopularAlbum.size  > 12) arrayListPopularAlbum = ArrayList(arrayListPopularAlbum.subList(0, 12))
                listPopularAlbum.value = arrayListPopularAlbum
                arrayListPopularAlbum.forEach {
                    getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "albumImage", count)
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
                    if (response.body() != null) {
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
            "track" -> {
                arrayListPopularTrack[index].trackUrl = url
                listPopularTrack.value = arrayListPopularTrack
            }
            "trackImage" ->{
                arrayListPopularTrack[index].imageUrl = url
                listPopularTrack.value = arrayListPopularTrack
            }
            "albumImage" -> {
                arrayListPopularAlbum[index].imageUrl = url
                listPopularAlbum.value = arrayListPopularAlbum
            }

        }
        if (index == arrayListPopularTrack.size - 1 && value == "track"){
            Log.e("Done", "track")
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListPopularTrack.size -1 && value == "trackImage"){
            Log.e("Done", "trackImage")
            isAlready.value?.put("trackImage", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListPopularAlbum.size -1 && value == "albumImage"){
            Log.e("Done", "album")
            isAlready.value?.put("albumImage", true)
            isAlready.value = isAlready.value
        }
    }
}