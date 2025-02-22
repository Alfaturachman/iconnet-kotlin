package com.example.iconnet.ui.teknisi.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.R

class DetailTugasActivity : AppCompatActivity() {

    private var userId: Int = -1
    private var idPengaduan: Int = -1
    private lateinit var spinnerStatus: Spinner
    private lateinit var etKeterangan: EditText
    private lateinit var tvFileName: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var btnUploadFile: ImageView
    private lateinit var btnSimpan: Button
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_tugas)

        // Set status bar color
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // ID User SharedPreferences
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("id_user", -1)
        idPengaduan = intent.getIntExtra("id_pengaduan", -1)

        // Inisialisasi View
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnUploadFile = findViewById(R.id.btnUploadFile)
        tvFileName = findViewById(R.id.tvFileName)
        ivPreview = findViewById(R.id.ivPreview)
        btnSimpan = findViewById(R.id.btnSimpan)
        etKeterangan = findViewById(R.id.etKeterangan)

        setupSpinner()

        btnUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" // Hanya gambar yang bisa dipilih
            startActivityForResult(intent, FILE_PICK_REQUEST)
        }

        setupButtonSimpan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let { uri ->
                val fileName = getFileName(uri)

                // Log informasi file yang dipilih
                Log.d("FileUpload", "File terpilih: $fileName")
                Log.d("FileUpload", "URI: $uri")

                tvFileName.text = fileName

                // Cek apakah file yang dipilih adalah gambar
                if (isImageFile(uri)) {
                    ivPreview.setImageURI(uri)
                    ivPreview.visibility = View.VISIBLE
                    Log.d("FileUpload", "File ini adalah gambar ✅")
                } else {
                    ivPreview.visibility = View.GONE
                    Log.d("FileUpload", "File ini BUKAN gambar ❌")
                }
            }
        }
    }

    private fun setupButtonSimpan() {
        btnSimpan.setOnClickListener {
            val statusPosition = spinnerStatus.selectedItemPosition // Ambil angka status (0,1,2,3)
            val statusName = spinnerStatus.selectedItem.toString() // Ambil nama status
            val keterangan = etKeterangan.text.toString().trim() // Ambil teks keterangan

            // Cek apakah ada file yang dipilih
            val fileName = if (selectedFileUri != null) getFileName(selectedFileUri!!) else "Tidak ada file"
            val fileUri = selectedFileUri?.toString() ?: "Tidak ada file"

            // Log informasi yang akan disimpan
            Log.d("DetailTugasActivity", "ID Pengaduan: $idPengaduan")
            Log.d("DetailTugasActivity", "ID Teknisi: $userId")
            Log.d("DetailTugasActivity", "Status: $statusPosition - $statusName")
            Log.d("DetailTugasActivity", "Keterangan: $keterangan")
            Log.d("DetailTugasActivity", "Nama File: $fileName")
            Log.d("DetailTugasActivity", "URI File: $fileUri")

            Toast.makeText(this, "Berhasil Simpan Tugas", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupSpinner() {
        val statusList = listOf("Antrian", "Proses", "Selesai", "Batal")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)
        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statusList[position]
                Log.d("SpinnerStatus", "Kode Status: $position, Nama Status: $selectedStatus")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Fungsi untuk mendapatkan nama asli file dari URI
    private fun getFileName(uri: Uri): String {
        var name = "File terpilih"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    // Fungsi untuk mengecek apakah file adalah gambar
    private fun isImageFile(uri: Uri): Boolean {
        val contentResolver = contentResolver
        val mimeType = contentResolver.getType(uri)
        Log.d("FileUpload", "MIME Type: $mimeType")
        return mimeType?.startsWith("image/") == true
    }

    companion object {
        private const val FILE_PICK_REQUEST = 100
    }
}
