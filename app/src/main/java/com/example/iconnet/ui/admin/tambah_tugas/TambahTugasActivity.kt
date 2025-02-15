package com.example.iconnet.ui.admin.tambah_tugas

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.iconnet.model.UpdatePengaduanRequest
import com.example.iconnet.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahTugasActivity : AppCompatActivity() {

    private var idTeknisi: Int = -1
    private var idPengaduan: Int = -1
    private var daerahPengaduan: String = "null"
    private var selectedDaerah: String = "Tidak ada Daerah"
    private var teknisiList: List<TeknisiData> = listOf()
    private var selectedTeknisi: TeknisiData? = null
    private lateinit var btnTambahTugas: Button
    private lateinit var spinnerDaerahSemarang: Spinner
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

        // Ambil data dari Intent
        idPengaduan = intent.getIntExtra("id_pengaduan", -1)
        val tanggalPengaduan = intent.getStringExtra("tanggal_pengaduan") ?: "N/A"
        val formattedDate = DateUtils.formatTanggal(tanggalPengaduan)
        val namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: "N/A"
        val judulPengaduan = intent.getStringExtra("judul_pengaduan") ?: "N/A"
        val isiPengaduan = intent.getStringExtra("isi_pengaduan") ?: "N/A"
        idTeknisi = intent.getIntExtra("id_teknisi", -1)
        daerahPengaduan = intent.getStringExtra("daerah_pengaduan") ?: "N/A"

        // Fungsi untuk mengatur status tombol
        btnTambahTugas = findViewById(R.id.btnTambahTugas)
        updateButtonState()

        spinnerDaerahSemarang = findViewById(R.id.spinnerDaerahSemarang)
        setupSpinnerDaerah(daerahPengaduan)

        spinnerNamaTeknisi = findViewById(R.id.spinnerNamaTeknisi)
        setupSpinnerTeknisi()

        // Set nilai ke EditText
        etTanggalPengaduan.setText(formattedDate)
        etNamaPelanggan.setText(namaPelanggan)
        etJudulPengaduan.setText(judulPengaduan)
        etIsiPengaduan.setText(isiPengaduan)

        // Button Simpan
        btnTambahTugas.setOnClickListener {
            if (selectedTeknisi == null || selectedDaerah == null) {
                Toast.makeText(this, "Harap pilih teknisi dan daerah terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log hasil putExtra
            Log.d("TambahTugasActivity", "ID Pengaduan: $idPengaduan")
            Log.d("TambahTugasActivity", "Daerah Pengaduan: $selectedDaerah")

            val request = UpdatePengaduanRequest(
                id_pengaduan = idPengaduan,
                id_user = selectedTeknisi?.idTeknisi,
                daerah_pengaduan = selectedDaerah
            )

            RetrofitClient.instance.updatePengaduan(request)
                .enqueue(object : Callback<ApiResponse<UpdatePengaduanRequest>> { // Tidak pakai List
                    override fun onResponse(call: Call<ApiResponse<UpdatePengaduanRequest>>, response: Response<ApiResponse<UpdatePengaduanRequest>>) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            Toast.makeText(this@TambahTugasActivity, apiResponse?.message, Toast.LENGTH_SHORT).show()
                            Log.d("TambahTugasActivity", "Response: ${apiResponse?.message}")
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@TambahTugasActivity, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<UpdatePengaduanRequest>>, t: Throwable) {
                        Toast.makeText(this@TambahTugasActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("TambahTugasActivity", "Error: ${t.message}")
                    }
                })
        }
    }

    private fun updateButtonState() {
        val isFormValid = selectedTeknisi != null && selectedDaerah.isNotEmpty() && selectedDaerah != "null"
        btnTambahTugas.isEnabled = isFormValid

        Log.d("ButtonState", "isEnabled: $isFormValid | selectedTeknisi: ${selectedTeknisi?.idTeknisi} | selectedDaerah: $selectedDaerah")
    }

    private fun setupSpinnerDaerah(daerahPengaduan: String) {
        val daerahSemarang = resources.getStringArray(R.array.daerah_semarang)

        val adapterDaerah = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            daerahSemarang
        )
        adapterDaerah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerDaerahSemarang.adapter = adapterDaerah

        // Cari posisi item dalam array
        val selectedIndex = daerahSemarang.indexOf(daerahPengaduan)

        // Jika ditemukan, set spinner ke posisi tersebut
        if (selectedIndex != -1) {
            spinnerDaerahSemarang.setSelection(selectedIndex)
        }

        // Handle item yang dipilih
        spinnerDaerahSemarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDaerah = if (position > 0) daerahSemarang[position] else null.toString()
                Log.d("Selected Spinner", "Daerah yang dipilih: $selectedDaerah")
                updateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }
    }

    private fun setupSpinnerTeknisi() {
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

                    // Cari posisi teknisi berdasarkan idTeknisi dari Intent
                    val selectedIndex = teknisiList.indexOfFirst { it.idTeknisi == idTeknisi }
                    if (selectedIndex != -1) {
                        spinnerNamaTeknisi.setSelection(selectedIndex + 1) // +1 karena ada placeholder di indeks 0
                    }

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
                                selectedTeknisi = teknisiList[position - 1] // -1 karena ada placeholder
                                updateButtonState()
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