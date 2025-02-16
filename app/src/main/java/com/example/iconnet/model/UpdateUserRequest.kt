package com.example.iconnet.model

data class UpdateUserRequest(
    val id: Int,
    val nama_instansi: String?,
    val email: String?,
    val alamat: String?,
    val no_hp: String?,
    val username: String?,
    val password: String?,
    val role_id: Int?
)
