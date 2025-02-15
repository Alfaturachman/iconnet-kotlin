package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class AllUser(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_instansi") val namaInstansi: String,
    @SerializedName("email") val email: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("no_hp") val noHp: String,
    @SerializedName("username") val username: String,
    @SerializedName("role_name") val roleName: String
)
