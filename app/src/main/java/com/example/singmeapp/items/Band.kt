package com.example.singmeapp.items

import java.io.Serializable

data class Band(var uuid: String,
                var name: String,
                var info: String,
                var imageUrl: String,
                var backgroundUrl: String): Serializable