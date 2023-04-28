package com.example.singmeapp.items

import java.time.LocalDateTime

data class Track(var uuid: String,
                 var name: String,
                 var band: String,
                 var album:String,
                 var bandUuid: String,
                 var albumUuid: String,
                 var imageUrl: String,
                 var trackUrl: String,
                 var isInLove: Boolean,
                 val date: LocalDateTime? = null)
