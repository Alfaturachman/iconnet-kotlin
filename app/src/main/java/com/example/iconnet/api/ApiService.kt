package com.example.iconnet.api

import com.example.iconnet.model.LoginRequest
import com.example.iconnet.model.UserData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("login.php")  // Sesuaikan dengan endpoint API
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<UserData>>
}
