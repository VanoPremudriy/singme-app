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
import com.example.singmeapp.items.Friend
import com.example.singmeapp.items.Track
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

class FriendsViewModel: ViewModel() {

    var listFriends = MutableLiveData<List<User>>()
    //var arrayListFriends = ArrayList<User>()
    lateinit var arrayListFriends: ArrayList<User>

    var listRequests = MutableLiveData<List<User>>()
    //var arrayListRequests = ArrayList<User>()
    lateinit var arrayListRequests:ArrayList<User>

    var listMyRequests = MutableLiveData<List<User>>()
    //var arrayListMyRequests = ArrayList<User>()
    lateinit var arrayListMyRequests:ArrayList<User>

    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database

    var listAllUsers = MutableLiveData<List<User>>()
    lateinit var arrayListAllUsers: ArrayList<User>


    fun getFriends(){
        var fbFriendAvatarUrl: String
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var friendCount = 0
                var requestCount = 0
                var myRequestCount = 0
                arrayListFriends = ArrayList<User>()
                arrayListRequests = ArrayList<User>()
                arrayListMyRequests = ArrayList<User>()
                listMyRequests.value = arrayListMyRequests
                listFriends.value = arrayListFriends
                listRequests.value = arrayListRequests
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
                        arrayListFriends.add(friend)
                        listFriends.value = arrayListFriends
                        getFilePath(fbFriendAvatarUrl, "avatarFriend", friendCount)
                        friendCount++
                    }

                    if (friend.friendshipStatus == "request"){
                        arrayListRequests.add(friend)
                        listRequests.value = arrayListRequests
                        getFilePath(fbFriendAvatarUrl, "avatarRequest", requestCount)
                        requestCount++
                    }

                    if (friend.friendshipStatus == "my request"){
                        arrayListMyRequests.add(friend)
                        listMyRequests.value = arrayListMyRequests
                        getFilePath(fbFriendAvatarUrl, "avatarMyRequest", myRequestCount)
                        myRequestCount++
                    }


                })

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun getOtherFriends(uuid: String){
        var fbFriendAvatarUrl: String
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var friendCount = 0
                var requestCount = 0
                var myRequestCount = 0
                arrayListFriends = ArrayList<User>()
                arrayListRequests = ArrayList<User>()
                arrayListMyRequests = ArrayList<User>()
                listMyRequests.value = arrayListMyRequests
                listFriends.value = arrayListFriends
                listRequests.value = arrayListRequests
                snapshot.child("/user_has_friends/${uuid}").children.forEach(Consumer { t ->
                    val friendName = snapshot.child("users/${t.key}/profile/name").value.toString()
                    val friendAge = snapshot.child("users/${t.key}/profile/age").value.toString()
                    val friendSex = snapshot.child("users/${t.key}/profile/sex").value.toString()
                    val avatarExtension = snapshot.child("users/${t.key}/profile/avatar").value.toString()
                    val friendshipStatusForFragment = t.value.toString()
                    val friendshipStatus = snapshot.child("/user_has_friends/${auth.currentUser?.uid}/${t.key}").value.toString()

                    fbFriendAvatarUrl = "/storage/users/${t.key}/profile/avatar.${avatarExtension}"

                    val friend = User(
                        t.key.toString(),
                        friendName,
                        friendAge.toInt(),
                        friendSex,
                        friendshipStatus,
                        friendshipStatusForFragment,
                        ""
                    )

                    if (friend.friendshipStatusForFragment == "friend"){
                        arrayListFriends.add(friend)
                        listFriends.value = arrayListFriends
                        getFilePath(fbFriendAvatarUrl, "avatarFriend", friendCount)
                        friendCount++
                    }

                    if (friend.friendshipStatusForFragment == "request"){
                        arrayListRequests.add(friend)
                        listRequests.value = arrayListRequests
                        getFilePath(fbFriendAvatarUrl, "avatarRequest", requestCount)
                        requestCount++
                    }

                    if (friend.friendshipStatusForFragment == "my request"){
                        arrayListMyRequests.add(friend)
                        listMyRequests.value = arrayListMyRequests
                        getFilePath(fbFriendAvatarUrl, "avatarMyRequest", myRequestCount)
                        myRequestCount++
                    }


                })

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    fun getAllUsers(){
        var fbAvatarUrl: String

        database.reference.addValueEventListener(object : ValueEventListener{
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                arrayListAllUsers = ArrayList<User>()
                listAllUsers.value = arrayListAllUsers
                snapshot.child("users").children.forEach {
                    val userName = it.child("profile/name").value.toString()
                    val userAge = it.child("profile/age").value.toString()
                    val userSex = it.child("profile/sex").value.toString()
                    val avatarExtension = it.child("profile/avatar").value.toString()
                    val friendShipStatus:String? = snapshot.child("user_has_friends/${auth.currentUser?.uid}/${it.key}").value?.toString()

                    fbAvatarUrl = "/storage/users/${it.key}/profile/avatar.${avatarExtension}"

                    val user = User(
                        it.key.toString(),
                        userName,
                        userAge.toInt(),
                        userSex,
                        friendshipStatus = friendShipStatus ?: "unknown",
                        friendshipStatusForFragment = friendShipStatus ?: "unknown",
                        ""
                    )

                    Log.e("sts", user.friendshipStatus)

                    arrayListAllUsers.add(user)
                    listAllUsers.value = arrayListAllUsers
                    getFilePath(fbAvatarUrl, "avatarUser", count)
                    count++
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    fun sendRequest(uuid: String){
        database.reference.child("user_has_friends/${auth.currentUser?.uid}/${uuid}").setValue("my request")
        database.reference.child("user_has_friends/${uuid}/${auth.currentUser?.uid}").setValue("request")
        getFriends()
    }

    fun deleteFriend(uuid: String){
        database.reference.child("user_has_friends/${auth.currentUser?.uid}/${uuid}").setValue(null)
        database.reference.child("user_has_friends/${uuid}/${auth.currentUser?.uid}").setValue(null)
        getFriends()
    }

    fun applyRequest(uuid: String){
        database.reference.child("user_has_friends/${auth.currentUser?.uid}/${uuid}").setValue("friend")
        database.reference.child("user_has_friends/${uuid}/${auth.currentUser?.uid}").setValue("friend")
        getFriends()
    }

    fun cancelRequest(uuid: String){
        database.reference.child("user_has_friends/${auth.currentUser?.uid}/${uuid}").setValue(null)
        database.reference.child("user_has_friends/${uuid}/${auth.currentUser?.uid}").setValue(null)
        getFriends()
    }

    fun cancelMyRequest(uuid: String){
        database.reference.child("user_has_friends/${auth.currentUser?.uid}/${uuid}").setValue(null)
        database.reference.child("user_has_friends/${uuid}/${auth.currentUser?.uid}").setValue(null)
        getFriends()
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
            "avatarFriend" -> {
                arrayListFriends[index].avatarUrl = url
                listFriends.value = arrayListFriends
            }

            "avatarRequest" ->{
                arrayListRequests[index].avatarUrl = url
                listRequests.value = arrayListRequests
            }

            "avatarMyRequest" ->{
                arrayListMyRequests[index].avatarUrl = url
                listMyRequests.value = arrayListMyRequests
            }

            "avatarUser" ->{
                arrayListAllUsers[index].avatarUrl = url
                listAllUsers.value = arrayListAllUsers
            }
        }
    }


}