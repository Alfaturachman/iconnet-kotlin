package com.example.iconnet.api

import com.example.iconnet.model.AllUser
import com.example.iconnet.model.CreateUserRequest
import com.example.iconnet.model.DashboardData
import com.example.iconnet.model.DeleteRequest
import com.example.iconnet.model.LoginRequest
import com.example.iconnet.model.RegisterData
import com.example.iconnet.model.RegisterRequest
import com.example.iconnet.model.LoginData
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.model.PengaduanRequest
import com.example.iconnet.model.RoleData
import com.example.iconnet.model.TeknisiData
import com.example.iconnet.model.TotalStatsResponse
import com.example.iconnet.model.UpdatePengaduanRequest
import com.example.iconnet.model.UpdateUserRequest
import com.example.iconnet.model.UploadTugasRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<LoginData>>

    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse<RegisterData>>

    @GET("get_all_pengaduan.php")
    fun getPengaduan(): Call<ApiResponse<List<Pengaduan>>>

    @POST("get_pengaduan_id_user.php")
    fun getPengaduan(@Body requestBody: Map<String, Int>): Call<ApiResponse<List<Pengaduan>>>

    @GET("get_dashboard_admin.php")
    fun getRekapDataPengaduanAdmin(): Call<ApiResponse<DashboardData>>

    @Headers("Content-Type: application/json")
    @POST("get_dashboard_user.php")
    fun getRekapDataPengaduanUser(@Body request: Map<String, Int>): Call<ApiResponse<DashboardData>>

    @GET("get_stats_admin.php")
    fun getStatsPengaduanAdmin(): Call<ApiResponse<List<TotalStatsResponse>>>

    @Headers("Content-Type: application/json")
    @POST("get_stats_user.php")
    fun getStatsPengaduanUser(@Body request: Map<String, Int>): Call<ApiResponse<List<TotalStatsResponse>>>

    @GET("get_all_teknisi.php")
    fun getTeknisi(): Call<ApiResponse<List<TeknisiData>>>

    @Headers("Content-Type: application/json")
    @POST("update_tugas_teknisi.php")
    fun updatePengaduan(@Body request: UpdatePengaduanRequest): Call<ApiResponse<UpdatePengaduanRequest>>

    @GET("get_all_user.php")
    fun getAllUsers(): Call<ApiResponse<List<AllUser>>>

    @GET("get_role.php")
    fun getRoles(): Call<ApiResponse<List<RoleData>>>

    @POST("post_user.php")
    fun tambahUser(@Body request: CreateUserRequest): Call<ApiResponse<CreateUserRequest>>

    @POST("update_user.php")
    fun updateUser(@Body request: UpdateUserRequest): Call<ApiResponse<UpdateUserRequest>>

    @Headers("Content-Type: application/json")
    @POST("delete_user.php")
    fun deleteUser(@Body request: DeleteRequest): Call<ApiResponse<DeleteRequest>>

    // USER
    @POST("post_pengaduan_user.php")
    fun tambahPengaduan(@Body request: PengaduanRequest): Call<ApiResponse<PengaduanRequest>>

    @Headers("Content-Type: application/json")
    @POST("get_pengaduan_id_pengaduan.php")
    fun getPengaduanByIdPengaduan(@Body request: Map<String, Int>): Call<ApiResponse<Pengaduan>>

    @POST("get_tugas_teknisi.php")
    fun getTugasTeknisi(@Body requestBody: Map<String, Int>): Call<ApiResponse<List<Pengaduan>>>

    @Multipart
    @POST("post_tugas_teknisi.php")
    fun uploadTugas(
        @Part("id_pengaduan") idPengaduan: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("status_pengaduan") status: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ApiResponse<UploadTugasRequest>>
}
