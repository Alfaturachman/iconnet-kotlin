package com.example.iconnet.ui.user.tambah

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.PengaduanRequest
import com.example.iconnet.ui.success.SuccessActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahPengaduanActivity : AppCompatActivity() {
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pengaduan)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Ambil user_id dari SharedPreferences
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("id_user", -1)

        val etIsiPengaduan = findViewById<EditText>(R.id.etIsiPengaduan)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etNomorHp = findViewById<EditText>(R.id.etNomorHp)
        val btnSimpanPengaduan = findViewById<Button>(R.id.btnSimpanPengaduan)

        val spinnerJudulKeluhan = findViewById<Spinner>(R.id.spinnerJudulKeluhan)
        val judulKeluhanArray = resources.getStringArray(R.array.judul_keluhan_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, judulKeluhanArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJudulKeluhan.adapter = adapter

        btnSimpanPengaduan.setOnClickListener {
            val judulKeluhan = spinnerJudulKeluhan.selectedItem.toString()
            val isiPengaduan = etIsiPengaduan.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val nomorHp = etNomorHp.text.toString().trim()

            // Validasi input isi pengaduan tidak boleh kosong
            if (isiPengaduan.isEmpty()) {
                etIsiPengaduan.error = "Isi pengaduan tidak boleh kosong"
                etIsiPengaduan.requestFocus()
                return@setOnClickListener
            }

            // Validasi input alamat tidak boleh kosong
            if (alamat.isEmpty()) {
                etAlamat.error = "Alamat tidak boleh kosong"
                etAlamat.requestFocus()
                return@setOnClickListener
            }

            // Validasi nomor HP tidak boleh kosong
            if (nomorHp.isEmpty()) {
                etNomorHp.error = "Nomor HP tidak boleh kosong"
                etNomorHp.requestFocus()
                return@setOnClickListener
            }

            // Log data untuk debugging
            Log.d("TambahPengaduan", "ID User: $userId")
            Log.d("TambahPengaduan", "Judul Keluhan: $judulKeluhan")
            Log.d("TambahPengaduan", "Isi Pengaduan: $isiPengaduan")
            Log.d("TambahPengaduan", "Alamat: $alamat")
            Log.d("TambahPengaduan", "Nomor HP: $nomorHp")

            simpanPengaduan(judulKeluhan, isiPengaduan, alamat, nomorHp)
        }
    }

    private fun simpanPengaduan(judulKeluhan: String, isiPengaduan: String, alamat: String, nomorHp: String) {
        val request = PengaduanRequest(
            instansi_id = userId,
            judul_pengaduan = judulKeluhan,
            isi_pengaduan = isiPengaduan,
            no_telp_pengaduan = nomorHp,
            alamat_pengaduan = alamat
        )

        RetrofitClient.instance.tambahPengaduan(request).enqueue(object : Callback<ApiResponse<PengaduanRequest>> {
            override fun onResponse(call: Call<ApiResponse<PengaduanRequest>>, response: Response<ApiResponse<PengaduanRequest>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.status) {
                        Toast.makeText(this@TambahPengaduanActivity, "Pengaduan berhasil disimpan!", Toast.LENGTH_SHORT).show()

                        // Kirim data ke aktivitas lain menggunakan Intent
                        val intent = Intent(this@TambahPengaduanActivity, SuccessActivity::class.java).apply {
                            putExtra("judul_pengaduan", request.judul_pengaduan)
                            putExtra("isi_pengaduan", request.isi_pengaduan)
                            putExtra("alamat_pengaduan", request.alamat_pengaduan)
                            putExtra("no_telp_pengaduan", request.no_telp_pengaduan)
                        }

                        setResult(RESULT_OK, intent)
                        startActivity(intent)
                        finish() // Tutup aktivitas ini setelah berhasil
                    } else {
                        Toast.makeText(this@TambahPengaduanActivity, "Gagal menyimpan pengaduan!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TambahPengaduanActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<PengaduanRequest>>, t: Throwable) {
                Toast.makeText(this@TambahPengaduanActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
                Log.e("TambahPengaduan", "Error: ${t.message}")
            }
        })
    }
}