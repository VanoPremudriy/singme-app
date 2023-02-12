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
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Member
import com.example.singmeapp.items.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import extensions.Extension
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class BandViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    var listMember = MutableLiveData<List<Member>>()
    val arrayListMember = ArrayList<Member>()
    val isEdit = MutableLiveData<Boolean>()
    val editText = MutableLiveData<String>()

    val currentBand = MutableLiveData<Band>()
    lateinit var band: Band

    fun getBandDate(bandUuid: String){
        database.reference.child("bands/${bandUuid}").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var bandName = snapshot.child("name").value.toString()
                var bandInfo = snapshot.child("info").value.toString()
                var bandAvatarExtension = snapshot.child("avatar").value.toString()
                var bandBackgroundExtension = snapshot.child("background").value.toString()

                val fbBandAvatar = "storage/bands/${bandName}/profile/avatar.${bandAvatarExtension}"
                val fbBandBack = "storage/bands/${bandName}/profile/avatar.${bandBackgroundExtension}"

                band = Band(
                    bandUuid,
                    bandName,
                    bandInfo,
                    "",
                    ""
                )

                getFilePath(fbBandAvatar, "bandAvatar", -1)
                getFilePath(fbBandBack, "bandBack", -1)


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun getMembers(bandUuid: String){
        var fbAvatarImageUrl: String
        var count = 0
        if (auth.currentUser != null){
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("/bands_has_users/${bandUuid}").children.forEach(
                        Consumer { t ->

                            val memberName = snapshot.child("/users/${t.key}/profile/name").value.toString()
                            var extension = snapshot.child("/users/${t.key}/profile/avatar").value.toString()
                            var memberRoles = t.value.toString()
                            /*t.children.forEach(Consumer { t ->
                                memberRoles += "${t.value.toString()}, "
                            })*/

                            fbAvatarImageUrl = "/storage/users/${t.key}/profile/avatar.${extension}"

                            val member = Member(
                                t.key.toString(),
                                memberName,
                                memberRoles,
                                ""
                            )

                            arrayListMember.add(member)
                            listMember.value = arrayListMember
                            getFilePath(fbAvatarImageUrl, "avatar", count)
                            count++
                        })

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun deleteMember(uuid: String, band: Band):Boolean{
        if (auth.currentUser?.uid != uuid){
        database.reference.child("bands_has_users/${band.uuid}/${uuid}").setValue(null)
        database.reference.child("users_has_bands/${uuid}/${band.uuid}").setValue(null)
            return true
        }
        else return false
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
            "avatar" -> {
                arrayListMember[index].avatarUrl = url
                listMember.value = arrayListMember
            }

            "bandAvatar" -> {
                band.imageUrl = url
                currentBand.value = band
            }
            "bandBack" -> {
                band.backgroundUrl = url
                currentBand.value = band
            }
        }
    }

    fun editBandInfo(bandUuid: String){
        database.reference.child("bands/${bandUuid}/info").setValue(editText.value)
    }

    fun changeImage(bandName:String, bandUuid: String, file: RequestBody, extension: String, image: String){
        if (image == "avatar") {
            getFileUrl("storage/bands/${bandName}/profile/avatar.${extension}", file)
            database.reference.child("bands/${bandUuid}/avatar").setValue(extension)
        }
        if (image == "back") {
            getFileUrl("storage/bands/${bandName}/profile/back.${extension}", file)
            database.reference.child("bands/${bandUuid}/background").setValue(extension)
        }
    }

    fun getFileUrl(url: String, file: RequestBody){
        mService.getUrlForReUpload(url, "true", authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200 || response.code() == 409) {
                    mService.addFile(response.body()!!.href, file, "image/*", "*/*").enqueue(object: Callback<ResponseBody>{
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
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 200)
                    Log.e("CreateBand", "File public")
                else Log.e("CreateBand", response.code().toString())
            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}