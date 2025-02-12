package com.example.iconnet.api

import com.example.iconnet.model.LoginRequest
import com.example.iconnet.model.RegisterData
import com.example.iconnet.model.RegisterRequest
import com.example.iconnet.model.LoginData
import com.example.iconnet.model.Pengaduan
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<LoginData>>

    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse<RegisterData>>

    @GET("get_all_pengaduan.php")
    fun getPengaduan(): Call<ApiResponse<List<Pengaduan>>>

    @POST("get_pengaduan_by_id.php")
    fun getPengaduan(@Body requestBody: Map<String, Int>): Call<ApiResponse<List<Pengaduan>>>

    @POST("get_tugas_teknisi.php")
    fun getTugasTeknisi(@Body requestBody: Map<String, Int>): Call<ApiResponse<List<Pengaduan>>>
}
