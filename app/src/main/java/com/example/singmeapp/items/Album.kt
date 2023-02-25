package com.example.singmeapp.items

import java.io.Serializable
import java.time.LocalDateTime

data class Album(val uuid: String,
                 val name: String,
                 val band: String,
                 val year: Int,
                 val isInLove: Boolean,
                 val isAuthor: Boolean,
                 var imageUrl: String,
                 var date: LocalDateTime? = null
): Serializable