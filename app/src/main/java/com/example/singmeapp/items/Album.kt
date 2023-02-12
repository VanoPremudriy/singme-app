package com.example.singmeapp.items

import java.io.Serializable

data class Album(val uuid: String,
                 val name: String,
                 val band: String,
                 val year: Int,
                 var imageUrl: String): Serializable