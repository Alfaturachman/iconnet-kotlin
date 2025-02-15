package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class TeknisiData(
    @SerializedName("id") val idTeknisi: Int,
    @SerializedName("nama_teknisi") val namaTeknisi: String
)