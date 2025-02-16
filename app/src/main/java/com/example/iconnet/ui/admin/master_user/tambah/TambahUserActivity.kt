package com.example.iconnet.ui.admin.master_user.tambah

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.RoleData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahUserActivity : AppCompatActivity() {
    private var selectedRoleValue: Int = -1
    private lateinit var spinnerRole: Spinner
    private lateinit var roleList: List<RoleData>

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_user)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val etNama = findViewById<EditText>(R.id.etNama)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etNomorHp = findViewById<EditText>(R.id.etNomorHp)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Spinner Role
        spinnerRole = findViewById(R.id.spinnerRoleUser)
        fetchSpinnerRoles()

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

        // Button Simpan
        val btnSimpanUser: Button = findViewById(R.id.btnSimpanUser)
        btnSimpanUser.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val nomorHp = etNomorHp.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Cek apakah ada yang kosong
            if (TextUtils.isEmpty(nama)) {
                etNama.error = "Nama tidak boleh kosong"
                etNama.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(alamat)) {
                etAlamat.error = "Alamat tidak boleh kosong"
                etAlamat.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(nomorHp)) {
                etNomorHp.error = "Nomor HP tidak boleh kosong"
                etNomorHp.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                etEmail.error = "Email tidak boleh kosong"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(username)) {
                etUsername.error = "Username tidak boleh kosong"
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.error = "Password tidak boleh kosong"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Cek role default ("Pilih Role")
            if (selectedRoleValue == -1) {
                Toast.makeText(this@TambahUserActivity, "Silakan pilih role terlebih dahulu", Toast.LENGTH_SHORT).show()
                spinnerRole.requestFocus()
                return@setOnClickListener
            }

            Log.d("TambahUser", "Nama: $nama")
            Log.d("TambahUser", "Alamat: $alamat")
            Log.d("TambahUser", "Nomor HP: $nomorHp")
            Log.d("TambahUser", "Email: $email")
            Log.d("TambahUser", "Username: $username")
            Log.d("TambahUser", "Password: $password")
            Log.d("TambahUser", "ID Role: $selectedRoleValue")

            Toast.makeText(this@TambahUserActivity, "Simpan user berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSpinnerRoles() {
        RetrofitClient.instance.getRoles().enqueue(object : Callback<ApiResponse<List<RoleData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<RoleData>>>,
                response: Response<ApiResponse<List<RoleData>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    roleList = response.body()?.data ?: listOf()

                    // Ubah data ke format untuk spinner
                    val roleNames = mutableListOf("Pilih Role") // Default
                    val roleMap = mutableMapOf<String, Int>()

                    for (role in roleList) {
                        roleNames.add(role.role)
                        roleMap[role.role] = role.id
                    }

                    val adapter = ArrayAdapter(
                        this@TambahUserActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        roleNames
                    )
                    spinnerRole.adapter = adapter

                    // Event saat item dipilih
                    spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (position != 0) {
                                val selectedRole = roleNames[position]
                                selectedRoleValue = roleMap[selectedRole] ?: -1

                                Log.d("TambahUserActivity", "Role dipilih: $selectedRole")
                                Log.d("TambahUserActivity", "ID Role: $selectedRoleValue")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                } else {
                    Toast.makeText(this@TambahUserActivity, "Gagal mengambil data role", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<RoleData>>>, t: Throwable) {
                Toast.makeText(this@TambahUserActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
