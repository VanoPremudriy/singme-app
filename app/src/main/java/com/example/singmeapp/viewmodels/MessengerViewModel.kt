package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.items.Message
import com.example.singmeapp.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.function.Consumer

class MessengerViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listChatUsers = MutableLiveData<List<ChatUser>>()
    lateinit var arrayListChatUsers: ArrayList<ChatUser>

    var messages = ArrayList<Message>()

    fun getChatUsers() {
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                arrayListChatUsers = ArrayList<ChatUser>()
                snapshot.child("messenger/${auth.currentUser?.uid}").children.forEach {
                    var friendUuid = it.key
                    it.children.forEach {it1 ->
                        messages.add(
                            Message(
                                it1.child("message").value.toString(),
                                it1.child("userUuid").value.toString()
                            )
                        )
                    }
                    var friendName = snapshot.child("users/${friendUuid}/profile/name").value.toString()
                    var friendAge= snapshot.child("users/${friendUuid}/profile/age").value.toString()
                    var friendSex = snapshot.child("users/${friendUuid}/profile/sex").value.toString()
                    var friendMail = snapshot.child("users/${friendUuid}/profile/mail").value.toString()
                    var friendshipStatus = snapshot.child("user_has_friends/${auth.currentUser?.uid}/${friendUuid}")?.value?.toString()

                    var friend = User(
                        it.key.toString(),
                        friendName,
                        friendAge.toInt(),
                        friendSex,
                        friendshipStatus = friendshipStatus ?: "unknown",
                        ""
                    )
                    Log.e("mess", messages[messages.size-1].message.toString())
                    arrayListChatUsers.add(ChatUser(friend, messages[messages.size-1]))
                    listChatUsers.value = arrayListChatUsers
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}