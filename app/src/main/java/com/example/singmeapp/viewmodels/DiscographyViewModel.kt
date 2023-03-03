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
import com.example.singmeapp.items.Member
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

class DiscographyViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listTrack = MutableLiveData<List<Track>>()
    val arrayListTrack = ArrayList<Track>()

    var listAlbum = MutableLiveData<List<Album>>()
    var arrayListAlbum = ArrayList<Album>()

    var listSingle = MutableLiveData<List<Album>>()
    var arrayListSingle = ArrayList<Album>()

    var listMemberUuid = MutableLiveData<List<String>>()
    val arrayListMemberUuid = ArrayList<String>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())


    fun getMembers(currentBand: Band){
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("/bands_has_users/${currentBand.uuid}").children.forEach(
                        Consumer { t ->
                            arrayListMemberUuid.add(t.key.toString())
                            listMemberUuid.value = arrayListMemberUuid
                        })
                }
                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }



    fun getTracks(currentBand: Band){
        var fbTrackUrl: String
        var fbTrackImageUrl: String

        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()

        var count = 0
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("/bands/${currentBand.uuid}/tracks").children.forEach{ t ->

                    val trackName = snapshot.child("/tracks/${t.value}/name").value.toString()
                    val trackAlbum = snapshot.child("/tracks/${t.value}/album").value.toString()
                    val trackAlbumName = snapshot.child("/albums/${trackAlbum}/name").value.toString()
                    val extension = snapshot.child("/albums/${trackAlbum}/cover").value.toString()

                    var isInLove = false
                    snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                        if (it1.value.toString() == t.value.toString()) isInLove = true
                    }

                    fbTrackUrl = "/storage/bands/${currentBand.name}/albums/${trackAlbumName}/${trackName}.mp3"
                    fbTrackImageUrl = "/storage/bands/${currentBand.name}/albums/${trackAlbumName}/cover.${extension}"

                    fbTrackUrls.put(t.value.toString(), fbTrackUrl)
                    fbTrackImageUrls.put(t.value.toString(), fbTrackImageUrl)

                    val track = Track(
                        t.value.toString(),
                        trackName,
                        currentBand.name,
                        trackAlbumName,
                        currentBand.uuid,
                        trackAlbum,
                        "",
                        "",
                        isInLove
                    )

                    arrayListTrack.add(track)
                }

                if (arrayListTrack.size != 0) {
                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                        getFilePath(fbTrackImageUrls.get(it.uuid)!!, "trackImage", count)
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

    fun getAlbums(currentBand: Band){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        var albumCount = 0
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("/bands/${currentBand.uuid}/albums").children.forEach{ t ->
                    val format = snapshot.child("/albums/${t.value}/format").value.toString()
                    if (format == "Album"){
                        val albumName = snapshot.child("/albums/${t.value}/name").value.toString()
                        val year = snapshot.child("/albums/${t.value}/year").value?.toString()?.toInt()
                        val extension = snapshot.child("/albums/${t.value}/cover").value.toString()

                        if (albumName != null && year != null && extension != null && format != null) {
                            fbAlbumImageUrl =
                                "/storage/bands/${currentBand.name}/albums/${albumName}/cover.${extension}"
                            fbAlbumImageUrls.put(t.value.toString(), fbAlbumImageUrl)

                            var isInLove = false
                            snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                if (it1.value.toString() == t.value.toString()) isInLove = true
                            }

                            val isAuthor =
                                snapshot.child("bands_has_users/${currentBand.uuid}/${auth.currentUser?.uid}").value != null

                            val album = Album(
                                t.value.toString(),
                                albumName,
                                currentBand.name,
                                year,
                                isInLove,
                                isAuthor,
                                ""
                            )

                            arrayListAlbum.add(album)

                        }
                    }
                }

                if (arrayListAlbum.size != 0) {
                    listAlbum.value = arrayListAlbum
                    arrayListAlbum.forEach {
                        getFilePath(fbAlbumImageUrls[it.uuid]!!, "albumImage", albumCount)
                        albumCount++
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

    fun getSingles(currentBand: Band){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        var albumCount = 0
        var singleCount = 0
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("/bands/${currentBand.uuid}/albums").children.forEach{ t ->
                    val format = snapshot.child("/albums/${t.value}/format").value.toString()
                    if (format == "Single/EP"){
                        val albumName = snapshot.child("/albums/${t.value}/name").value.toString()
                        val year = snapshot.child("/albums/${t.value}/year").value?.toString()?.toInt()
                        val extension = snapshot.child("/albums/${t.value}/cover").value.toString()

                        if (albumName != null && year != null && extension != null && format != null) {
                            fbAlbumImageUrl =
                                "/storage/bands/${currentBand.name}/albums/${albumName}/cover.${extension}"
                            fbAlbumImageUrls.put(t.value.toString(), fbAlbumImageUrl)

                            var isInLove = false
                            snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                if (it1.value.toString() == t.value.toString()) isInLove = true
                            }

                            val isAuthor =
                                snapshot.child("bands_has_users/${currentBand.uuid}/${auth.currentUser?.uid}").value != null

                            val album = Album(
                                t.value.toString(),
                                albumName,
                                currentBand.name,
                                year,
                                isInLove,
                                isAuthor,
                                ""
                            )
                            arrayListSingle.add(album)
                        }
                    }
                }

                if (arrayListSingle.size != 0) {
                    listSingle.value = arrayListSingle
                    arrayListSingle.forEach {
                        getFilePath(fbAlbumImageUrls[it.uuid]!!, "singleImage", singleCount)
                        singleCount++
                    }
                }
                else {
                    isAlready.value?.put("singleImage", true)
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
                arrayListTrack[index].trackUrl = url
                listTrack.value = arrayListTrack
            }
            "trackImage" ->{
                arrayListTrack[index].imageUrl = url
                listTrack.value = arrayListTrack
            }
            "albumImage" -> {
                arrayListAlbum[index].imageUrl = url
                listAlbum.value = arrayListAlbum
            }
            "singleImage" -> {
                arrayListSingle[index].imageUrl = url
                listSingle.value = arrayListSingle
            }
        }
        if (index == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListTrack.size -1 && value == "trackImage"){
            isAlready.value?.put("trackImage", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListAlbum.size -1 && value == "albumImage"){
            isAlready.value?.put("albumImage", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListSingle.size -1 && value == "singleImage"){
            isAlready.value?.put("singleImage", true)
            isAlready.value = isAlready.value
        }
    }

}