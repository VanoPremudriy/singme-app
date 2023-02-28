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

class LoveAlbumsViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listAlbum = MutableLiveData<List<Album>>()
    val arrayListAlbum = ArrayList<Album>()

    var url: String = "tracks/user_tracks/${auth.currentUser?.uid}/albums"

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getAlbums(){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    arrayListAlbum.clear()
                    listAlbum.value = arrayListAlbum
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_albums").children.forEach{ t ->
                        val band = snapshot.child("/albums/${t.value}").child("band").value.toString()

                        val bandName = snapshot.child("/bands/${band}").child("name").value.toString()
                        val albumName = snapshot.child("/albums/${t.value}").child("name").value.toString()
                        val year = snapshot.child("/albums/${t.value}").child("year").value.toString().toInt()
                        val extension = snapshot.child("/albums/${t.value}").child("cover").value.toString()


                        fbAlbumImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                        fbAlbumImageUrls.put(t.value.toString(), fbAlbumImageUrl)

                        var isInLove = false
                        snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                            if (it1.value.toString() == t.value.toString()) isInLove = true
                        }

                        val isAuthor = snapshot.child("bands_has_users/${band}/${auth.currentUser?.uid}").value != null

                        val album = Album(
                            t.value.toString(),
                            albumName,
                            bandName,
                            year,
                            isInLove,
                            isAuthor,
                            "",
                        )

                        arrayListAlbum.add(album)

                    }

                    if (arrayListAlbum.size != 0) {
                        listAlbum.value = arrayListAlbum
                        arrayListAlbum.forEach {
                            getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "image", count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("image", true)
                        isAlready.value = isAlready.value
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
                    val filePath = (response.body() as FileApiModel).public_url
                    getFileUrl(filePath,value, index)
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
                    val fileUrl = (response.body() as SecondFileApiModel).href
                    setList(fileUrl, value, index)
                }

                override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

                }

            })
    }

    fun setList(url: String, value: String, index: Int){
        when(value){
            "image" -> {
                arrayListAlbum[index].imageUrl= url
                listAlbum.value = arrayListAlbum
            }
        }

        if (index == arrayListAlbum.size -1 && value == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }
    }
}