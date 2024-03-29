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
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period
import java.util.function.Consumer

class ChooseMemberViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database


    var listChooseMembers = MutableLiveData<List<User>>()
    var arrayListChooseMembers =  ArrayList<User>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun getChooseMembers() {
        var fbFriendAvatarUrl: String
        var fbFriendAvatarUrls = HashMap<String, String>()
        var count = 0
        if (auth.currentUser != null) {
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListChooseMembers.clear()
                    listChooseMembers.value = arrayListChooseMembers
                    snapshot.child("/user_has_friends/${auth.currentUser?.uid}").children.forEach{ t ->
                        val friendshipStatus = t.value.toString()
                        if (friendshipStatus == "friend") {
                            val friendName =
                                snapshot.child("users/${t.key}/profile/name").value.toString()
                            val friendSex =
                                snapshot.child("users/${t.key}/profile/sex").value.toString()
                            val avatarExtension =
                                snapshot.child("users/${t.key}/profile/avatar").value.toString()
                            val birthday = snapshot.child("users/${t.key}/profile/birthday").value.toString()
                            var birthdayDateTime = LocalDate.parse(birthday)

                            var friendAge = Period.between(birthdayDateTime, LocalDate.now()).years

                            fbFriendAvatarUrl =
                                "/storage/users/${t.key}/profile/avatar.${avatarExtension}"
                            fbFriendAvatarUrls.put(t.key.toString(), fbFriendAvatarUrl)

                            val friend = User(
                                t.key.toString(),
                                friendName,
                                friendAge.toInt(),
                                friendSex,
                                friendshipStatus,
                                friendshipStatus,
                                ""
                            )

                            arrayListChooseMembers.add(friend)
                        }

                        if (arrayListChooseMembers.size != 0) {
                            listChooseMembers.value = arrayListChooseMembers
                            arrayListChooseMembers.forEach {
                                getFilePath(fbFriendAvatarUrls.get(it.uuid)!!, "avatar", count)
                                count++
                            }
                        } else {
                            isAlready.value?.put("avatar", true)
                            isAlready.value = isAlready.value
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun addNewMember(uuid: String, roles: ArrayList<String>, band: Band){
        database.reference.child("bands_has_users/${band.uuid}/${uuid}").setValue(roles)
        database.reference.child("users_has_bands/${uuid}/${band.uuid}").setValue(roles)
    }

    fun getFilePath(url: String, value: String, index: Int){
        mService.getFile(url, authToken)
            .enqueue(object : Callback<FileApiModel> {
                override fun onResponse(
                    call: Call<FileApiModel>,
                    response: Response<FileApiModel>
                ) {
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
                arrayListChooseMembers[index].avatarUrl = url
                listChooseMembers.value = arrayListChooseMembers
            }
        }

        if (index == arrayListChooseMembers.size -1 && value == "avatar"){
            isAlready.value?.put("avatar", true)
            isAlready.value = isAlready.value
        }
    }
}