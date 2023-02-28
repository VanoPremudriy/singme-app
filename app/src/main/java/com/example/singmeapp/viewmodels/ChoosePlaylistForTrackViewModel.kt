package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Album
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

class ChoosePlaylistForTrackViewModel: ViewModel() {

    val trackUuid = MutableLiveData<String>()

    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listPlaylists = MutableLiveData<List<Album>>()
    val arrayListPlaylists = ArrayList<Album>()

    var isExist = MutableLiveData<Boolean>()

    lateinit var playlistName: String
    lateinit var playlistUuid: String

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun setTrackToPlaylist(playlistUuid: String){
        database.reference.child("albums/${playlistUuid}/tracks").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                val tracksUuids = ArrayList<String>()
                snapshot.children.forEach {
                    tracksUuids.add(it.value.toString())
                }

                if (!tracksUuids.contains(trackUuid.value)) {
                    database.reference.child("albums/${playlistUuid}/tracks/${count}")
                        .setValue(trackUuid.value)
                    isExist.value = false
                } else isExist.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getPlaylists(){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    arrayListPlaylists.clear()
                    listPlaylists.value = arrayListPlaylists
                    snapshot.child("/users/${auth.currentUser?.uid}/library/playlists").children.forEach(
                        Consumer { t ->
                            val authorUuid = snapshot.child("/albums/${t.value}").child("band").value.toString()
                            if (authorUuid == auth.currentUser?.uid) {
                                val authorName =
                                    snapshot.child("/users/${authorUuid}/profile/name").value.toString()
                                val playlistsName = snapshot.child("/albums/${t.value}")
                                    .child("name").value.toString()
                                val year = snapshot.child("/albums/${t.value}")
                                    .child("year").value.toString().toInt()
                                val extension = snapshot.child("/albums/${t.value}")
                                    .child("cover").value.toString()

                                if (extension != "null") {
                                    fbAlbumImageUrl =
                                        "/storage/users/${auth.currentUser?.uid}/playlists/${playlistsName}/cover.${extension}"
                                } else {
                                    fbAlbumImageUrl = "/storage/default_images/cover.png"
                                }

                                fbAlbumImageUrls.put(t.value.toString(), fbAlbumImageUrl)

                                var isInLove = false
                                snapshot.child("users/${auth.currentUser?.uid}/library/playlists").children.forEach { it1 ->
                                    if (it1.value.toString() == t.value.toString()) isInLove = true
                                }

                                val isAuthor = authorUuid == auth.currentUser?.uid.toString()
                                val album = Album(
                                    t.value.toString(),
                                    playlistsName,
                                    authorName,
                                    year,
                                    isInLove,
                                    isAuthor,
                                    "",
                                )

                                arrayListPlaylists.add(album)

                            }

                            if (arrayListPlaylists.size != 0) {
                                listPlaylists.value = arrayListPlaylists
                                arrayListPlaylists.forEach {
                                    getFilePath(fbAlbumImageUrls[it.uuid]!!, "image", count)
                                    count++
                                }
                            } else {
                                isAlready.value?.put("image", true)
                                isAlready.value = isAlready.value
                            }
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
            "image" -> {
                arrayListPlaylists[index].imageUrl= url
                listPlaylists.value = arrayListPlaylists
            }
        }

        if (index == arrayListPlaylists.size -1 && value  == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }

    }
}