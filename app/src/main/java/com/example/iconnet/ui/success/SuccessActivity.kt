package com.example.iconnet.ui.success

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R
import com.example.iconnet.ui.user.UserFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success)

        // Set status bar color and light icons
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userNama = sharedPreferences.getString("nama", null)

        // Inisialisasi TextView
        val tvTanggal: TextView = findViewById(R.id.tvTanggal)
        val tvNama: TextView = findViewById(R.id.tvNama)
        val tvJudulPengaduan: TextView = findViewById(R.id.tvJudulPengaduan)
        val tvIsiPengaduan: TextView = findViewById(R.id.tvIsiPengaduan)
        val btnRiwayat: Button = findViewById(R.id.btnRiwayat)

        // Ambil data dari Intent
        val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val today = format.format(Date()) // Ambil tanggal hari ini

        val tanggal = today
        val nama = userNama
        val judulPengaduan = intent.getStringExtra("judul_pengaduan") ?: "Tidak tersedia"
        val isiPengaduan = intent.getStringExtra("isi_pengaduan") ?: "Tidak tersedia"

        // Set data ke TextView
        tvTanggal.text = tanggal
        tvNama.text = nama
        tvJudulPengaduan.text = judulPengaduan
        tvIsiPengaduan.text = isiPengaduan

        btnRiwayat.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
