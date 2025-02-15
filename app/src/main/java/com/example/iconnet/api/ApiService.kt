package com.example.iconnet.api

import com.example.iconnet.model.AllUser
import com.example.iconnet.model.DashboardData
import com.example.iconnet.model.LoginRequest
import com.example.iconnet.model.RegisterData
import com.example.iconnet.model.RegisterRequest
import com.example.iconnet.model.LoginData
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.model.TeknisiData
import com.example.iconnet.model.TotalStatsAdmin
import com.example.iconnet.model.UpdatePengaduanRequest
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

    @GET("get_dashboard_admin.php")
    fun getDashboardData(): Call<ApiResponse<DashboardData>>

    @GET("get_stats_admin.php")
    fun getPengaduanData(): Call<ApiResponse<List<TotalStatsAdmin>>>

    @GET("get_all_teknisi.php")
    fun getTeknisi(): Call<ApiResponse<List<TeknisiData>>>

    @Headers("Content-Type: application/json")
    @POST("update_tugas_teknisi.php")
    fun updatePengaduan(@Body request: UpdatePengaduanRequest): Call<ApiResponse<UpdatePengaduanRequest>>

    @GET("get_all_user.php")
    fun getAllUsers(): Call<ApiResponse<List<AllUser>>>
}
