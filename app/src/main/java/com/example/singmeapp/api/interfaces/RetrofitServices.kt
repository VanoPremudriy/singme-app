package com.example.singmeapp.api.interfaces
import com.example.singmeapp.api.models.FileApiModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {


    @GET("v1/disk/resources")
    fun getFile(@Query("path") path: String, @Header("Authorization") lang:String): Call<FileApiModel>
}