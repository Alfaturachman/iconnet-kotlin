package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class DetailTeknisi(
    @SerializedName("teknisi_upload_id") val teknisiUploadId: Int,
    @SerializedName("id_pengaduan") val idPengaduan: Int?,
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("file_pengaduan") val filePengaduan: String?,
    @SerializedName("pengaduan_id") val pengaduanId: Int,
    @SerializedName("id_pelanggan") val idPelanggan: String,
    @SerializedName("id_user") val idUser: Int?,
    @SerializedName("instansi_id") val instansiId: Int,
    @SerializedName("tgl_pengaduan") val tglPengaduan: String,
    @SerializedName("judul_pengaduan") val judulPengaduan: String,
    @SerializedName("isi_pengaduan") val isiPengaduan: String,
    @SerializedName("no_telp_pengaduan") val noTelpPengaduan: String?,
    @SerializedName("alamat_pengaduan") val alamatPengaduan: String?,
    @SerializedName("daerah_pengaduan") val daerahPengaduan: String?,
    @SerializedName("status_pengaduan") val statusPengaduan: Int
)