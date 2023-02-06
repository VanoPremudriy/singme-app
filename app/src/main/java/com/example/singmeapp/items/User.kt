package com.example.singmeapp.items

import java.io.Serializable

data class User(var uuid: String,
                var name: String,
                val age: Int,
                val sex: String,
                var friendshipStatus: String,
                var friendshipStatusForFragment: String,
                var avatarUrl: String): Serializable