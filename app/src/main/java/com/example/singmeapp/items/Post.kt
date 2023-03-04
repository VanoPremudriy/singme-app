package com.example.singmeapp.items

import java.io.Serializable
import java.time.LocalDateTime

data class Post(val uuid: String,
                val band: Band,
                val album: Album,
                val dateTime: LocalDateTime
                ): Serializable