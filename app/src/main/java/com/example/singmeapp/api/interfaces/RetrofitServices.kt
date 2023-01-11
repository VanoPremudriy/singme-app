package com.example.singmeapp.api.interfaces
import com.example.singmeapp.api.models.TrackApiModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {


    @GET("v1/disk/resources")
    fun getTrack(@Query("path") path: String, @Header("Authorization") lang:String): Call<TrackApiModel>
}