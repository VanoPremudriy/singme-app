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
import java.util.function.Consumer

class ChooseMemberViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database


    var listChooseMembers = MutableLiveData<List<User>>()
    lateinit var arrayListChooseMembers: ArrayList<User>

    fun getChooseMembers() {
        var fbFriendAvatarUrl: String
        if (auth.currentUser != null) {
            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    arrayListChooseMembers = ArrayList<User>()
                    listChooseMembers.value = arrayListChooseMembers
                    snapshot.child("/user_has_friends/${auth.currentUser?.uid}").children.forEach(Consumer { t ->
                        val friendName = snapshot.child("users/${t.key}/profile/name").value.toString()
                        val friendAge = snapshot.child("users/${t.key}/profile/age").value.toString()
                        val friendSex = snapshot.child("users/${t.key}/profile/sex").value.toString()
                        val avatarExtension = snapshot.child("users/${t.key}/profile/avatar").value.toString()
                        val friendshipStatus = t.value.toString()

                        fbFriendAvatarUrl = "/storage/users/${t.key}/profile/avatar.${avatarExtension}"

                        val friend = User(
                            t.key.toString(),
                            friendName,
                            friendAge.toInt(),
                            friendSex,
                            friendshipStatus,
                            friendshipStatus,
                            ""
                        )

                        if (friend.friendshipStatus == "friend"){
                            arrayListChooseMembers.add(friend)
                            listChooseMembers.value = arrayListChooseMembers
                            getFilePath(fbFriendAvatarUrl, "avatar", count)
                            count++
                        }
                    })
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
    }
}