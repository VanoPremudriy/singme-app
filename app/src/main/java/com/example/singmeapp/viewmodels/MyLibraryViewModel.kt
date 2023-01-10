package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.items.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.function.Consumer

class MyLibraryViewModel: ViewModel() {
    private val firebaseSongUrl = "https://firebasestorage.googleapis.com/v0/b/singmedb.appspot.com/o/"
    private val firebaseImageUrl = "gs://singmedb.appspot.com/"
    private val token = "?alt=media&token=0cae4f78-6eb5-4026-b4ed-49cb0f844f86"
    var auth: FirebaseAuth
    var database = Firebase.database
    var listSong = MutableLiveData<List<Song>>()
    val arrayListSong = ArrayList<Song>()
    lateinit var url: String

    init {
        auth = FirebaseAuth.getInstance()
        url = "songs/user_songs/${auth.currentUser?.uid}/love"
    }

    fun getSongs(){
        if (auth.currentUser != null)
        database.getReference("songs/user_songs/${auth.currentUser?.uid}/love").addValueEventListener(object : ValueEventListener{
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach(Consumer { t ->
                    val song = Song(
                        t.child("name").value.toString(),
                        t.child("band").value.toString(),
                        t.child("album").value.toString(),
                        "${firebaseImageUrl}/bands/${t.child("band").value.toString()}/albums/${t.child("album").value.toString()}/cover.jpg",
                        "https://www.dropbox.com/s/pz68j3ixhtqg32k/bands/${t.child("band").value.toString()}/albums/${t.child("album").value.toString()}/${t.child("name").value.toString()}.mp3?dl=1"//"${firebaseSongUrl}bands%2F${t.child("band").value.toString()}%2Falbums%2F${t.child("album").value.toString()}%2F${t.child("name").value.toString()}.mp3${token}"
                    )
                    arrayListSong.add(song)
                    Log.d("ViewModel", song.songUrl)
                    Log.d("ViewModel", song.imageUrl)
                })
                listSong.value = arrayListSong
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }




}