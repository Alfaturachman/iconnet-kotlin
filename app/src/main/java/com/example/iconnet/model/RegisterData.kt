package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class RegisterData (
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("role_id") val role_id: Int
)