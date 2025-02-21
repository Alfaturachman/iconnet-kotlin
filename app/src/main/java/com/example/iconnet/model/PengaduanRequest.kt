package com.example.iconnet.model

data class PengaduanRequest(
    val instansi_id: Int,
    val judul_pengaduan: String,
    val isi_pengaduan: String,
    val no_telp_pengaduan: String,
    val alamat_pengaduan: String
)
