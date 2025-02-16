package com.example.iconnet.ui.admin.master_user.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.CreateUserRequest
import com.example.iconnet.model.RoleData
import com.example.iconnet.model.UpdateUserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserActivity : AppCompatActivity() {

    private var idRole: Int = -1
    private var selectedRoleValue: Int = -1
    private lateinit var spinnerRole: Spinner
    private lateinit var roleList: List<RoleData>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        val idUser = intent.getIntExtra("id_user", 0)
        val nama = intent.getStringExtra("nama_pelanggan")
        val alamat = intent.getStringExtra("alamat")
        val nomorHp = intent.getStringExtra("nomor_hp")
        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("username")
        idRole = intent.getIntExtra("id_role", 0)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etNomorHp = findViewById<EditText>(R.id.etNomorHp)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Set nilai EditText
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNomorHp.setText(nomorHp)
        etEmail.setText(email)
        etUsername.setText(username)

        // Spinner Role
        spinnerRole = findViewById(R.id.spinnerRoleUser)
        fetchSpinnerRoles()

        // Button Update
        val btnUpdateUser: Button = findViewById(R.id.btnUpdateUser)
        btnUpdateUser.setOnClickListener {
            val idUser = intent.getIntExtra("id_user", 0)
            val newPassword = etPassword.text.toString()

            val updateRequest = UpdateUserRequest(
                id = idUser,
                nama_instansi = etNama.text.toString(),
                email = etEmail.text.toString(),
                alamat = etAlamat.text.toString(),
                no_hp = etNomorHp.text.toString(),
                username = etUsername.text.toString(),
                password = if (newPassword.isNotEmpty()) newPassword else null,  // Abaikan jika kosong
                role_id = idRole
            )

            RetrofitClient.instance.updateUser(updateRequest).enqueue(object : Callback<ApiResponse<UpdateUserRequest>> {
                override fun onResponse(call: Call<ApiResponse<UpdateUserRequest>>, response: Response<ApiResponse<UpdateUserRequest>>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.status) {
                            Toast.makeText(this@EditUserActivity, "User berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@EditUserActivity, apiResponse?.message ?: "Gagal memperbarui user", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EditUserActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<UpdateUserRequest>>, t: Throwable) {
                    Toast.makeText(this@EditUserActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
                        this@EditUserActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        roleNames
                    )
                    spinnerRole.adapter = adapter

                    // Pilih role berdasarkan idRole
                    if (idRole != -1) {
                        val selectedIndex = roleList.indexOfFirst { it.id == idRole }
                        if (selectedIndex != -1) {
                            spinnerRole.setSelection(selectedIndex + 1) // +1 karena index 0 adalah "Pilih Role"
                        }
                    }

                    // Event saat item dipilih
                    spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (position != 0) {
                                val selectedRole = roleNames[position]
                                selectedRoleValue = roleMap[selectedRole] ?: -1

                                Log.d("EditUserActivity", "Role dipilih: $selectedRole")
                                Log.d("EditUserActivity", "ID Role: $selectedRoleValue")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                } else {
                    Toast.makeText(this@EditUserActivity, "Gagal mengambil data role", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<RoleData>>>, t: Throwable) {
                Toast.makeText(this@EditUserActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}