package com.example.iconnet.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iconnet.MainActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.RegisterData
import com.example.iconnet.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private lateinit var etNamaInstansi: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHp: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        etNamaInstansi = findViewById(R.id.etNamaInstansi)
        etAlamat = findViewById(R.id.etAlamat)
        etNoHp = findViewById(R.id.etNoHp)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val namaInstansi = etNamaInstansi.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val noHp = etNoHp.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (TextUtils.isEmpty(namaInstansi)) {
                etNamaInstansi.error = "Nama Instansi is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(alamat)) {
                etAlamat.error = "Alamat is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(noHp)) {
                etNoHp.error = "No. HP is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(username)) {
                etUsername.error = "Username is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(namaInstansi, alamat, username, password, email, noHp)

            RetrofitClient.instance.registerUser(registerRequest).enqueue(object : Callback<ApiResponse<RegisterData>> {
                override fun onResponse(call: Call<ApiResponse<RegisterData>>, response: Response<ApiResponse<RegisterData>>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val userData = response.body()?.data
                        if (userData != null) {
                            Toast.makeText(this@RegisterActivity, "Register Berhasil", Toast.LENGTH_SHORT).show()

                            // Redirect ke LoginActivity
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("RegisterError", "Response failed: Code=${response.code()}, Message=${response.message()}, ErrorBody=$errorBody")

                        Toast.makeText(this@RegisterActivity, "${response.body()?.message ?: "Username atau Email sudah ada!"}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<RegisterData>>, t: Throwable) {
                    Log.e("RegisterError", "Request failed: ${t.message}", t)
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}