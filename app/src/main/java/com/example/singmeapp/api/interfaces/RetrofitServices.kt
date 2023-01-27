package com.example.singmeapp.api.interfaces
import android.graphics.Bitmap
import com.example.singmeapp.api.models.FileApiModel
import com.example.singmeapp.api.models.SecondFileApiModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {

    @GET("v1/disk/resources")
    fun getFile(@Query("path") path: String, @Header("Authorization") lang:String): Call<FileApiModel>

    @GET("v1/disk/public/resources/download")
    fun getSecondFile(@Query("public_key") publicKey: String, @Header("Authorization") land: String): Call<SecondFileApiModel>

    @GET("v1/disk/resources/upload")
    fun getUrlForUpload(@Query("path") path: String, @Header("Authorization") land: String): Call<SecondFileApiModel>

    @PUT("v1/disk/resources")
    fun createDir(@Query("path") path: String, @Header("Authorization") land: String): Call<SecondFileApiModel>

    @PUT
    fun addFile(@Url url:String, @Body requestBody: RequestBody, @Header("Content-Type") contentType: String, @Header("Accept") accept: String): Call<ResponseBody>

    @PUT("v1/disk/resources/publish")
    fun publishFile(@Query("path") path: String, @Header("Authorization") land: String): Call<SecondFileApiModel>


}