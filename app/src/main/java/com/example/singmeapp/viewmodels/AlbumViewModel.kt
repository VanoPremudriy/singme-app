package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
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
import java.util.function.Consumer

class AlbumViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack: MutableLiveData<List<Track>> = MutableLiveData<List<Track>>()
    lateinit var arrayListTrack: ArrayList<Track>

    val currentAlbum = MutableLiveData<Album>()
    lateinit var album: Album

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getAlbumData(albumUuid: String){
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val albumName = snapshot.child("albums/${albumUuid}/name").value.toString()
                val albumBandUuid  = snapshot.child("albums/${albumUuid}/band").value.toString()
                val albumBandName = snapshot.child("bands/${albumBandUuid}/name").value.toString()
                val albumYear = snapshot.child("albums/${albumUuid}/year").value.toString().toInt()
                val albumCoverExtension = snapshot.child("albums/${albumUuid}/cover").value.toString()

                val fbAlbumCover = "storage/bands/${albumBandName}/albums/${albumName}/cover.${albumCoverExtension}"

                album = Album(
                    albumUuid,
                    albumName,
                    albumBandName,
                    albumYear,
                    ""
                )

                currentAlbum.value = album
                getFilePath(fbAlbumCover, "albumCover", -1)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getTracks(albumUuid: String){
        var fbTrackUrl: String
        var fbImageUrl: String
            database.reference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    arrayListTrack = ArrayList<Track>()
                    listTrack.value = arrayListTrack
                    snapshot.child("/albums/${albumUuid}/tracks").children.forEach(Consumer { t ->

                        val trackName = snapshot.child("/tracks/${t.value}/name").value.toString()
                        val bandUuuid = snapshot.child("albums/${albumUuid}/band").value.toString()
                        val bandName = snapshot.child("/bands/${bandUuuid}/name").value.toString()
                        val albumName = snapshot.child("/albums/${albumUuid}/name").value.toString()

                        val albumCoverExtension = snapshot.child("/albums/${albumUuid}/cover").value.toString()

                        fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${albumCoverExtension}"
                        fbTrackUrl = "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"


                        val track = Track(
                            t.value.toString(),
                            trackName,
                            bandName,
                            albumName,
                            bandUuuid,
                            albumUuid,
                            "",
                            "",
                            false
                        )

                        arrayListTrack.add(track)
                        listTrack.value = arrayListTrack
                        getFilePath(fbTrackUrl, "track", count)
                        getFilePath(fbImageUrl, "trackImage", count)
                        count++
                    })

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

            "trackImage" -> {
                arrayListTrack[index].imageUrl = url
                listTrack.value = arrayListTrack
            }

            "albumCover" ->{
                album.imageUrl = url
                currentAlbum.value = album
            }
        }
    }
}