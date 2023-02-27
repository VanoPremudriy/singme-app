package com.example.singmeapp.viewmodels

import android.os.Build
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Band
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

class PlayerPlaylistViewModel : ViewModel(){
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    //////////////////////////////////////////////
    /////for player playlist
    var trackList = MutableLiveData<List<Track>>()
    var url = MutableLiveData<String>()
    var currentTrackId = MutableLiveData<Int>()
    var isPlaying = MutableLiveData<Boolean>()

    var prevItem = MutableLiveData<TrackAdapter.TrackHolder>()
    var prevId = MutableLiveData<Int>()

    var curBand = MutableLiveData<Band>()

    var isListTrackChanged = MutableLiveData<Boolean>(false)

    fun updateListeningCounter(trackUuid: String, albumUuid: String){
        Log.e("Uuids", "${trackUuid}, ${albumUuid}")
        database.reference.child("tracks/${trackUuid}/listening_counter").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter = 0
                if (snapshot.value != null){
                    counter = snapshot.value.toString().toInt()
                }
                counter++
                database.reference.child("tracks/${trackUuid}/listening_counter").setValue(counter)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.reference.child("albums/${albumUuid}/listening_counter").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter = 0
                if (snapshot.value != null){
                    counter = snapshot.value.toString().toInt()
                }
                counter++
                database.reference.child("albums/${albumUuid}/listening_counter").setValue(counter)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun addTrackToLove(track: Track){
        database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val trackCount = snapshot.childrenCount
                database.reference.child("/users/${auth.currentUser?.uid}/library/love_tracks/${trackCount}").setValue(track.uuid)
                isListTrackChanged.value = !isListTrackChanged.value!!
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
                Log.e("PlayerPlaylist", "delete")
                isListTrackChanged.value = !isListTrackChanged.value!!
                //getTracksForMyLibrary()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}