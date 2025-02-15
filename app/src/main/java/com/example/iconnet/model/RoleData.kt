package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class RoleData(
    @SerializedName("id") val id: String,
    @SerializedName("role") val role: String
)
