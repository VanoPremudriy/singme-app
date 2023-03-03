package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
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

class CatalogueViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listTrack = MutableLiveData<HashMap<String, Track>>()
    var arrayListTrack = HashMap<String, Track>()

    var listAlbum = MutableLiveData<HashMap<String, Album>>()
    var arrayListAlbum = HashMap<String, Album>()

    var listBand = MutableLiveData<HashMap<String, Band>>()
    var arrayListBand = HashMap<String, Band>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun getTracks(search: String){
        var fbTrackUrl = ""
        var fbTrackImageUrl = ""
        var count = 0
        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListTrack.clear()
                listTrack.value = arrayListTrack
                if (search != "")
                snapshot.child("tracks").children.forEach { t ->
                    val trackName = t.child("name").value.toString()
                    if (trackName.lowercase().contains(search.lowercase())) {
                        val trackAlbumUuid = t.child("album").value.toString()
                        val trackAlbumName =
                            snapshot.child("/albums/${trackAlbumUuid}/name").value.toString()
                        val bandUuid = t.child("band").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val extension =
                            snapshot.child("/albums/${trackAlbumUuid}/cover").value.toString()
                        val date = t.child("created_at").value.toString()
                        var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.parse(date)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }

                        var isInLove = false
                        snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                            if (it1.value.toString() == t.key.toString()) isInLove = true
                        }

                        fbTrackUrl =
                            "/storage/bands/${bandName}/albums/${trackAlbumName}/${trackName}.mp3"
                        fbTrackImageUrl =
                            "/storage/bands/${bandName}/albums/${trackAlbumName}/cover.${extension}"
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
                        arrayListTrack.put(t.key.toString(), track)
                    }
                }
                if (arrayListTrack.size != 0) {
                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbTrackUrls.get(it.key)!!, "track", it.key, count)
                        getFilePath(fbTrackImageUrls.get(it.key)!!, "trackImage", it.key, count)
                        count++
                    }
                } else {
                    isAlready.value?.put("track", true)
                    isAlready.value?.put("trackImage", true)
                    isAlready.value = isAlready.value
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    fun getAlbums(search: String){
        var fbAlbumImageUrl: String
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbAlbumImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListAlbum.clear()
                listAlbum.value = arrayListAlbum
                if (search != "")
                snapshot.child("albums").children.forEach{ t ->
                    val albumName = t.child("name").value.toString()
                    if (albumName.lowercase().contains(search.lowercase())){
                    val format = t.child("/format").value.toString()
                    if (format == "Album" || format == "Single/EP"){
                        val bandUuid = t.child("band").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val year = t.child("year").value?.toString()?.toInt()
                        val extension = t.child("cover").value.toString()
                        val date = t.child("created_at").value.toString()
                        if (albumName != "null" && year != null && extension != "null" && format != "null") {

                            val listeningCounter = t.child("listening_counter").value.toString()
                            if (listeningCounter != "null") {
                                listeningCounters.put(t.key.toString(), listeningCounter.toInt())
                            } else {
                                listeningCounters.put(t.key.toString(), 0)
                            }

                            var localDateTime =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                            arrayListAlbum.put(t.key.toString(), album)
                        }

                        }
                    }
                }

                /*arrayListAlbum.sortBy { album -> listeningCounters.get(album.uuid) }
                arrayListAlbum = arrayListAlbum.reversed() as ArrayList<Album> *//* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                if (arrayListAlbum.size != 0) {
                    listAlbum.value = arrayListAlbum
                    arrayListAlbum.forEach {
                        getFilePath(fbAlbumImageUrls.get(it.key)!!, "albumImage", it.key, count)
                        count++
                    }
                } else {
                    isAlready.value?.put("albumImage", true)
                    isAlready.value = isAlready.value
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun getBands(search: String){
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
                    if (search != "")
                    snapshot.child("bands").children.forEach{ t ->
                        val bandName = t.child("name").value.toString()
                        if (bandName.lowercase().contains(search.lowercase())) {
                            var extension = t.child("avatar").value.toString()
                            var info = t.child("info").value.toString()
                            var backExtension = t.child("background").value.toString()

                            val date = t.child("created_at").value.toString()

                            var localDateTime =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    LocalDateTime.parse(date)
                                } else {
                                    TODO("VERSION.SDK_INT < O")
                                }

                            fbBandImageUrl =
                                "/storage/bands/${bandName}/profile/avatar.${extension}"
                            fbBandBackUrl =
                                "/storage/bands/${bandName}/profile/back.${backExtension}"

                            fbBandBackUrls.put(t.key.toString(), fbBandBackUrl)
                            fbBandImageUrls.put(t.key.toString(), fbBandImageUrl)

                            val band = Band(
                                t.key.toString(),
                                bandName,
                                info,
                                "",
                                "",
                                localDateTime
                            )

                            arrayListBand.put(t.key.toString(), band)
                        }

                    }

                    if (arrayListBand.size != 0) {
                        listBand.value = arrayListBand
                        arrayListBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.key)!!, "bandImage", it.key, count)
                            getFilePath(fbBandBackUrls.get(it.key)!!, "bandBack", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("bandImage", true)
                        isAlready.value?.put("bandBack", true)
                        isAlready.value = isAlready.value
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getFilePath(url: String, value: String, index: String, count: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
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
                    if (response.body() != null) {
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
            "track" -> {
                if (arrayListTrack[index]?.trackUrl == "") {
                    arrayListTrack[index]?.trackUrl = url
                    listTrack.value = arrayListTrack
                }
            }
            "trackImage" ->{
                if (arrayListTrack[index]?.imageUrl == "") {
                    arrayListTrack[index]?.imageUrl = url
                    listTrack.value = arrayListTrack
                }
            }

            "albumImage" -> {
                if (arrayListAlbum[index]?.imageUrl == "") {
                    arrayListAlbum[index]?.imageUrl = url
                    listAlbum.value = arrayListAlbum
                }
            }

            "bandImage" -> {
                arrayListBand[index]?.imageUrl = url
                listBand.value = arrayListBand
            }

            "bandBack" -> {
                arrayListBand[index]?.backgroundUrl = url
                listBand.value = arrayListBand
            }
        }

        if (count == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

        if (count == arrayListTrack.size -1 && value == "trackImage"){
            isAlready.value?.put("trackImage", true)
            isAlready.value = isAlready.value
        }

        if (count ==  arrayListAlbum.size -1 && value == "albumImage"){
            isAlready.value?.put("albumImage", true)
            isAlready.value = isAlready.value
        }

        if (count == arrayListBand.size -1 && value == "bandImage"){
            isAlready.value?.put("bandImage", true)
            isAlready.value = isAlready.value
        }

        if (count == arrayListBand.size -1 && value == "bandBack"){
            isAlready.value?.put("bandBack", true)
            isAlready.value = isAlready.value
        }
    }
}