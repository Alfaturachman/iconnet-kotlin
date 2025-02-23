package com.example.iconnet.model

data class UploadTugasRequest(
    val id_pengaduan: Int,
    val keterangan: String?,
    val file_pengaduan: String?
)
