package com.example.singmeapp.api.Common

import com.example.singmeapp.api.RetrofitClient
import com.example.singmeapp.api.interfaces.RetrofitServices

object Common {
    private const val BASE_URL = "https://cloud-api.yandex.net/"
    val retro = RetrofitClient
    val retrofitService: RetrofitServices
        get() = retro.getClient(BASE_URL).create(RetrofitServices::class.java)

}

