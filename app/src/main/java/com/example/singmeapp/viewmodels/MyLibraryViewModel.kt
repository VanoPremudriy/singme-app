package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Album
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


class MyLibraryViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listTrack = MutableLiveData<HashMap<String, Track>>()
    var arrayListTrack =  HashMap<String, Track>()

    var listMyTrack = MutableLiveData<HashMap<String, Track>>()
    var arrayListMyTrack = HashMap<String, Track>()

    var listMyAlbum = MutableLiveData<HashMap<String, Album>>()
    var arrayListMyAlbum = HashMap<String, Album>()

    var listMyPlaylist = MutableLiveData<HashMap<String, Album>>()
    var arrayListMyPlaylist = HashMap<String, Album>()

    var listMyBand = MutableLiveData<HashMap<String, Band>>()
    var arrayListMyBand = HashMap<String, Band>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    var isAlreadySearch = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    init {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun getTracks() {
        var fbTrackUrl: String
        var fbImageUrl: String
        var fbTrackUrls = HashMap<String, String>()
        var fbImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null)
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    //snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").childrenCount - 1
                    arrayListTrack.clear()
                    listTrack.value = arrayListTrack
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").children.forEach{
                            val band =
                                snapshot.child("/tracks/${it.value}").child("band").value.toString()
                            val album = snapshot.child("/tracks/${it.value}")
                                .child("album").value.toString()

                            val trackName =
                                snapshot.child("/tracks/${it.value}").child("name").value.toString()
                            val bandName =
                                snapshot.child("/bands/${band}").child("name").value.toString()
                            val albumName =
                                snapshot.child("/albums/${album}").child("name").value.toString()
                            val extension = snapshot.child("/albums/${album}").child("cover").value.toString()
                            Log.e("sv", extension)
                            var isTrackInLove = false

                            snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach(
                                Consumer { it1 ->
                                    if (it1.value.toString() == it1.value.toString()) isTrackInLove = true
                                })


                            fbTrackUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"
                            fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"

                            fbTrackUrls.put(it.value.toString(), fbTrackUrl)
                            fbImageUrls.put(it.value.toString(), fbImageUrl)


                            val track = Track(
                                it.value.toString(),
                                trackName,
                                bandName,
                                albumName,
                                band,
                                album,
                                "",
                                "",
                                isTrackInLove
                            )
                            Log.e("Is In Love", isTrackInLove.toString())

                            //arrayListTrack.add(0,track)
                        arrayListTrack.put(it.value.toString(), track)

                        }

                    if (arrayListTrack.size != 0) {
                        listTrack.value = arrayListTrack
                        arrayListTrack.forEach {
                            getFilePath(fbImageUrls.get(it.key)!!, "image", it.key, count)
                            getFilePath(fbTrackUrls.get(it.key)!!, "track", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("image", true)
                        isAlready.value?.put("track", true)
                        isAlready.value = isAlready.value
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    fun getMyTracks(search: String){
        var fbTrackUrl: String
        var fbImageUrl: String
        var fbTrackUrls = HashMap<String, String>()
        var fbImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null)
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    //snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").childrenCount - 1
                    arrayListMyTrack.clear()
                    listMyTrack.value = arrayListMyTrack
                    if (search.isNotEmpty())
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_tracks").children.forEach{
                        val trackName =
                            snapshot.child("/tracks/${it.value}").child("name").value.toString()

                        if (trackName.lowercase().contains(search.lowercase())) {
                            val band =
                                snapshot.child("/tracks/${it.value}").child("band").value.toString()
                            val album = snapshot.child("/tracks/${it.value}")
                                .child("album").value.toString()

                            val bandName =
                                snapshot.child("/bands/${band}").child("name").value.toString()
                            val albumName =
                                snapshot.child("/albums/${album}").child("name").value.toString()
                            val extension = snapshot.child("/albums/${album}").child("cover").value.toString()
                            Log.e("sv", extension)
                            var isTrackInLove = false

                            snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach(
                                Consumer { it1 ->
                                    if (it1.value.toString() == it1.value.toString()) isTrackInLove = true
                                })


                            fbTrackUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"
                            fbImageUrl = "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"

                            fbTrackUrls.put(it.value.toString(), fbTrackUrl)
                            fbImageUrls.put(it.value.toString(), fbImageUrl)


                            val track = Track(
                                it.value.toString(),
                                trackName,
                                bandName,
                                albumName,
                                band,
                                album,
                                "",
                                "",
                                isTrackInLove
                            )
                            Log.e("Is In Love", isTrackInLove.toString())
                            arrayListMyTrack.put(it.value.toString(), track)
                        }



                    }

                    if (arrayListMyTrack.size != 0) {
                        listMyTrack.value = arrayListMyTrack
                        arrayListMyTrack.forEach {
                            getFilePath(fbImageUrls.get(it.key)!!, "myTrackImage", it.key, count)
                            getFilePath(fbTrackUrls.get(it.key)!!, "myTrack", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("myTrackImage", true)
                        isAlready.value?.put("myTrack", true)
                        isAlready.value = isAlready.value
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun getMyAlbums(search: String){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    arrayListMyAlbum.clear()
                    listMyAlbum.value = arrayListMyAlbum
                    if (search.isNotEmpty())
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_albums").children.forEach{ t ->
                        val albumName = snapshot.child("/albums/${t.value}").child("name").value.toString()

                        if (albumName.lowercase().contains(search.lowercase())) {
                            val band = snapshot.child("/albums/${t.value}").child("band").value.toString()
                            val bandName = snapshot.child("/bands/${band}").child("name").value.toString()
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

                            arrayListMyAlbum.put(t.value.toString(), album)
                        }



                    }

                    if (arrayListMyAlbum.size != 0) {
                        listMyAlbum.value = arrayListMyAlbum
                        arrayListMyAlbum.forEach {
                            getFilePath(fbAlbumImageUrls.get(it.key)!!, "myAlbumImage", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("myAlbumImage", true)
                        isAlready.value = isAlready.value
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getMyPlaylists(search: String){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListMyPlaylist.clear()
                    listMyPlaylist.value = arrayListMyPlaylist
                    if (search.isNotEmpty())
                    snapshot.child("/users/${auth.currentUser?.uid}/library/playlists").children.forEach { t ->
                        val playlistsName = snapshot.child("/albums/${t.value}").child("name").value.toString()
                        if (playlistsName.lowercase().contains(search.lowercase())) {
                            val authorUuid = snapshot.child("/albums/${t.value}").child("band").value.toString()
                            val authorName = snapshot.child("/users/${authorUuid}/profile/name").value.toString()
                            val year = snapshot.child("/albums/${t.value}").child("year").value.toString()
                            val extension = snapshot.child("/albums/${t.value}").child("cover").value.toString()

                            if (authorUuid != "null" && authorName != "null" && playlistsName!= "null" && year != "null") {

                                if (extension != "null")
                                    fbAlbumImageUrl =
                                        "/storage/users/${auth.currentUser?.uid}/playlists/${playlistsName}/cover.${extension}"
                                else fbAlbumImageUrl = "/storage/default_images/cover.png"
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

                                arrayListMyPlaylist.put(t.value.toString(), album)
                            }
                        }

                    }

                    if (arrayListMyPlaylist.size != 0) {
                        listMyPlaylist.value = arrayListMyPlaylist
                        arrayListMyPlaylist.forEach {
                            getFilePath(fbAlbumImageUrls.get(it.key)!!, "myPlaylistImage", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("myPlaylistImage", true)
                        isAlready.value = isAlready.value
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getMyBands(search: String){
        var fbBandImageUrl: String
        var fbBandBackUrl: String
        var fbBandImageUrls = HashMap<String, String>()
        var fbBandBackUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListMyBand.clear()
                    listMyBand.value = arrayListMyBand
                    if (search.isNotEmpty())
                    snapshot.child("/users/${auth.currentUser?.uid}/library/love_bands").children.forEach{ t ->
                        val bandName = snapshot.child("/bands/${t.value}").child("name").value.toString()
                        if (bandName.lowercase().contains(search.lowercase())) {
                            var extension = snapshot.child("/bands/${t.value}").child("avatar").value.toString()
                            var info = snapshot.child("/bands/${t.value}").child("info").value.toString()
                            var backExtension = snapshot.child("/bands/${t.value}").child("background").value.toString()

                            fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                            fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                            fbBandBackUrls.put(t.value.toString(), fbBandBackUrl)
                            fbBandImageUrls.put(t.value.toString(), fbBandImageUrl)

                            val band = Band(
                                t.value.toString(),
                                bandName,
                                info,
                                "",
                                ""
                            )
                            arrayListMyBand.put(t.value.toString(), band)
                        }

                    }

                    if (arrayListMyBand.size != 0) {
                        listMyBand.value = arrayListMyBand
                        arrayListMyBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.key)!!, "myBandImage", it.key, count)
                            getFilePath(fbBandBackUrls.get(it.key)!!, "myBandBack", it.key, count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("myBandImage", true)
                        isAlready.value?.put("myBandBack", true)
                        isAlready.value = isAlready.value
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
    fun getFilePath(url: String, value: String,index: String, count: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
                    Log.e("Track", "Three")
                    if (response.body() != null) {
                        val filePath = (response.body() as FileApiModel).public_url
                        getFileUrl(filePath, value, index, count)
                    }
                }

                override fun onFailure(call: Call<FileApiModel>, t: Throwable) {

                }

            })
    }

    fun getFileUrl(url: String, value: String, index: String, count: Int){
        mService.getSecondFile(url, authToken)
            .enqueue(object : Callback<SecondFileApiModel> {
                override fun onResponse(
                    call: Call<SecondFileApiModel>,
                    response: Response<SecondFileApiModel>
                ) {
                    if (response.body() != null){
                        val fileUrl = (response.body() as SecondFileApiModel).href
                        setList(fileUrl, value, index, count)
                    }
                }

                override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

                }

            })
    }

    fun setList(url: String, value: String, index: String, count: Int){
        when(value){
            "image" -> {
                arrayListTrack[index]?.imageUrl = url
                listTrack.value = arrayListTrack
            }
            "track" -> {
                arrayListTrack[index]?.trackUrl = url
                listTrack.value = arrayListTrack
            }

            "myTrackImage" -> {
                if (arrayListMyTrack[index]?.imageUrl == "") {
                    arrayListMyTrack[index]?.imageUrl = url
                    listMyTrack.value = arrayListMyTrack
                }

            }
            "myTrack" -> {
                if (arrayListMyTrack[index]?.trackUrl == ""){
                    arrayListTrack[index]?.trackUrl = url
                    listMyTrack.value = arrayListMyTrack
                }
            }

            "myAlbumImage" -> {
                if (arrayListMyAlbum[index]?.imageUrl == "") {
                    arrayListMyAlbum[index]?.imageUrl = url
                    listMyAlbum.value = arrayListMyAlbum
                }
            }

            "myPlaylistImage" -> {
                if (arrayListMyPlaylist[index]?.imageUrl == "") {
                    arrayListMyPlaylist[index]?.imageUrl = url
                    listMyPlaylist.value = arrayListMyPlaylist
                }
            }

            "myBandImage" -> {
                if (arrayListMyBand[index]?.imageUrl == "") {
                    arrayListMyBand[index]?.imageUrl = url
                    listMyBand.value = arrayListMyBand
                }
            }

            "myBandBack" -> {
                if (arrayListMyBand[index]?.backgroundUrl == "") {
                    arrayListMyBand[index]?.backgroundUrl = url
                    listMyBand.value = arrayListMyBand
                }
            }

        }

        if (count == arrayListTrack.size -1 && value == "image"){
            isAlready.value?.put("image", true)
            isAlready.value = isAlready.value
        }
        if (count == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

        if (count == arrayListMyTrack.size -1 && value == "myTrackImage"){
            isAlreadySearch.value?.put("myTrackImage", true)
            isAlreadySearch.value = isAlreadySearch.value
        }
        if (count == arrayListMyTrack.size -1 && value == "myTrack"){
            isAlreadySearch.value?.put("myTrack", true)
            isAlreadySearch.value = isAlreadySearch.value
        }

        if (count == arrayListMyAlbum.size -1 && value == "myAlbumImage"){
            isAlreadySearch.value?.put("myAlbumImage", true)
            isAlreadySearch.value = isAlreadySearch.value
        }

        if (count == arrayListMyPlaylist.size -1 && value == "myPlaylistImage"){
            isAlreadySearch.value?.put("myPlaylistImage", true)
            isAlreadySearch.value = isAlreadySearch.value
        }

        if (count == arrayListMyBand.size -1 && value == "myBandImage"){
            isAlreadySearch.value?.put("myBandImage", true)
            isAlreadySearch.value = isAlreadySearch.value
        }

        if (count == arrayListMyBand.size -1 && value == "myBandBack"){
            isAlreadySearch.value?.put("myBandBack", true)
            isAlreadySearch.value = isAlreadySearch.value
        }

    }

}