package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("nama_instansi") val namaInstansi: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("no_hp") val noHp: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("role_id") val roleId: Int?
)