package com.example.iconnet.ui.admin.tambah_tugas

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.TeknisiData
import com.example.iconnet.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahTugasActivity : AppCompatActivity() {

    private var idTeknisi: Int = -1
    private var daerahPengaduan: String = "null"
    private var selectedDaerah: String = "Tidak ada Daerah"
    private var teknisiList: List<TeknisiData> = listOf()
    private var selectedTeknisi: TeknisiData? = null
    private lateinit var spinnerNamaTeknisi: Spinner

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tugas)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Inisialisasi EditText
        val etTanggalPengaduan: TextView = findViewById(R.id.etTanggalPengaduan)
        val etNamaPelanggan: TextView = findViewById(R.id.etNamaPelanggan)
        val etJudulPengaduan: TextView = findViewById(R.id.etJudulPengaduan)
        val etIsiPengaduan: TextView = findViewById(R.id.etIsiPengaduan)
        val btnTambahTugas: Button = findViewById(R.id.btnTambahTugas)

        // Ambil data dari Intent
        val idPengaduan = intent.getStringExtra("id_pengaduan") ?: "N/A"
        val tanggalPengaduan = intent.getStringExtra("tanggal_pengaduan") ?: "N/A"
        val formattedDate = DateUtils.formatTanggal(tanggalPengaduan)
        val namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: "N/A"
        val judulPengaduan = intent.getStringExtra("judul_pengaduan") ?: "N/A"
        val isiPengaduan = intent.getStringExtra("isi_pengaduan") ?: "N/A"
        idTeknisi = intent.getIntExtra("id_teknisi", -1)
        daerahPengaduan = intent.getStringExtra("daerah_pengaduan") ?: "N/A"

        // Fungsi untuk mengatur status tombol
        fun updateButtonState() {
            btnTambahTugas.isEnabled = selectedTeknisi != null && selectedDaerah != null
        }

        updateButtonState()

        val spinnerDaerahSemarang: Spinner = findViewById(R.id.spinnerDaerahSemarang)

// Ambil data daerah dari resources
        val daerahSemarang = resources.getStringArray(R.array.daerah_semarang)

// Buat adapter untuk Spinner
        val adapterDaerah = ArrayAdapter(
            this, // Context
            android.R.layout.simple_spinner_item,
            daerahSemarang // Data
        )
        adapterDaerah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter ke Spinner
        spinnerDaerahSemarang.adapter = adapterDaerah

        // Ambil data dari intent
        val daerahPengaduan = intent.getStringExtra("daerah_pengaduan") ?: "N/A"

        // Cari posisi item dalam array
        val selectedIndex = daerahSemarang.indexOf(daerahPengaduan)

        // Jika ditemukan, set spinner ke posisi tersebut, jika tidak biarkan default
        if (selectedIndex != -1) {
            spinnerDaerahSemarang.setSelection(selectedIndex)
        }

        // Handle item yang dipilih
        var selectedDaerah: String? = null
        spinnerDaerahSemarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Abaikan placeholder jika ada
                    selectedDaerah = daerahSemarang[position]
                    updateButtonState()
                    Log.d("Selected Spinner", "Teknisi yang dipilih: $selectedDaerah")
                } else {
                    selectedDaerah = null
                    updateButtonState()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        spinnerNamaTeknisi = findViewById(R.id.spinnerNamaTeknisi)
        loadTeknisiData()

        // Set nilai ke EditText
        etTanggalPengaduan.setText(formattedDate)
        etNamaPelanggan.setText(namaPelanggan)
        etJudulPengaduan.setText(judulPengaduan)
        etIsiPengaduan.setText(isiPengaduan)

        // Button Simpan
        btnTambahTugas.setOnClickListener {
            // Validasi tambahan sebelum melanjutkan
            if (selectedTeknisi == null || selectedDaerah == null) {
                Toast.makeText(this@TambahTugasActivity, "Harap pilih teknisi dan daerah terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Log hasil putExtra
            Log.d("TambahTugasActivity", "ID Pengaduan: $idPengaduan")
            Log.d("TambahTugasActivity", "Daerah Pengaduan: $selectedDaerah")

            // Pastikan selectedTeknisi tidak null sebelum mengakses properti
            if (selectedTeknisi != null) {
                Log.d("TambahTugasActivity", "ID Teknisi: ${selectedTeknisi?.idTeknisi}")
                Log.d("TambahTugasActivity", "Nama Teknisi: ${selectedTeknisi?.namaTeknisi}")
            } else {
                Log.d("TambahTugasActivity", "Teknisi belum dipilih")
            }

            Toast.makeText(this@TambahTugasActivity, "Tambah tugas berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTeknisiData() {
        RetrofitClient.instance.getTeknisi().enqueue(object : Callback<ApiResponse<List<TeknisiData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<TeknisiData>>>,
                response: Response<ApiResponse<List<TeknisiData>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    teknisiList = response.body()!!.data

                    val teknisiNames = teknisiList.map { it.namaTeknisi }.toMutableList()
                    teknisiNames.add(0, "Pilih Teknisi") // Tambahkan placeholder di awal

                    val adapterTeknisi = ArrayAdapter(
                        this@TambahTugasActivity,
                        android.R.layout.simple_spinner_item,
                        teknisiNames
                    )
                    adapterTeknisi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerNamaTeknisi.adapter = adapterTeknisi

                    // Listener untuk menangani item yang dipilih
                    spinnerNamaTeknisi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            Log.d("Spinner", "Position selected: $position") // Debugging posisi yang dipilih

                            if (position > 0) { // Pastikan bukan "Pilih Teknisi"
                                selectedTeknisi = teknisiList[position - 1]
                                Log.d("Spinner", "Teknisi yang dipilih: ${selectedTeknisi?.namaTeknisi}")
                                Log.d("Spinner", "ID Teknisi yang dipilih: ${selectedTeknisi?.idTeknisi}")
                            } else {
                                selectedTeknisi = null
                                Log.d("Spinner", "Teknisi belum dipilih")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@TambahTugasActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<TeknisiData>>>, t: Throwable) {
                Log.e("API Error", t.message.toString())
                Toast.makeText(this@TambahTugasActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}