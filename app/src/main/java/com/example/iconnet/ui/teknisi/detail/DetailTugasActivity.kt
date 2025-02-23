package com.example.iconnet.ui.teknisi.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.UploadTugasRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val idPengaduan = idPengaduan
            val keterangan = etKeterangan.text.toString().trim()
            val statusPosition = spinnerStatus.selectedItemPosition

            Log.d("DetailTugasActivity", "ID Pengaduan: $idPengaduan")
            Log.d("DetailTugasActivity", "Status: $statusPosition")
            Log.d("DetailTugasActivity", "Keterangan: $keterangan")

            if (selectedFileUri == null) {
                Toast.makeText(this, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gunakan ContentResolver untuk membuka file dari URI
            val contentResolver = applicationContext.contentResolver
            val mimeType = contentResolver.getType(selectedFileUri!!)
            val extension = when (mimeType) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "application/pdf" -> ".pdf"
                else -> ".tmp"
            }
            val file = File(cacheDir, "temp_file$extension") // Tambahkan ekstensi
            val inputStream = contentResolver.openInputStream(selectedFileUri!!)
            if (inputStream == null) {
                Toast.makeText(this, "File tidak dapat dibuka", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = MultipartBody.Part.createFormData(
                "file_pengaduan",
                file.name,
                file.asRequestBody()
            )

            // Buat request body untuk parameter lainnya
            val idPengaduanBody = idPengaduan.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = statusPosition.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Panggil API
            RetrofitClient.instance.uploadTugas(idPengaduanBody, keteranganBody, statusBody, requestFile)
                .enqueue(object : Callback<ApiResponse<UploadTugasRequest>> {
                    @SuppressLint("UnsafeIntentLaunch")
                    override fun onResponse(
                        call: Call<ApiResponse<UploadTugasRequest>>,
                        response: Response<ApiResponse<UploadTugasRequest>>
                    ) {
                        Log.d("DetailTugasActivity", "Response received: $response")

                        if (response.isSuccessful) {
                            val uploadResponse = response.body()
                            Log.d("DetailTugasActivity", "Response body: $uploadResponse")

                            if (uploadResponse?.status == true) {
                                Log.d("DetailTugasActivity", "Data: ${uploadResponse.data}")
                                Toast.makeText(this@DetailTugasActivity, "Berhasil menyimpan tugas", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK, intent)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@DetailTugasActivity, uploadResponse?.message ?: "Gagal menyimpan tugas", Toast.LENGTH_SHORT).show()
                                Log.e("DetailTugasActivity", "Upload gagal: ${uploadResponse?.message}")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@DetailTugasActivity, "Gagal menyimpan tugas: ${response.message()}", Toast.LENGTH_SHORT).show()
                            Log.e("DetailTugasActivity", "Error response: ${response.message()}, Body: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<UploadTugasRequest>>, t: Throwable) {
                        Toast.makeText(this@DetailTugasActivity, "Gagal menyimpan tugas: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DetailTugasActivity", "Request failed: ${t.message}", t)
                    }
                })
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                if (index != -1) {
                    filePath = cursor.getString(index)
                }
            }
        }
        return filePath
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
