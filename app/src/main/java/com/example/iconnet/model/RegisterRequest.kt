package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nama_instansi") val namaInstansi: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("no_hp") val noHp: String
)