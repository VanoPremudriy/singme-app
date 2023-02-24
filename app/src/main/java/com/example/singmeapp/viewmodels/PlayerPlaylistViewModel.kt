package com.example.singmeapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlayerPlaylistViewModel : ViewModel(){
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var trackList = MutableLiveData<List<Track>>()
    var url = MutableLiveData<String>()
    var currentTrackId = MutableLiveData<Int>()
    var isPlaying = MutableLiveData<Boolean>()

    var prevItem = MutableLiveData<TrackAdapter.TrackHolder>()
    var prevId = MutableLiveData<Int>()

    var curBand = MutableLiveData<Band>()


    fun addTrackToLove(track: Track){
        database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val trackCount = snapshot.childrenCount
                database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks/${trackCount}").setValue(track.uuid)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun deleteTrackFromLove(track: Track){
        var uuidList = ArrayList<String>()
        database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach { t2->
                    uuidList.add(t2.value.toString())
                }

                uuidList = uuidList.filter {
                    it != track.uuid
                } as ArrayList<String>


                database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks").setValue(uuidList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}