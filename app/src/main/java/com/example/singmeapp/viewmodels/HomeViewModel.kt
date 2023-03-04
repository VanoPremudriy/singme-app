package com.example.singmeapp.viewmodels

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Post
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

class HomeViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var arrayListPosts = ArrayList<Post>()
    val listPosts = MutableLiveData<List<Post>>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())


    fun getPosts(){
        var fbAlbumCover: String
        var fbBandAvatar: String
        var fbAlbumCovers = HashMap<String, String>()
        var fbBandAvatars = HashMap<String, String>()
        var count = 0
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val loveBandsUuids = ArrayList<String>()
                snapshot.child("users/${auth.currentUser?.uid}/library/love_bands").children.forEach {
                    loveBandsUuids.add(it.value.toString())
                }

                snapshot.child("posts").children.forEach {
                    val bandUuid = it.child("band").value.toString()
                    if (loveBandsUuids.contains(bandUuid)){
                        val postUuid = it.key.toString()
                        val albumUuid = it.child("album").value.toString()
                        val albumName = snapshot.child("albums/${albumUuid}/name").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val date = it.child("created_at").value.toString()
                        val bandAvatarExtension = snapshot.child("bands/${bandUuid}/avatar").value.toString()
                        val albumCoverExtension = snapshot.child("albums/${albumUuid}/cover").value.toString()

                        fbAlbumCover = "storage/bands/${bandName}/albums/${albumName}/cover.${albumCoverExtension}"
                        fbBandAvatar = "/storage/bands/${bandName}/profile/avatar.${bandAvatarExtension}"

                        fbBandAvatars.put(it.key.toString(), fbBandAvatar)
                        fbAlbumCovers.put(it.key.toString(), fbAlbumCover)

                        var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.parse(date)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }

                        val album = Album(
                            albumUuid,
                            albumName,
                            bandName,
                            -1,
                            false,
                            false,
                            "",
                        )

                        val band = Band(
                            bandUuid,
                            bandName,
                            "",
                            "",
                            ""
                        )

                        arrayListPosts.add(Post(postUuid, band, album, localDateTime))

                    }
                }

                if(arrayListPosts.size != 0){
                    arrayListPosts.sortBy { post -> post.dateTime }
                    arrayListPosts.reverse()
                    listPosts.value = arrayListPosts
                    arrayListPosts.forEach{
                        getFilePath(fbAlbumCovers[it.uuid.toString()]!!, "albumCover", count)
                        getFilePath(fbBandAvatars[it.uuid.toString()]!!, "bandAvatar", count)
                        count++
                    }
                }
                else {
                    isAlready.value?.put("bandAvatar", true)
                    isAlready.value?.put("albumCover", true)
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
                    Log.e("Track", url)
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
            "bandAvatar" -> {
                arrayListPosts[index].band.imageUrl= url
                listPosts.value = arrayListPosts
            }
            "albumCover" -> {
                arrayListPosts[index].album.imageUrl = url
                listPosts.value = arrayListPosts
            }
        }

        if (index == arrayListPosts.size -1 && value == "bandAvatar"){
            isAlready.value?.put("bandAvatar", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListPosts.size -1 && value == "albumCover"){
            isAlready.value?.put("albumCover", true)
            isAlready.value = isAlready.value
        }
    }


}