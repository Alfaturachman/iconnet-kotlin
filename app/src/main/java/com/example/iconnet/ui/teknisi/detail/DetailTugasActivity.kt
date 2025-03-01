package com.example.iconnet.ui.teknisi.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.api.RetrofitClient.ip
import com.example.iconnet.databinding.ActivityDetailTugasBinding
import com.example.iconnet.model.DetailTeknisi
import com.example.iconnet.model.Pengaduan
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
    private var userNama: String = "0"
    private var idPengaduan: Int = -1
    private lateinit var spinnerStatus: Spinner
    private lateinit var etKeterangan: EditText
    private lateinit var tvFileName: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var btnSimpan: Button
    private lateinit var layoutUpload: LinearLayout
    private lateinit var layoutUploadDone: LinearLayout
    private var selectedFileUri: Uri? = null
    private lateinit var binding: ActivityDetailTugasBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_tugas)
        binding = ActivityDetailTugasBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        userNama = sharedPreferences.getString("nama", "") ?: ""
        idPengaduan = intent.getIntExtra("id_pengaduan", -1)

        // Inisialisasi View
        spinnerStatus = findViewById(R.id.spinnerStatus)
        tvFileName = findViewById(R.id.tvFileName)
        ivPreview = findViewById(R.id.ivPreview)
        btnSimpan = findViewById(R.id.btnSimpan)
        etKeterangan = findViewById(R.id.etKeterangan)
        layoutUpload = findViewById(R.id.layoutUpload)
        layoutUploadDone = findViewById(R.id.layoutUploadDone)

        setupSpinner()

        binding.btnUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" // Hanya gambar yang bisa dipilih
            startActivityForResult(intent, FILE_PICK_REQUEST)
        }

        fetchDetailTeknisi(idPengaduan)
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
                                setResult(RESULT_OK)
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

    private fun fetchDetailTeknisi(idPengaduan: Int) {
        val requestBody = mapOf("id_pengaduan" to idPengaduan)

        Log.d("DetailTugasActivity", "Request Body: $requestBody") // Log request body

        RetrofitClient.instance.getDetailTeknisi(requestBody).enqueue(object : Callback<ApiResponse<DetailTeknisi>> {
            override fun onResponse(call: Call<ApiResponse<DetailTeknisi>>, response: Response<ApiResponse<DetailTeknisi>>) {
                Log.d("DetailTugasActivity", "Response Code: ${response.code()}") // Log response code
                Log.d("DetailTugasActivity", "Response Body: ${response.body()}") // Log response body

                if (response.isSuccessful && response.body()?.status == true) {
                    val pengaduan = response.body()?.data
                    pengaduan?.let {
                        Log.d("DetailTugasActivity", "Detail Data: $it")

                        binding.tvTanggalPengaduan.text = it.tglPengaduan
                        binding.tvIdPelanggan.text = it.idPelanggan
                        binding.tvNamaPelanggan.text = it.namaUser
                        binding.tvJudulPengaduan.text = it.judulPengaduan
                        binding.tvIsiPengaduan.text = it.isiPengaduan
                        binding.tvAlamat.text = it.alamatPengaduan
                        binding.tvStatus.text = when (it.statusPengaduan) {
                            0 -> "Antrian"
                            1 -> "Proses"
                            2 -> "Selesai"
                            3 -> "Batal"
                            else -> "Tidak Diketahui"
                        }

                        Log.d("DetailTugasActivity", "teknisiUploadId: ${it.teknisiUploadId}")

                        if (it.teknisiUploadId != null) {
                            binding.tvKeterangan.text = it.keterangan
                            binding.tvNamaFile.text = it.filePengaduan
                            if (!it.filePengaduan.isNullOrEmpty()) {
                                val imageUrl = "http://$ip:80/iconnet_api/uploads/${it.filePengaduan}"

                                binding.cardviewImage.setOnClickListener {
                                    showImagePopup(imageUrl)
                                }
                            } else {
                                Log.e("DetailTugasActivity", "File pengaduan kosong atau tidak tersedia")
                            }
                            layoutUpload.visibility = View.GONE
                            layoutUploadDone.visibility = View.VISIBLE
                        } else {
                            layoutUpload.visibility = View.VISIBLE
                            layoutUploadDone.visibility = View.GONE
                        }
                    }
                } else {
                    Log.d("DetailTugasActivity", "Belum ada data pengaduan atau teknisi upload")
                }
            }

            override fun onFailure(call: Call<ApiResponse<DetailTeknisi>>, t: Throwable) {
                Log.e("DetailTugasActivity", "Gagal menghubungi server", t)
                Toast.makeText(this@DetailTugasActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showImagePopup(imageUrl: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_preview)

        val imageView = dialog.findViewById<ImageView>(R.id.ivPreview)
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground) // Placeholder jika gambar belum termuat
            .error(R.drawable.ic_launcher_background) // Jika gagal memuat gambar
            .into(imageView)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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
