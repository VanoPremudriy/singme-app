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
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.function.Consumer

class LoveAlbumsViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listAlbum = MutableLiveData<List<Album>>()
    val arrayListAlbum = ArrayList<Album>()
    var url: String = "tracks/user_tracks/${auth.currentUser?.uid}/albums"

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getAlbums(){
        if (auth.currentUser != null){
            database.getReference(url).addValueEventListener(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach(Consumer { t ->
                        val imagePath = mService.getFile("storage/bands/${t.child("band").value.toString()}/albums/${t.child("name").value.toString()}/cover.jpg", authToken).execute().body()?.public_url
                        val imageUrl = mService.getSecondFile(imagePath!!, authToken).execute().body()?.href.toString()
                        val album = Album(
                            t.child("name").value.toString(),
                            t.child("band").value.toString(),
                            t.child("year").value.toString().toInt(),
                            imageUrl,
                        )
                        arrayListAlbum.add(album)
                    })
                    listAlbum.value = arrayListAlbum
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
}