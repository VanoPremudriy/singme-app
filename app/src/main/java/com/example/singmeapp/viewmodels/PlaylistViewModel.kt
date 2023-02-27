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
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class PlaylistViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack: MutableLiveData<List<Track>> = MutableLiveData<List<Track>>()
    var arrayListTrack = ArrayList<Track>()
    val curPlaylist = MutableLiveData<Album>()
    lateinit var playlist: Album

    var isUserPlaylistsChanged = MutableLiveData<Boolean>(false)

    fun getPlaylistData(playlistUuid: String){
        database.reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlistName = snapshot.child("albums/${playlistUuid}/name").value.toString()
                val playlistAuthorUuid = snapshot.child("albums/${playlistUuid}/band").value.toString()
                val playlistAuthorName = snapshot.child("users/${playlistAuthorUuid}/profile/name").value.toString()
                val playlistYear = snapshot.child("albums/${playlistUuid}/year").value.toString().toInt()
                val playlistCoverExtension = snapshot.child("albums/${playlistUuid}/cover").value.toString()

                val fbPlaylistCover = "storage/users/${playlistAuthorUuid}/playlists/${playlistName}/cover.${playlistCoverExtension}"

                var isInLove = false
                snapshot.child("users/${auth.currentUser?.uid}/library/playlists").children.forEach { it1 ->
                    if (it1.value.toString() == playlistUuid) isInLove = true
                }
                val isAuthor = playlistAuthorUuid == auth.currentUser?.uid.toString()

                playlist = Album(
                    playlistUuid,
                    playlistName,
                    playlistAuthorName,
                    playlistYear,
                    isInLove,
                    isAuthor,
                    ""
                )

                curPlaylist.value = playlist
                getFilePath(fbPlaylistCover, "playlistCover", -1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getTracks(playlistUuid: String){
        var fbTrackUrl: String
        var fbImageUrl: String
        database.reference.addListenerForSingleValueEvent(object: ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                arrayListTrack.clear()
                listTrack.value = arrayListTrack
                if (snapshot.child("albums/${playlistUuid}/tracks").childrenCount > 0)
                snapshot.child("albums/${playlistUuid}/tracks").children.forEach(Consumer { t->
                        val trackName = snapshot.child("/tracks/${t.value}/name").value.toString()
                        val bandUuid = snapshot.child("/tracks/${t.value}/band").value.toString()
                        val albumUuid = snapshot.child("/tracks/${t.value}/album").value.toString()
                        val bandName = snapshot.child("/bands/${bandUuid}/name").value.toString()
                        val albumName = snapshot.child("/albums/${albumUuid}/name").value.toString()

                        val albumCoverExtension = snapshot.child("/albums/${albumUuid}/cover").value.toString()

                        fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${albumCoverExtension}"
                        fbTrackUrl = "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"

                        var isInLove = false
                        snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                            if (it1.value.toString() == t.value.toString()) isInLove = true
                        }


                        val track = Track(
                            t.value.toString(),
                            trackName,
                            bandName,
                            albumName,
                            bandUuid,
                            albumUuid,
                            "",
                            "",
                            isInLove
                        )

                        arrayListTrack.add(track)
                        listTrack.value = arrayListTrack
                        Log.e("URL", fbImageUrl)
                        Log.e("URL", fbTrackUrl)
                        getFilePath(fbTrackUrl, "track", count)
                        getFilePath(fbImageUrl, "trackImage", count)
                        count++

                    }
                )
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun deletePlaylist(playlistUuid: String){
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val usersHavePLaylistUuids = ArrayList<String>()
                val playlistName = snapshot.child("albums/${playlistUuid}/name").value.toString()

                database.reference.child("albums/${playlistUuid}").setValue(null)
                database.reference.child("album_exist/${auth.currentUser?.uid}/${playlistName}").setValue(null)

                snapshot.child("users").children.forEachIndexed {index,  user ->
                    user.child("library/playlists").children.forEach{
                        usersHavePLaylistUuids.add(it.value.toString())
                    }
                    if (usersHavePLaylistUuids.remove(playlistUuid)){
                        database.reference.child("users/${user.key}/library/playlists").setValue(usersHavePLaylistUuids)
                        isUserPlaylistsChanged.value = !isUserPlaylistsChanged.value!!
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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
                        Log.e("FileUrl", url)
                        Log.e("FileUrl", value)
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

            "playlistCover" ->{
                playlist.imageUrl = url
                curPlaylist.value = playlist
            }
        }
    }

    fun changeImage(file: RequestBody, extension: String, image: String){
        if (image == "cover") {
            getFileUrl("storage/users/${auth.currentUser?.uid}/playlists/${curPlaylist.value?.name}/cover.${extension}", file)
            database.reference.child("albums/${curPlaylist.value?.uuid}/cover").setValue(extension)
        }
    }

    fun deleteTrack(trackUuid: String){
        database.reference.child("albums/${curPlaylist.value?.uuid}/tracks").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                val tracksUuids = ArrayList<String>()
                snapshot.children.forEach {
                    tracksUuids.add(it.value.toString())
                }
                if (tracksUuids.remove(trackUuid)){
                    database.reference.child("albums/${curPlaylist.value?.uuid}/tracks").setValue(tracksUuids)
                    getTracks(curPlaylist.value!!.uuid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun getFileUrl(url: String, file: RequestBody){
        mService.getUrlForReUpload(url, "true", authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200 || response.code() == 409) {
                    mService.addFile(response.body()!!.href, file, "image/*", "*/*").enqueue(object: Callback<ResponseBody>{
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            publicFile(url)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })

                }
                else Log.e("Responce", "URL not Getted")
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

            }

        })
    }


    fun publicFile(url: String){
        mService.publishFile(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200){

                }
                else Log.e("CreateAlbum", response.code().toString())
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}