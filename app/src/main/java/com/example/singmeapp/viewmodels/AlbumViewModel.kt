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

class AlbumViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    lateinit var currentAlbum:Album
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack = MutableLiveData<List<Track>>()
    val arrayListTrack = ArrayList<Track>()

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getTracks(currentAlbum:Album){
        val url = "tracks/band_tracks/${currentAlbum.band}/albums/${currentAlbum.name}"
        Log.e("URL", url)
            database.getReference(url).addValueEventListener(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach(Consumer { t ->
                        Log.e("URL2", "/storage/bands/${t.child("band").value.toString()}/albums/${t.child("album").value.toString()}/${t.child("name").value.toString()}.mp3")
                        val trackPath = mService.getFile("/storage/bands/${t.child("band").value.toString()}/albums/${t.child("album").value.toString()}/${t.child("name").value.toString()}.mp3",authToken).execute().body()?.public_url
                        //val imagePath = mService.getFile("storage/bands/${t.child("band").value.toString()}/albums/${t.child("album").value.toString()}/cover.jpg", authToken).execute().body()?.public_url
                        if (trackPath != null) {
                            Log.d("PATH", trackPath)
                        }
                        val track = Track(
                            t.child("name").value.toString(),
                            t.child("band").value.toString(),
                            t.child("album").value.toString(),
                            currentAlbum.imageUrl,
                            "https://getfile.dokpub.com/yandex/get/${trackPath}"
                        )
                        arrayListTrack.add(track)
                        Log.d("ViewModel", track.trackUrl)
                        Log.d("ViewModel", track.imageUrl)
                    })
                    listTrack.value = arrayListTrack
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}