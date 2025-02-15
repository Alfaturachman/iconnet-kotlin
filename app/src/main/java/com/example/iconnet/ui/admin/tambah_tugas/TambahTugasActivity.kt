package com.example.iconnet.ui.admin.tambah_tugas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iconnet.R
import com.example.iconnet.model.Pengaduan

class TambahTugasActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tugas)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi EditText
        val etTanggalPengaduan: EditText = findViewById(R.id.etTanggalPengaduan)
        val etNamaPelanggan: EditText = findViewById(R.id.etNamaPelanggan)
        val etJudulPengaduan: EditText = findViewById(R.id.etJudulPengaduan)
        val etIsiPengaduan: EditText = findViewById(R.id.etIsiPengaduan)

        // Ambil data dari Intent
        val idPengaduan = intent.getStringExtra("id_pengaduan") ?: "N/A"
        val tanggalPengaduan = intent.getStringExtra("tanggal_pengaduan") ?: "N/A"
        val namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: "N/A"
        val judulPengaduan = intent.getStringExtra("judul_pengaduan") ?: "N/A"
        val isiPengaduan = intent.getStringExtra("isi_pengaduan") ?: "N/A"

        // Set nilai ke EditText
        etTanggalPengaduan.setText(tanggalPengaduan)
        etNamaPelanggan.setText(namaPelanggan)
        etJudulPengaduan.setText(judulPengaduan)
        etIsiPengaduan.setText(isiPengaduan)

        // Log hasil putExtra
        Log.d("TambahTugasActivity", "id_pengaduan: $idPengaduan")
        Log.d("TambahTugasActivity", "tanggal_pengaduan: $tanggalPengaduan")
        Log.d("TambahTugasActivity", "nama_pelanggan: $namaPelanggan")
        Log.d("TambahTugasActivity", "judul_pengaduan: $judulPengaduan")
        Log.d("TambahTugasActivity", "isi_pengaduan: $isiPengaduan")
    }
}