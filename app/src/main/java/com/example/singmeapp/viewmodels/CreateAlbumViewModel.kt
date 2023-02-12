package com.example.singmeapp.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class CreateAlbumViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database


    lateinit var albumName: String
    lateinit var band: Band
    lateinit var albumFormat: String
    lateinit var albumCoverRequestBody: RequestBody
    lateinit var albumCoverExtension: String
    lateinit var audioRequestBodyList: ArrayList<RequestBody>
    lateinit var audioFileNames: ArrayList<String>

    lateinit var albumUuid: String

    var albumsCount: Long = 0

    val isExist = MutableLiveData<Boolean>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun createAlbum(_name:String,
                    _band: Band,
                    _albumFormat: String,
                    _albumCoverRequestBody: RequestBody,
                    _albumCoverExtension: String,
                    _audioRequestBodyList: ArrayList<RequestBody>,
                    _audioFileNames: ArrayList<String>){

        albumUuid = UUID.randomUUID().toString()
        albumName = _name
        band = _band
        albumFormat = _albumFormat
        albumCoverRequestBody = _albumCoverRequestBody
        albumCoverExtension = _albumCoverExtension
        audioRequestBodyList = _audioRequestBodyList
        audioFileNames = _audioFileNames

        database.reference.child("album_exist/${band.uuid}").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(albumName).exists()){
                    isExist.value = true
                } else {
                    createAlbum()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createAlbum(){
        val datetime = LocalDateTime.now()
        database.reference.child("album_exist/${band.uuid}/${albumName}/uuid").setValue(albumUuid)
        database.reference.child("album_exist/${band.uuid}/${albumName}/format").setValue(albumFormat)
        database.reference.child("/albums/${albumUuid}/band").setValue(band.uuid)
        database.reference.child("/albums/${albumUuid}/cover").setValue(albumCoverExtension)
        database.reference.child("/albums/${albumUuid}/format").setValue(albumFormat)
        database.reference.child("/albums/${albumUuid}/name").setValue(albumName)
        database.reference.child("/albums/${albumUuid}/year").setValue(datetime.year.toString())

        database.reference.child("/bands/${band.uuid}/albums").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                albumsCount = snapshot.childrenCount
                createDir("/storage/bands/${band.name}/albums",albumCoverRequestBody,"cover.${albumCoverExtension}")
                database.reference.child("/bands/${band.uuid}/albums/${albumsCount}").setValue(albumUuid)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.reference.child("/bands/${band.uuid}/tracks").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var bandTracksCount = snapshot.childrenCount
                for ((albumTracksCount, i) in audioRequestBodyList.withIndex()){
                    val trackUuid = UUID.randomUUID().toString()
                    createDir("/storage/bands/${band.name}/albums", audioRequestBodyList[albumTracksCount], audioFileNames[albumTracksCount])
                    database.reference.child("tracks/${trackUuid}/album").setValue(albumUuid)
                    database.reference.child("tracks/${trackUuid}/band").setValue(band.uuid)
                    database.reference.child("tracks/${trackUuid}/name").setValue(audioFileNames[albumTracksCount].substring(0, audioFileNames[albumTracksCount].length - 4))
                    database.reference.child("/albums/${albumUuid}/tracks/${albumTracksCount}").setValue(trackUuid)
                    database.reference.child("/bands/${band.uuid}/tracks/${bandTracksCount}").setValue(trackUuid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun createDir(url:String, file: RequestBody, name:String){
        mService.createDir(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 201 || response.code() == 409) {
                    Log.e("Responce", "Dir Created")
                    createAlbumDir("${url}/${albumName}", file, name)
                }
                else Log.e("Responce", "Dir Not Created")

            }

            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun createAlbumDir(url:String, file: RequestBody, name:String){
        mService.createDir(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 201 || response.code() == 409) {
                    Log.e("Responce", "Dir Created")
                    getFileUrl("${url}/${name}", file)
                }
                else Log.e("Responce", "Dir Not Created")

            }

            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }


    fun getFileUrl(url: String, file: RequestBody){
        mService.getUrlForUpload(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200) {
                    mService.addFile(response.body()!!.href, file, "*/*", "*/*").enqueue(object: Callback<ResponseBody>{
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            publicFile(url)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })

                }
                else Log.e("Responce", "URL not Getted")
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

            }

        })
    }


    fun publicFile(url: String){
        mService.publishFile(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200){

                }
                else Log.e("CreateAlbum", response.code().toString())
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}