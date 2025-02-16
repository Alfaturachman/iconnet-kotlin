package com.example.iconnet.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.MainActivity
import com.example.iconnet.R
import com.example.iconnet.api.*
import com.example.iconnet.model.LoginRequest
import com.example.iconnet.model.LoginData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        etUsername = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        var isPasswordVisible = false
        etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etPassword.compoundDrawablesRelative[2] // Ambil drawableEnd
                if (drawableEnd != null && event.rawX >= (etPassword.right - drawableEnd.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_pass_eye, 0)
                    } else {
                        etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_pass_eye_off, 0)
                    }
                    etPassword.typeface = Typeface.DEFAULT // **Menjaga font tetap Roboto**
                    etPassword.setSelection(etPassword.text.length) // Menjaga posisi kursor
                    return@setOnTouchListener true
                }
            }
            false
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                etUsername.error = "Username is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            loginUser()
        }
    }

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val request = LoginRequest(username, password)

        btnLogin.isEnabled = false

        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(call: Call<ApiResponse<LoginData>>, response: Response<ApiResponse<LoginData>>) {
                btnLogin.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    if (loginResponse.status) {
                        val userData = loginResponse.data

                        // Simpan session user ke SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putInt("id_user", userData.id)
                            putString("email", userData.email)
                            putString("nama", userData.nama)
                            putString("role", userData.role)
                            apply()
                        }

                        // Log untuk memastikan data tersimpan
                        val storedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE)
                        Log.d("UserSession", "ID: ${storedPrefs.getInt("id_user", 0)}")
                        Log.d("UserSession", "Email: ${storedPrefs.getString("email", "null")}")
                        Log.d("UserSession", "Nama: ${storedPrefs.getString("nama", "null")}")
                        Log.d("UserSession", "Role: ${storedPrefs.getString("role", "null")}")

                        Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()

                        // Pindah ke MainActivity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e("LoginError", "Login gagal: ${loginResponse.message ?: "Pesan error tidak tersedia"}")
                        Toast.makeText(this@LoginActivity, loginResponse.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginError", "Gagal login: ${response.code()} - ${errorBody ?: "Response body kosong"}")
                    Toast.makeText(this@LoginActivity, "Gagal login, coba lagi", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                btnLogin.isEnabled = true
                Log.e("LoginFailure", "Terjadi kesalahan jaringan: ${t.message}", t)
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
