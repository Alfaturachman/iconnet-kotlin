package com.example.iconnet.model

data class UpdatePengaduanRequest(
    val id_pengaduan: Int,
    val id_user: Int?,
    val daerah_pengaduan: String?
)
