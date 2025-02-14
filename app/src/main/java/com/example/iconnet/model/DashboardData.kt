package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class DashboardData(
    @SerializedName("totalUserPelanggan") val totalUserPelanggan: Int,
    @SerializedName("totalUserTeknisi") val totalUserTeknisi: Int,
    @SerializedName("totalPengaduanAntrian") val totalPengaduanAntrian: Int,
    @SerializedName("totalPengaduanProses") val totalPengaduanProses: Int,
    @SerializedName("totalPengaduanSelesai") val totalPengaduanSelesai: Int,
    @SerializedName("totalPengaduanBatal") val totalPengaduanBatal: Int
)
