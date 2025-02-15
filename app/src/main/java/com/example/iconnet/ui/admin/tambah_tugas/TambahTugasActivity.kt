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
import com.example.iconnet.utils.DateUtils

class TambahTugasActivity : AppCompatActivity() {

    private var selectedDaerah: String = "Tidak ada Daerah"
    private var selectedTeknisi: String = "Tidak ada Teknisi"

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
        // Atur Spinner untuk tidak memilih item apa pun secara default
        spinnerDaerahSemarang.setSelection(0, false)
        // Handle item yang dipilih
        var selectedDaerah: String? = null
        spinnerDaerahSemarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Abaikan placeholder
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

        val spinnerNamaTeknisi: Spinner = findViewById(R.id.spinnerNamaTeknisi)
        // Ambil data teknisi dari resources
        val namaTeknisi = resources.getStringArray(R.array.nama_teknisi)
        // Buat adapter untuk Spinner
        val adapterTeknisi = ArrayAdapter(
            this, // Context
            android.R.layout.simple_spinner_item, // Layout default untuk item
            namaTeknisi // Data
        )
        // Atur layout dropdown
        adapterTeknisi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set adapter ke Spinner
        spinnerNamaTeknisi.adapter = adapterTeknisi
        // Atur Spinner untuk tidak memilih item apa pun secara default
        spinnerNamaTeknisi.setSelection(0, false)
        // Handle item yang dipilih
        var selectedTeknisi: String? = null
        spinnerNamaTeknisi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Abaikan placeholder
                    selectedTeknisi = namaTeknisi[position]
                    updateButtonState()
                    Log.d("Selected Spinner", "Teknisi yang dipilih: $selectedTeknisi")
                } else {
                    selectedTeknisi = null // Jika placeholder dipilih, set ke null
                    updateButtonState()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

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
            Log.d("TambahTugasActivity", "ID Teknisi: $selectedTeknisi")
            Log.d("TambahTugasActivity", "Nama Teknisi: $selectedTeknisi")

            Toast.makeText(this@TambahTugasActivity, "Tambah tugas berhasil", Toast.LENGTH_SHORT).show()
        }
    }
}