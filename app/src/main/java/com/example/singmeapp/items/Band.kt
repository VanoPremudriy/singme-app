package com.example.singmeapp.items

import java.io.Serializable
import java.time.LocalDateTime

data class Band(var uuid: String,
                var name: String,
                var info: String,
                var imageUrl: String,
                var backgroundUrl: String,
                var date: LocalDateTime? = null
): Serializable