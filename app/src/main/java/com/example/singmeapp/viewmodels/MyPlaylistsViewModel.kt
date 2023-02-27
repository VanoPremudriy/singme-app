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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyPlaylistsViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listPlaylists = MutableLiveData<List<Album>>()
    val arrayListPlaylists = ArrayList<Album>()

    var isExist = MutableLiveData<Boolean>()

    lateinit var playlistName: String
    lateinit var playlistUuid: String

    fun isPlaylistExist(playlistName: String){
        this.playlistName = playlistName
        database.reference.child("playlist_exist/${auth.currentUser?.uid}").addListenerForSingleValueEvent(object: ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(playlistName).exists()){
                    isExist.value = true
                } else {
                    createPlaylist()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createPlaylist(){
        playlistUuid = UUID.randomUUID().toString()
        val datetime = LocalDateTime.now()
        database.reference.child("album_exist/${auth.currentUser?.uid}/${playlistName}").setValue(playlistUuid)
        database.reference.child("albums/${playlistUuid}/band").setValue(auth.currentUser?.uid)
        database.reference.child("albums/${playlistUuid}/format").setValue("Playlist")
        database.reference.child("albums/${playlistUuid}/name").setValue(playlistName)
        database.reference.child("/albums/${playlistUuid}/year").setValue(datetime.year.toString())
        database.reference.child("/albums/${playlistUuid}/created_at").setValue(datetime.toString())

        database.reference.child("users/${auth.currentUser?.uid}/library/playlists").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                database.reference.child("users/${auth.currentUser?.uid}/library/playlists/${count}").setValue(playlistUuid)
                getPlaylists()
                createDir("/storage/users/${auth.currentUser?.uid}/playlists", playlistName)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getPlaylists(userUuid: String? = null){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListPlaylists.clear()
                    listPlaylists.value = arrayListPlaylists
                    snapshot.child("/users/${userUuid?: auth.currentUser?.uid}/library/playlists").children.forEach { t ->
                            val authorUuid = snapshot.child("/albums/${t.value}").child("band").value.toString()
                            val authorName = snapshot.child("/users/${authorUuid}/profile/name").value.toString()
                            val playlistsName = snapshot.child("/albums/${t.value}").child("name").value.toString()
                            val year = snapshot.child("/albums/${t.value}").child("year").value.toString()
                            val extension = snapshot.child("/albums/${t.value}").child("cover").value.toString()

                            if (authorUuid != null
                                && authorName != null
                                && playlistsName!= null
                                && year != null
                                && authorUuid != "null"
                                && authorName != "null"
                                && playlistsName!= "null"
                                && year != "null") {


                                fbAlbumImageUrl =
                                    "/storage/users/${userUuid ?: auth.currentUser?.uid}/playlists/${playlistsName}/cover.${extension}"
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
                                    year.toInt(),
                                    isInLove,
                                    isAuthor,
                                    "",
                                )

                                arrayListPlaylists.add(album)
                            }
                        }

                    listPlaylists.value = arrayListPlaylists
                    arrayListPlaylists.forEach {
                        getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "image", count)
                        Log.e("Image", fbAlbumImageUrls.get(it.uuid).toString())
                        count++
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
    }

    fun createDir(url:String, name:String){
        mService.createDir(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 201 || response.code() == 409) {
                    Log.e("Responce", "Dir Created")
                    createAlbumDir("${url}/${name}")
                }
                else Log.e("Responce", "Dir Not Created")

            }

            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun createAlbumDir(url:String){
        mService.createDir(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 201 || response.code() == 409) {
                    Log.e("Responce", "Dir Created")
                }
                else Log.e("Responce", "Dir Not Created")

            }

            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}