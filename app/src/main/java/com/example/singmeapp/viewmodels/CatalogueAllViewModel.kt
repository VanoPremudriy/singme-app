package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
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
import java.time.LocalDateTime
import java.util.function.Consumer

class CatalogueAllViewModel: ViewModel() {

    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listTrack = MutableLiveData<List<Track>>()
    var arrayListTrack = ArrayList<Track>()

    var listAlbum = MutableLiveData<List<Album>>()
    var arrayListAlbum = ArrayList<Album>()

    var listBand = MutableLiveData<List<Band>>()
    var arrayListBand = ArrayList<Band>()

    var listPlaylist = MutableLiveData<List<Album>>()
    var arrayListPlaylist = ArrayList<Album>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())


    fun getNewTracks(){
        var fbTrackUrl = ""
        var fbTrackImageUrl = ""
        var count = 0
        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("tracks").children.forEach{ t ->
                    val trackName = t.child("name").value.toString()
                    val trackAlbumUuid = t.child("album").value.toString()
                    val trackAlbumName = snapshot.child("/albums/${trackAlbumUuid}/name").value.toString()
                    val bandUuid = t.child("band").value.toString()
                    val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                    val extension = snapshot.child("/albums/${trackAlbumUuid}/cover").value.toString()
                    val date = t.child("created_at").value.toString()
                    var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.parse(date)
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }

                    var isInLove = false
                    snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                        if (it1.value.toString() == t.key.toString()) isInLove = true
                    }

                    fbTrackUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/${trackName}.mp3"
                    fbTrackImageUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/cover.${extension}"
                    fbTrackUrls.put(t.key.toString(), fbTrackUrl)
                    fbTrackImageUrls.put(t.key.toString(), fbTrackImageUrl)

                    val track = Track(
                        t.key.toString(),
                        trackName,
                        bandName,
                        trackAlbumName,
                        bandUuid,
                        trackAlbumUuid,
                        "",
                        "",
                        isInLove,
                        localDateTime
                    )


                    arrayListTrack.add(track)
                }

                if (arrayListTrack.size != 0) {
                    arrayListTrack.sortBy { track -> track.date }
                    arrayListTrack =
                        arrayListTrack.reversed() as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                        getFilePath(fbTrackImageUrls.get(it.uuid)!!, "trackImage", count)
                        count++
                    }
                } else {
                    isAlready.value?.put("track", true)
                    isAlready.value?.put("trackImage", true)
                    isAlready.value = isAlready.value
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getPopularTracks(){
        var fbTrackUrl = ""
        var fbTrackImageUrl = ""
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("tracks").children.forEach{ t ->

                    val trackName = t.child("name").value.toString()
                    val trackAlbumUuid = t.child("album").value.toString()
                    val trackAlbumName = snapshot.child("/albums/${trackAlbumUuid}/name").value.toString()
                    val bandUuid = t.child("band").value.toString()
                    val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                    val extension = snapshot.child("/albums/${trackAlbumUuid}/cover").value.toString()
                    val date = t.child("created_at").value.toString()


                    var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.parse(date)
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }

                    val listeningCounter = t.child("listening_counter").value.toString()
                    if (listeningCounter != "null"){
                        listeningCounters.put(t.key.toString(), listeningCounter.toInt())
                    } else {
                        listeningCounters.put(t.key.toString(), 0)
                    }
                    //Log.e("listening counter", listeningCounters.get(t.key.toString()).toString())

                    var isInLove = false
                    snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach { it1 ->
                        if (it1.value.toString() == t.value.toString()) isInLove = true
                    }

                    fbTrackUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/${trackName}.mp3"
                    fbTrackImageUrl = "/storage/bands/${bandName}/albums/${trackAlbumName}/cover.${extension}"
                    fbTrackUrls.put(t.key.toString(), fbTrackUrl)
                    fbTrackImageUrls.put(t.key.toString(), fbTrackImageUrl)

                    val track = Track(
                        t.key.toString(),
                        trackName,
                        bandName,
                        trackAlbumName,
                        bandUuid,
                        trackAlbumUuid,
                        "",
                        "",
                        isInLove,
                        localDateTime
                    )


                    arrayListTrack.add(track)
                }

                arrayListTrack.sortBy { track -> listeningCounters.get(track.uuid) }
                arrayListTrack = arrayListTrack.reversed() as ArrayList<Track> /* = java.util.ArrayList<com.example.singmeapp.items.Track> */
                listTrack.value = arrayListTrack
                arrayListTrack.forEach {
                    getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                    getFilePath(fbTrackImageUrls.get(it.uuid)!!, "trackImage", count)
                    count++
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getSearchTracks(search: String, category: String){
        var fbTrackUrl = ""
        var fbTrackImageUrl = ""
        var path = ""
        var count = 0
        var fbTrackUrls = HashMap<String, String>()
        var fbTrackImageUrls = HashMap<String, String>()
        var uuid = ""
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListTrack.clear()
                listTrack.value = arrayListTrack
                if (category == "all"){
                    path = "tracks"
                } else if (category == "user"){
                    path = "/users/${auth.currentUser?.uid}/library/love_tracks"
                }
                if (search != "")
                    snapshot.child(path).children.forEach { t ->
                        if (category == "all"){
                            uuid = t.key.toString()
                        } else if (category == "user"){
                            uuid = t.value.toString()
                        }

                        val trackName =
                            snapshot.child("/tracks/${uuid}").child("name").value.toString()

                        if (trackName.lowercase().contains(search.lowercase())) {
                            val band =
                                snapshot.child("/tracks/${uuid}").child("band").value.toString()
                            val album = snapshot.child("/tracks/${uuid}")
                                .child("album").value.toString()

                            val bandName =
                                snapshot.child("/bands/${band}").child("name").value.toString()
                            val albumName =
                                snapshot.child("/albums/${album}").child("name").value.toString()
                            val extension = snapshot.child("/albums/${album}").child("cover").value.toString()

                            var isTrackInLove = false

                            snapshot.child("users/${auth.currentUser?.uid}/library/love_tracks").children.forEach(
                                Consumer { it1 ->
                                    if (it1.value.toString() == uuid) isTrackInLove = true
                                })

                            fbTrackUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/${trackName}.mp3"
                            fbTrackImageUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                            fbTrackUrls.put(uuid, fbTrackUrl)
                            fbTrackImageUrls.put(uuid, fbTrackImageUrl)

                            val track = Track(
                                uuid,
                                trackName,
                                bandName,
                                albumName,
                                band,
                                album,
                                "",
                                "",
                                isTrackInLove,
                            )
                            arrayListTrack.add( track)
                        }
                    }
                if (arrayListTrack.size != 0) {
                    listTrack.value = arrayListTrack
                    arrayListTrack.forEach {
                        getFilePath(fbTrackUrls.get(it.uuid)!!, "track", count)
                        getFilePath(fbTrackImageUrls.get(it.uuid)!!, "trackImage", count)
                        count++
                    }
                } else {
                    isAlready.value?.put("track", true)
                    isAlready.value?.put("trackImage", true)
                    isAlready.value = isAlready.value
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getNewAlbums(){
        var fbAlbumImageUrl = ""
        var count = 0
        var fbAlbumImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("albums").children.forEach{ t ->
                    val format = t.child("/format").value.toString()
                    if (format == "Album" || format == "Single/EP"){
                        val albumName = t.child("name").value.toString()
                        val bandUuid = t.child("band").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val year = t.child("year").value?.toString()?.toInt()
                        val extension = t.child("cover").value.toString()
                        val date = t.child("created_at").value.toString()
                        if (albumName != "null" && year != null && extension != "null" && format != "null") {

                            var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(date)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }

                            fbAlbumImageUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                            fbAlbumImageUrls.put(t.key.toString(), fbAlbumImageUrl)

                            var isInLove = false
                            snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                if (it1.value.toString() == t.value.toString()) isInLove = true
                            }

                            val isAuthor =
                                snapshot.child("bands_has_users/${bandUuid}/${auth.currentUser?.uid}").value != null

                            val album = Album(
                                t.key.toString(),
                                albumName,
                                bandName,
                                year,
                                isInLove,
                                isAuthor,
                                "",
                                localDateTime
                            )
                            arrayListAlbum.add(album)

                        }
                    }
                }
                arrayListAlbum.sortBy { album ->  album.date}
                arrayListAlbum = arrayListAlbum.reversed() as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                listAlbum.value = arrayListAlbum
                arrayListAlbum.forEach {
                    getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "albumImage", count)
                    count++
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    fun getPopularAlbums(){
        var fbAlbumImageUrl: String
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbAlbumImageUrls = HashMap<String, String>()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("albums").children.forEach{ t ->
                    val format = t.child("/format").value.toString()
                    if (format == "Album" || format == "Single/EP"){
                        val albumName = t.child("name").value.toString()
                        val bandUuid = t.child("band").value.toString()
                        val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                        val year = t.child("year").value?.toString()?.toInt()
                        val extension = t.child("cover").value.toString()
                        val date = t.child("created_at").value.toString()
                        if (albumName != "null" && year != null && extension != "null" && format != "null") {

                            val listeningCounter = t.child("listening_counter").value.toString()
                            if (listeningCounter != "null"){
                                listeningCounters.put(t.key.toString(), listeningCounter.toInt())
                            } else {
                                listeningCounters.put(t.key.toString(), 0)
                            }

                            var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(date)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }

                            fbAlbumImageUrl =
                                "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                            fbAlbumImageUrls.put(t.key.toString(), fbAlbumImageUrl)

                            var isInLove = false
                            snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                if (it1.value.toString() == t.value.toString()) isInLove = true
                            }

                            val isAuthor =
                                snapshot.child("bands_has_users/${bandUuid}/${auth.currentUser?.uid}").value != null

                            val album = Album(
                                t.key.toString(),
                                albumName,
                                bandName,
                                year,
                                isInLove,
                                isAuthor,
                                "",
                                localDateTime
                            )
                            arrayListAlbum.add(album)

                        }
                    }
                }

                arrayListAlbum.sortBy { album -> listeningCounters.get(album.uuid) }
                arrayListAlbum = arrayListAlbum.reversed() as ArrayList<Album> /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
                listAlbum.value = arrayListAlbum
                arrayListAlbum.forEach {
                    getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "albumImage", count)
                    count++
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun getSearchAlbums(search: String, category: String){
        var fbAlbumImageUrl: String
        var count = 0
        var listeningCounters = HashMap<String, Int>()
        var fbAlbumImageUrls = HashMap<String, String>()
        var path = ""
        var uuid = ""
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListAlbum.clear()
                listAlbum.value = arrayListAlbum
                if (category == "all"){
                    path = "albums"
                } else if (category == "user"){
                    path = "/users/${auth.currentUser?.uid}/library/love_albums"
                }
                if (search != "")
                    snapshot.child(path).children.forEach{ t ->
                        if (category == "all"){
                            uuid = t.key.toString()
                        } else if (category == "user"){
                            uuid = t.value.toString()
                        }
                        val albumName = snapshot.child("/albums/${uuid}").child("name").value.toString()
                        if (albumName.lowercase().contains(search.lowercase())){
                            val format = snapshot.child("/albums/${uuid}").child("/format").value.toString()
                            if (format == "Album" || format == "Single/EP"){
                                val bandUuid = snapshot.child("/albums/${uuid}").child("band").value.toString()
                                val bandName = snapshot.child("bands/${bandUuid}/name").value.toString()
                                val year = snapshot.child("/albums/${uuid}").child("year").value?.toString()?.toInt()
                                val extension = snapshot.child("/albums/${uuid}").child("cover").value.toString()
                                val date = snapshot.child("/albums/${uuid}").child("created_at").value.toString()
                                if (albumName != "null" && year != null && extension != "null" && format != "null") {

                                    val listeningCounter = snapshot.child("/albums/${uuid}").child("listening_counter").value.toString()
                                    if (listeningCounter != "null") {
                                        listeningCounters.put(uuid, listeningCounter.toInt())
                                    } else {
                                        listeningCounters.put(uuid, 0)
                                    }

                                    var localDateTime =
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            LocalDateTime.parse(date)
                                        } else {
                                            TODO("VERSION.SDK_INT < O")
                                        }

                                    fbAlbumImageUrl =
                                        "/storage/bands/${bandName}/albums/${albumName}/cover.${extension}"
                                    fbAlbumImageUrls.put(uuid, fbAlbumImageUrl)

                                    var isInLove = false
                                    snapshot.child("users/${auth.currentUser?.uid}/library/love_albums").children.forEach { it1 ->
                                        if (it1.value.toString() == uuid) isInLove = true
                                    }

                                    val isAuthor =
                                        snapshot.child("bands_has_users/${bandUuid}/${auth.currentUser?.uid}").value != null

                                    val album = Album(
                                        uuid,
                                        albumName,
                                        bandName,
                                        year,
                                        isInLove,
                                        isAuthor,
                                        "",
                                        localDateTime
                                    )
                                    arrayListAlbum.add(album)
                                }

                            }
                        }
                    }

                if (arrayListAlbum.size != 0) {
                    listAlbum.value = arrayListAlbum
                    arrayListAlbum.forEach {
                        getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "albumImage", count)
                        count++
                    }
                } else {
                    isAlready.value?.put("albumImage", true)
                    isAlready.value = isAlready.value
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun getNewBands(){
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
                    arrayListBand.clear()
                    listBand.value = arrayListBand
                    snapshot.child("bands").children.forEach{ t ->

                        val bandName = t.child("name").value.toString()
                        var extension = t.child("avatar").value.toString()
                        var info = t.child("info").value.toString()
                        var backExtension = t.child("background").value.toString()

                        val date = t.child("created_at").value.toString()

                        var localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.parse(date)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }

                        fbBandImageUrl = "/storage/bands/${bandName}/profile/avatar.${extension}"
                        fbBandBackUrl = "/storage/bands/${bandName}/profile/back.${backExtension}"

                        fbBandBackUrls.put(t.key.toString(), fbBandBackUrl)
                        fbBandImageUrls.put(t.key.toString(), fbBandImageUrl)

                        val band = Band(
                            t.key.toString(),
                            bandName,
                            info,
                            "",
                            "",
                            localDateTime
                        )

                        arrayListBand.add(band)

                    }

                    if (arrayListBand.size != 0) {
                        arrayListBand.sortBy { band ->  band.date}
                        arrayListBand.reverse()
                        listBand.value = arrayListBand
                        arrayListBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.uuid)!!, "bandImage", count)
                            getFilePath(fbBandBackUrls.get(it.uuid)!!, "bandBack", count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("bandImage", true)
                        isAlready.value?.put("bandBack", true)
                        isAlready.value = isAlready.value
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getSearchBands(search: String, category: String){
        var fbBandImageUrl: String
        var fbBandBackUrl: String
        var fbBandImageUrls = HashMap<String, String>()
        var fbBandBackUrls = HashMap<String, String>()
        var count = 0
        var path = ""
        var uuid = ""
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListBand.clear()
                    listBand.value = arrayListBand
                    if (category == "all"){
                        path = "bands"
                    } else if (category == "user"){
                        path = "/users/${auth.currentUser?.uid}/library/love_bands"
                    }
                    if (search != "")
                        snapshot.child(path).children.forEach{ t ->
                            if (category == "all"){
                                uuid = t.key.toString()
                            } else if (category == "user"){
                                uuid = t.value.toString()
                            }
                            val bandName = snapshot.child("/bands/${uuid}").child("name").value.toString()
                            if (bandName.lowercase().contains(search.lowercase())) {
                                var extension = snapshot.child("/bands/${uuid}").child("avatar").value.toString()
                                var info = snapshot.child("/bands/${uuid}").child("info").value.toString()
                                var backExtension = snapshot.child("/bands/${uuid}").child("background").value.toString()

                                val date = snapshot.child("/bands/${uuid}").child("created_at").value.toString()

                                var localDateTime =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        LocalDateTime.parse(date)
                                    } else {
                                        TODO("VERSION.SDK_INT < O")
                                    }

                                fbBandImageUrl =
                                    "/storage/bands/${bandName}/profile/avatar.${extension}"
                                fbBandBackUrl =
                                    "/storage/bands/${bandName}/profile/back.${backExtension}"

                                fbBandBackUrls.put(uuid, fbBandBackUrl)
                                fbBandImageUrls.put(uuid, fbBandImageUrl)

                                val band = Band(
                                    uuid,
                                    bandName,
                                    info,
                                    "",
                                    "",
                                    localDateTime
                                )

                                arrayListBand.add(band)
                            }

                        }

                    if (arrayListBand.size != 0) {
                        listBand.value = arrayListBand
                        arrayListBand.forEach {
                            getFilePath(fbBandImageUrls.get(it.uuid)!!, "bandImage", count)
                            getFilePath(fbBandBackUrls.get(it.uuid)!!, "bandBack", count)
                            count++
                        }
                    } else {
                        isAlready.value?.put("bandImage", true)
                        isAlready.value?.put("bandBack", true)
                        isAlready.value = isAlready.value
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun getSearchPlaylists(search: String){
        var fbAlbumImageUrl: String
        var fbAlbumImageUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi", "SuspiciousIndentation")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListPlaylist.clear()
                    listPlaylist.value = arrayListPlaylist
                    if (search.isNotEmpty())
                    snapshot.child("/users/${auth.currentUser?.uid}/library/playlists").children.forEach { t ->
                        val playlistsName =
                            snapshot.child("/albums/${t.value}").child("name").value.toString()
                        if (playlistsName.lowercase().contains(search.lowercase())) {
                            val authorUuid =
                                snapshot.child("/albums/${t.value}").child("band").value.toString()
                            val authorName =
                                snapshot.child("/users/${authorUuid}/profile/name").value.toString()
                            val year =
                                snapshot.child("/albums/${t.value}").child("year").value.toString()
                            val extension =
                                snapshot.child("/albums/${t.value}").child("cover").value.toString()

                            if (authorUuid != "null"
                                && authorName != "null"
                                && playlistsName != "null"
                                && year != "null"
                            ) {


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

                                arrayListPlaylist.add(album)
                            }
                        }
                    }

                    if (arrayListPlaylist.size != 0) {
                        listPlaylist.value = arrayListPlaylist
                        arrayListPlaylist.forEach {
                            getFilePath(fbAlbumImageUrls.get(it.uuid)!!, "playlistImage", count)
                            Log.e("Image", fbAlbumImageUrls.get(it.uuid).toString())
                            count++
                        }
                    } else {
                        isAlready.value?.put("playlistImage", true)
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
            "trackImage" ->{
                arrayListTrack[index].imageUrl = url
                listTrack.value = arrayListTrack
            }

            "albumImage" -> {
                arrayListAlbum[index].imageUrl = url
                listAlbum.value = arrayListAlbum
            }

            "bandImage" -> {
                arrayListBand[index].imageUrl = url
                listBand.value = arrayListBand
            }
            "bandBack" -> {
                arrayListBand[index].backgroundUrl = url
                listBand.value = arrayListBand
            }
            "playlistImage" -> {
                arrayListPlaylist[index].imageUrl = url
                listPlaylist.value = arrayListPlaylist
            }


        }

        if (index == arrayListTrack.size -1 && value == "track"){
            isAlready.value?.put("track", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListTrack.size -1 && value == "trackImage"){
            isAlready.value?.put("trackImage", true)
            isAlready.value = isAlready.value
        }


        if (index ==  arrayListAlbum.size -1 && value == "albumImage"){
            isAlready.value?.put("albumImage", true)
            isAlready.value = isAlready.value
        }


        if (index == arrayListBand.size -1 && value == "bandImage"){
            isAlready.value?.put("bandImage", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListBand.size -1 && value == "bandBack"){
            isAlready.value?.put("bandBack", true)
            isAlready.value = isAlready.value
        }

        if (index == arrayListPlaylist.size -1 && value == "playlistImage"){
            isAlready.value?.put("playlistImage", true)
            isAlready.value = isAlready.value
        }

    }
}