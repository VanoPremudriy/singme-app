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
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.items.Member
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
import java.util.function.Consumer

class ChatViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listMessages = MutableLiveData<List<Message>>()
    lateinit var arrayListMessages: ArrayList<Message>

    fun getMessages(chatUser: ChatUser){
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}").addValueEventListener(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListMessages = ArrayList<Message>()
                snapshot.children.forEach{ t ->
                       val message = Message(
                           t.child("message").value.toString(),
                           t.child("senderUuid").value.toString(),
                           t.child("is_read").value.toString().toBoolean(),
                           LocalDateTime.parse(t.child("created_at").value.toString())

                       )

                        arrayListMessages.add(message)
                        listMessages.value = arrayListMessages
                    }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun set(){
        /*var message = Message("Hello, fucking beautiduls svlknovlanoaenrgoiw", "CT0bbnzjjjTDddVDUkQDpSBBOFp2")
        database.reference.child("messenger/${auth.currentUser?.uid}/CT0bbnzjjjTDddVDUkQDpSBBOFp2/0").setValue(message)
        message = Message("vvsdlkvkmedrlb;nesrojbnesrjtbheln erg gesngoeagrn eognrao", auth.currentUser?.uid.toString())
        database.reference.child("messenger/${auth.currentUser?.uid}/CT0bbnzjjjTDddVDUkQDpSBBOFp2/1").setValue(message)*/

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(mess: String, chatUser: ChatUser){
        val datetime = LocalDateTime.now().toString()
        val count = listMessages.value?.size ?: 0
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}/${count}/created_at").setValue(datetime)
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}/${count}/senderUuid").setValue(auth.currentUser?.uid.toString())
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}/${count}/message").setValue(mess)
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}/${count}/is_read").setValue(false)

    }

    fun readMessages(chatUser: ChatUser){
        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if (it.child("senderUuid").value.toString() == chatUser.user.uuid){
                        database.reference.child("messenger/${auth.currentUser?.uid}/${chatUser.user.uuid}/${it.key}/is_read").setValue(true)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}