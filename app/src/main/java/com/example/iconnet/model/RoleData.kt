package com.example.iconnet.model

import com.google.gson.annotations.SerializedName

data class RoleData(
    @SerializedName("id") val id: Int,
    @SerializedName("role") val role: String
)
