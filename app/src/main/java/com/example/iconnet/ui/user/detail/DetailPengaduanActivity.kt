package com.example.iconnet.ui.user.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.user.edit.EditPengaduanActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPengaduanActivity : AppCompatActivity() {
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var etTanggalPengaduan: TextView
    private lateinit var etIdPelanggan: TextView
    private lateinit var etNamaPelanggan: TextView
    private lateinit var etJudulPengaduan: TextView
    private lateinit var etIsiPengaduan: TextView
    private lateinit var etAlamat: TextView
    private lateinit var etStatus: TextView
    private var userNama: String = "0"

    private var idPengaduan: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pengaduan)

        // Set status bar color
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userNama = sharedPreferences.getString("nama", null).toString()

        // Inisialisasi UI
        etTanggalPengaduan = findViewById(R.id.etTanggalPengaduan)
        etIdPelanggan = findViewById(R.id.etIdPelanggan)
        etNamaPelanggan = findViewById(R.id.etNamaPelanggan)
        etJudulPengaduan = findViewById(R.id.etJudulPengaduan)
        etIsiPengaduan = findViewById(R.id.etIsiPengaduan)
        etAlamat = findViewById(R.id.etAlamat)
        etStatus = findViewById(R.id.etStatus)

        // Button kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        // Register result launcher
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchDetailPengaduan(idPengaduan)
            }
        }

        val btnEditPengaduan: Button = findViewById(R.id.btnEditPengaduan)
        btnEditPengaduan.setOnClickListener {
            val intent = Intent(this, EditPengaduanActivity::class.java)
            intent.putExtra("id_pengaduan", idPengaduan)
            editLauncher.launch(intent)
        }

        // Ambil ID Pengaduan dari Intent
        idPengaduan = intent.getIntExtra("id_pengaduan", -1)

        if (idPengaduan != -1) {
            fetchDetailPengaduan(idPengaduan)
        } else {
            Toast.makeText(this, "ID Pengaduan tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDetailPengaduan(idPengaduan: Int) {
        val requestBody = mapOf("id_pengaduan" to idPengaduan)

        RetrofitClient.instance.getPengaduanByIdPengaduan(requestBody).enqueue(object : Callback<ApiResponse<Pengaduan>> {
            override fun onResponse(call: Call<ApiResponse<Pengaduan>>, response: Response<ApiResponse<Pengaduan>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val pengaduan = response.body()?.data
                    pengaduan?.let {
                        etTanggalPengaduan.text = it.tglPengaduan
                        etIdPelanggan.text = it.idPelanggan
                        etNamaPelanggan.text = userNama ?: "Tidak Diketahui"
                        etJudulPengaduan.text = it.judulPengaduan
                        etIsiPengaduan.text = it.isiPengaduan
                        etAlamat.text = it.alamatPengaduan ?: "Tidak Tersedia"
                        etStatus.text = when (it.statusPengaduan) {
                            0.toString() -> "Antrian"
                            1.toString() -> "Proses"
                            2.toString() -> "Selesai"
                            3.toString() -> "Batal"
                            else -> "Tidak Diketahui"
                        }.toString()
                    }
                } else {
                    Toast.makeText(this@DetailPengaduanActivity, "Pengaduan tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Pengaduan>>, t: Throwable) {
                Toast.makeText(this@DetailPengaduanActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
