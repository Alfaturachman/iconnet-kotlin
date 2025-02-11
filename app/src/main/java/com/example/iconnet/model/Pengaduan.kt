package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class Pengaduan(
    @SerializedName("id_pengaduan") val idPengaduan: String,
    @SerializedName("id_teknisi") val idTeknisi: String,
    @SerializedName("id_user") val idUser: String,
    @SerializedName("id_pelanggan") val idPelanggan: String,
    @SerializedName("nama_user") val namaUser: String,
    @SerializedName("tgl_pengaduan") val tglPengaduan: String,
    @SerializedName("judul_pengaduan") val judulPengaduan: String,
    @SerializedName("isi_pengaduan") val isiPengaduan: String,
    @SerializedName("no_telp_pengaduan") val noTelpPengaduan: String,
    @SerializedName("alamat_pengaduan") val alamatPengaduan: String,
    @SerializedName("daerah_pengaduan") val daerahPengaduan: String,
    @SerializedName("status_pengaduan") val statusPengaduan: String
)
