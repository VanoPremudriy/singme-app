package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.items.Message
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
import java.time.LocalDateTime

class MessengerViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listChatUsers = MutableLiveData<List<ChatUser>>()
    var arrayListChatUsers = ArrayList<ChatUser>()

    var messages = ArrayList<Message>()

    var isAlready = MutableLiveData<HashMap<String, Boolean>>(HashMap())

    fun getChatUsers() {
        var fbAvatarUrl: String
        var fbAvatarUrls = HashMap<String, String>()
        database.reference.addValueEventListener(object : ValueEventListener {
            @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                arrayListChatUsers.clear()
                snapshot.child("messenger/${auth.currentUser?.uid}").children.forEach { it ->
                    var friendUuid = it.key
                    val messCount = it.childrenCount
                    var message = Message(
                            it.child("${messCount-1}/message").value.toString(),
                            it.child("${messCount-1}/senderUuid").value.toString(),
                            it.child("${messCount-1}/is_read").value.toString().toBoolean(),
                            LocalDateTime.parse(it.child("${messCount-1}/created_at").value.toString())

                        )



                    var friendName = snapshot.child("users/${friendUuid}/profile/name").value.toString()
                    var friendAge= snapshot.child("users/${friendUuid}/profile/age").value.toString()
                    var friendSex = snapshot.child("users/${friendUuid}/profile/sex").value.toString()
                    var friendMail = snapshot.child("users/${friendUuid}/profile/mail").value.toString()
                    var friendshipStatus = snapshot.child("user_has_friends/${auth.currentUser?.uid}/${friendUuid}")?.value?.toString()
                    var avatarExtension = snapshot.child("users/${friendUuid}/profile/avatar").value.toString()

                    if (avatarExtension != "null"){
                        fbAvatarUrl = "/storage/users/${friendUuid}/profile/avatar.${avatarExtension}"
                    } else {
                        fbAvatarUrl = "/storage/default_images/cover.png"
                    }

                    fbAvatarUrls.put(it.key.toString(), fbAvatarUrl)

                    var friend = User(
                        it.key.toString(),
                        friendName,
                        friendAge.toInt(),
                        friendSex,
                        friendshipStatus = friendshipStatus ?: "unknown",
                        friendshipStatusForFragment = friendshipStatus ?: "unknown",
                        ""
                    )
                    arrayListChatUsers.add(ChatUser(friend, message))
                }
                if (arrayListChatUsers.size != 0) {
                    arrayListChatUsers.sortBy {
                        it.lastMessage?.dateTime
                    }
                    arrayListChatUsers.reverse()
                    listChatUsers.value = arrayListChatUsers
                    arrayListChatUsers.forEach {
                        getFilePath(fbAvatarUrls[it.user.uuid]!!, "avatarChatUser", count)
                        count++
                    }
                } else {
                    isAlready.value?.put("avatarChatUser", true)
                    isAlready.value = isAlready.value
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
                    Log.e("Track", url)
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
            "avatarChatUser" -> {
                arrayListChatUsers[index].user.avatarUrl = url
                listChatUsers.value = arrayListChatUsers
            }
        }
        if (index == arrayListChatUsers.size -1 && value == "avatarChatUser"){
            isAlready.value?.put("avatarChatUser", true)
            isAlready.value = isAlready.value
        }
    }
}