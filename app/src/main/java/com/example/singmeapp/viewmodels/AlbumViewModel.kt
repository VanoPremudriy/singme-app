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

class AlbumViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listTrack: MutableLiveData<List<Track>> = MutableLiveData<List<Track>>()
    lateinit var arrayListTrack: ArrayList<Track>

    val bandUuid = MutableLiveData<String>()

    val currentAlbum = MutableLiveData<Album>()

    val isAuthor = MutableLiveData<Boolean>()

    val isLove  = MutableLiveData<Boolean>()

    var isLoveAlbumsListChanged = MutableLiveData<Boolean>(false)

    lateinit var album: Album

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }


    /*fun updateListeningCounter(albumUuid: String){
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
    }*/

    fun getAlbumData(albumUuid: String){
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val albumName = snapshot.child("albums/${albumUuid}/name").value.toString()
                val albumBandUuid  = snapshot.child("albums/${albumUuid}/band").value.toString()
                val albumBandName = snapshot.child("bands/${albumBandUuid}/name").value.toString()
                val albumYear = snapshot.child("albums/${albumUuid}/year").value.toString().toInt()
                val albumCoverExtension = snapshot.child("albums/${albumUuid}/cover").value.toString()

                val fbAlbumCover = "storage/bands/${albumBandName}/albums/${albumName}/cover.${albumCoverExtension}"

                var isInLove = false
                snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                    if (it1.value.toString() == albumUuid) isInLove = true
                }

                val isAuthor = snapshot.child("bands_has_users/${albumBandUuid}/${auth.currentUser?.uid}").value != null

                isLove.value = isInLove

                album = Album(
                    albumUuid,
                    albumName,
                    albumBandName,
                    albumYear,
                    isInLove,
                    isAuthor,
                    ""
                )

                currentAlbum.value = album
                getFilePath(fbAlbumCover, "albumCover", -1)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getTracks(albumUuid: String){
        var fbTrackUrl: String
        var fbImageUrl: String
        var count = 0
        var fbTrackUrls = HashMap<String, String>()
        var fbImageUrls = HashMap<String, String>()
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListTrack.clear()
                    listTrack.value = arrayListTrack
                    snapshot.child("/albums/${albumUuid}/tracks").children.forEach{ t ->

                        val trackName = snapshot.child("/tracks/${t.value}/name").value.toString()
                        val bandUuuid = snapshot.child("albums/${albumUuid}/band").value.toString()
                        val bandName = snapshot.child("/bands/${bandUuuid}/name").value.toString()
                        val albumName = snapshot.child("/albums/${albumUuid}/name").value.toString()

                        val albumCoverExtension = snapshot.child("/albums/${albumUuid}/cover").value.toString()

                        fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${albumCoverExtension}"
                        fbTrackUrl = "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"

                        fbImageUrls.put(t.value.toString(), fbImageUrl)
                        fbTrackUrls.put(t.value.toString(), fbTrackUrl)

                        var isInLove = false
                        snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                            if (it1.value.toString() == t.value.toString()) isInLove = true
                        }

                        bandUuid.value = bandUuuid

                        val track = Track(
                            t.value.toString(),
                            trackName,
                            bandName,
                            albumName,
                            bandUuuid,
                            albumUuid,
                            "",
                            "",
                            isInLove
                        )

                        arrayListTrack.add(track)
                    }

                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                        getFilePath(fbImageUrls.get(it.uuid)!!, "trackImage", count)
                        count++
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
            "track" -> {
                arrayListTrack[index].trackUrl = url
                listTrack.value = arrayListTrack
            }

            "trackImage" -> {
                arrayListTrack[index].imageUrl = url
                listTrack.value = arrayListTrack
            }

            "albumCover" ->{
                album.imageUrl = url
                currentAlbum.value = album
            }
        }
    }



    fun addAlbumInLove(albumUuid: String){
        database.reference.child("/users/${auth.currentUser?.uid}/library/love_albums").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val albumCount = snapshot.childrenCount
                database.reference.child("/users/${auth.currentUser?.uid}/library/love_albums/${albumCount}").setValue(albumUuid)
                isLoveAlbumsListChanged.value = !isLoveAlbumsListChanged.value!!
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun deleteAlbumFromLove(albumUuid: String){
        var uuidList = ArrayList<String>()
        database.reference.child("/users/${auth.currentUser?.uid}/library/love_albums").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach { t2->
                    uuidList.add(t2.value.toString())
                }

                uuidList = uuidList.filter {
                    it != albumUuid
                } as ArrayList<String>


                database.reference.child("/users/${auth.currentUser?.uid}/library/love_albums").setValue(uuidList)
                isLoveAlbumsListChanged.value = !isLoveAlbumsListChanged.value!!
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getAuthors(albumUuid: String){
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bandUuid = snapshot.child("albums/${albumUuid}/band").value.toString()
                val isExist = snapshot.child("bands_has_users/${bandUuid}/${auth.currentUser?.uid}").value
                isAuthor.value = isExist != null
                Log.e("is", isExist.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun deleteAlbum(albumUuid: String){
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val bandUuid = snapshot.child("/albums/${albumUuid}/band").value.toString()
                val albumName = snapshot.child("/albums/${albumUuid}/name").value.toString()

                val tracksUuids = ArrayList<String>()
                val userLoveTracksUuids = ArrayList<String>()
                val userLoveAlbumsUuids = ArrayList<String>()
                val bandTracksUuids = ArrayList<String>()

                snapshot.child("albums/${albumUuid}/tracks").children.forEach {
                    tracksUuids.add(it.value.toString())
                }

                snapshot.child("/users").children.forEach { user ->
                    user.child("/library/love_tracks").children.forEach{ userLoveTrack ->
                        userLoveTracksUuids.add(userLoveTrack.value.toString())
                    }
                    if (userLoveTracksUuids.removeAll(HashSet<String>(tracksUuids)))
                    database.reference.child("users/${user.key}/library/love_tracks").setValue(userLoveTracksUuids)

                    user.child("/library/love_albums").children.forEach{userLoveAlbum ->
                        userLoveAlbumsUuids.add(userLoveAlbum.value.toString())
                    }
                    if (userLoveAlbumsUuids.remove(albumUuid))
                    database.reference.child("users/${user.key.toString()}/library/love_albums").setValue(userLoveAlbumsUuids)
                }

                snapshot.child("/tracks").children.forEach { track ->
                    if (tracksUuids.contains(track.key.toString())){
                        database.reference.child("tracks/${track.key.toString()}").setValue(null)
                    }
                }

                snapshot.child("/bands/${bandUuid}/tracks").children.forEach { track ->
                    bandTracksUuids.add(track.value.toString())
                }

                if (bandTracksUuids.removeAll(HashSet<String>(tracksUuids))){
                    database.reference.child("/bands/${bandUuid}/tracks").setValue(bandTracksUuids)
                }

                snapshot.child("/bands/${bandUuid}/albums").children.forEach { album ->
                    if (album.value.toString() == albumUuid){
                        database.reference.child("/bands/${bandUuid}/albums/${album.key.toString()}").setValue(null)
                    }
                }

                database.reference.child("album_exist/${bandUuid}/${albumName}").setValue(null)

                database.reference.child("albums/${albumUuid}").setValue(null)


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}