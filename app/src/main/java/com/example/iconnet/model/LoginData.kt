package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role") val role: String
)