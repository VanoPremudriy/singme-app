package com.example.singmeapp.items

import java.io.Serializable

data class ChatUser(var user: User, var lastMessage: Message?): Serializable