package com.example.singmeapp.api.models

data class FileApiModel(
    val antivirus_status: String,
    val comment_ids: CommentIds,
    val created: String,
    val exif: Exif,
    val `file`: String,
    val md5: String,
    val media_type: String,
    val mime_type: String,
    val modified: String,
    val name: String,
    val path: String,
    val public_key: String,
    val public_url: String,
    val resource_id: String,
    val revision: Long,
    val sha256: String,
    val size: Int,
    val type: String
)

data class CommentIds(
    val private_resource: String,
    val public_resource: String
)

class Exif