package com.example.singmeapp.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.api.Common.Common
import com.example.singmeapp.api.interfaces.RetrofitServices
import com.example.singmeapp.api.models.SecondFileApiModel
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Member
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


class CreateBandViewModel: ViewModel() {
    private val authToken = "y0_AgAAAAAGPsvAAADLWwAAAADZbKmDfz8x-nCuSJ-i7cNOGYhnyRVPBUc"
    var mService: RetrofitServices = Common.retrofitService
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = Firebase.database
    val isExist = MutableLiveData<Boolean>()

    fun createBand(band: Band, avatar: RequestBody, back: RequestBody, avatarExtension: String, backExtension: String){
        val uuid = java.util.UUID.randomUUID()
        band.uuid = uuid.toString()
        database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("band_exist").child(band.name).exists()){
                    isExist.value = true
                }
                else {
                    createDir("/storage/bands/${band.name}", avatar, "avatar.${avatarExtension}")
                    createDir("/storage/bands/${band.name}", back, "back.${backExtension}")
                    database.reference.child("bands/${band.uuid}").child("name").setValue(band.name)
                    database.reference.child("band_exist").child(band.name).setValue(band.uuid)
                    database.reference.child("bands/${band.uuid}").child("info").setValue(band.info)
                    database.reference.child("bands/${band.uuid}").child("avatar").setValue(avatarExtension)
                    database.reference.child("bands/${band.uuid}").child("background").setValue(backExtension)
                    database.reference.child("bands_has_users").child(band.uuid).child(auth.currentUser!!.uid).setValue("Вокалист")
                    database.reference.child("users_has_bands").child(auth.currentUser!!.uid).child(band.uuid).setValue("Вокалист")
                }


            }

            override fun onCancelled(error: DatabaseError) {

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
                    createProfileDir("${url}/profile",file, name)
                }
                else Log.e("Responce", "Dir Not Created")

            }

            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun createProfileDir(url: String, file: RequestBody, name: String){
        mService.createDir(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                if (response.code() == 201 || response.code() == 409) {
                    getFileUrl("${url}/${name}", file)
                    Log.e("Response", "Dir Created")
                }
                else Log.e("Response", "Dir Not Created")

            }


            override fun onFailure(call: Call<SecondFileApiModel>, t: Throwable) {

            }

        })
    }

    fun getFileUrl(url: String, file: RequestBody){
        //Log.e("Response", file.body().toString())
        mService.getUrlForUpload(url, authToken).enqueue(object :
            Callback<SecondFileApiModel> {
            override fun onResponse(
                call: Call<SecondFileApiModel>,
                response: Response<SecondFileApiModel>
            ) {
                Log.e("Response", url)
                Log.e("Response", response.code().toString())
                Log.e("Response", response.body().toString())
                if (response.code() == 200) {
                    Log.e("Responce", "URL Getted")
                    mService.addFile(response.body()!!.href, file, "image/*", "*/*").enqueue(object: Callback<ResponseBody>{
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            Log.e("Response", "File added")
                            Log.e("Response", response.message())
                            publicFile(url)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("Response", "File not created")
                            Log.e("Response", t.toString())
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