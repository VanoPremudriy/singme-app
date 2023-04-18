package com.example.singmeapp.items

import java.time.LocalDateTime

data class Message(var message: String,
              var senderUuid: String,
              var isReaded: Boolean,
              var dateTime: LocalDateTime
)