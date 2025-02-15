package com.example.iconnet.ui.admin.master_user.tambah

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
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
    private var selectedRoleValue: String = "" // ID role yang dipilih
    private lateinit var spinnerRole: Spinner
    private lateinit var roleList: List<RoleData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_user)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Setup Spinner
        spinnerRole = findViewById(R.id.spinnerRoleUser)

        // Ambil data role dari API
        fetchRoles()
    }

    private fun fetchRoles() {
        RetrofitClient.instance.getRoles().enqueue(object : Callback<ApiResponse<List<RoleData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<RoleData>>>,
                response: Response<ApiResponse<List<RoleData>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    roleList = response.body()?.data ?: listOf()

                    // Ubah data ke format untuk spinner
                    val roleNames = mutableListOf("Pilih Role") // Tambah opsi default
                    val roleMap = mutableMapOf<String, String>()

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
                                selectedRoleValue = roleMap[selectedRole] ?: ""

                                Toast.makeText(
                                    this@TambahUserActivity,
                                    "Role dipilih: $selectedRole (ID: $selectedRoleValue)",
                                    Toast.LENGTH_SHORT
                                ).show()
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
