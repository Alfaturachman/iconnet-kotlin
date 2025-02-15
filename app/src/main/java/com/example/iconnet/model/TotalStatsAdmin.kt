package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class TotalStatsAdmin(
    @SerializedName("tahun")
    val tahun: Int,

    @SerializedName("bulan")
    val bulan: String,

    @SerializedName("total_pengaduan")
    val totalPengaduan: TotalPengaduan
)

data class TotalPengaduan(
    @SerializedName("antrian")
    val antrian: Int,

    @SerializedName("proses")
    val proses: Int,

    @SerializedName("selesai")
    val selesei: Int,

    @SerializedName("batal")
    val batal: Int
)