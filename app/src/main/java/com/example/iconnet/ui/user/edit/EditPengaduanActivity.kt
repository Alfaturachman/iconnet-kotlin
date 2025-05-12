package com.example.iconnet.ui.user.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.Pengaduan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPengaduanActivity : AppCompatActivity() {

    private lateinit var spinnerJudulKeluhan: Spinner
    private lateinit var etIsiPengaduan: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var btnSimpanPengaduan: Button
    private var idPengaduan: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_pengaduan)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize views
        spinnerJudulKeluhan = findViewById(R.id.spinnerJudulKeluhan)
        etIsiPengaduan = findViewById(R.id.etIsiPengaduan)
        etAlamat = findViewById(R.id.etAlamat)
        etNomorHp = findViewById(R.id.etNomorHp)
        btnSimpanPengaduan = findViewById(R.id.btnSimpanPengaduan)

        // Setup spinner
        val judulKeluhanArray = resources.getStringArray(R.array.judul_keluhan_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, judulKeluhanArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJudulKeluhan.adapter = adapter

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Get pengaduan ID from intent
        idPengaduan = intent.getIntExtra("id_pengaduan", 0)
        if (idPengaduan > 0) {
            fetchDetailPengaduan(idPengaduan)
        } else {
            Toast.makeText(this, "ID Pengaduan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set click listener for save button
        btnSimpanPengaduan.setOnClickListener {
            updatePengaduan()
        }
    }

    private fun fetchDetailPengaduan(idPengaduan: Int) {
        val requestBody = mapOf("id_pengaduan" to idPengaduan)

        RetrofitClient.instance.getPengaduanByIdPengaduan(requestBody).enqueue(object : Callback<ApiResponse<Pengaduan>> {
            override fun onResponse(call: Call<ApiResponse<Pengaduan>>, response: Response<ApiResponse<Pengaduan>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val pengaduan = response.body()?.data
                    pengaduan?.let {
                        // Set spinner selection based on judul pengaduan
                        val judulKeluhanArray = resources.getStringArray(R.array.judul_keluhan_array)
                        val position = judulKeluhanArray.indexOf(it.judulPengaduan)
                        if (position >= 0) {
                            spinnerJudulKeluhan.setSelection(position)
                        }

                        etIsiPengaduan.setText(it.isiPengaduan)
                        etAlamat.setText(it.alamatPengaduan ?: "")
                        etNomorHp.setText(it.noTelpPengaduan ?: "")
                    }
                } else {
                    Toast.makeText(this@EditPengaduanActivity, "Pengaduan tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Pengaduan>>, t: Throwable) {
                Toast.makeText(this@EditPengaduanActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun updatePengaduan() {
        val judul = spinnerJudulKeluhan.selectedItem.toString()
        val isi = etIsiPengaduan.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (judul.isEmpty() || isi.isEmpty() || alamat.isEmpty() || nomorHp.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = mapOf(
            "id_pengaduan" to idPengaduan,
            "judul_pengaduan" to judul,
            "isi_pengaduan" to isi,
            "alamat_pengaduan" to alamat,
            "nomor_hp" to nomorHp
        )

        RetrofitClient.instance.updateUserPengaduan(requestBody as Map<String, String>).enqueue(object : Callback<ApiResponse<Pengaduan>> {
            override fun onResponse(call: Call<ApiResponse<Pengaduan>>, response: Response<ApiResponse<Pengaduan>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@EditPengaduanActivity, "Pengaduan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@EditPengaduanActivity, "Gagal memperbarui pengaduan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Pengaduan>>, t: Throwable) {
                Toast.makeText(this@EditPengaduanActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}