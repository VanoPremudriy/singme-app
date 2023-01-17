package com.example.singmeapp.api.interfaces
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {

    @GET("v1/disk/resources")
    fun getFile(@Query("path") path: String, @Header("Authorization") lang:String): Call<FileApiModel>

    @GET("v1/disk/public/resources/download")
    fun getSecondFile(@Query("public_key") publicKey: String, @Header("Authorization") land: String): Call<SecondFileApiModel>
}