package com.example.iconnet.ui.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentUserBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.master_user.tambah.TambahUserActivity
import com.example.iconnet.ui.user.tambah.TambahPengaduanActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var apiService: ApiService
    private var userId: Int = -1

    // ActivityResultLauncher
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = RetrofitClient.instance

        // Ambil user_id dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            loadPengaduanData(userId)
        } else {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // Button Kembali
        binding.btnTambahPengaduan.setOnClickListener {
            val intent = Intent(requireContext(), TambahPengaduanActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun loadPengaduanData(userId: Int) {
        val requestBody = mapOf("user_id" to userId)
        Log.d("UserFragment", "Mengirim request dengan user_id: $userId")

        apiService.getPengaduan(requestBody).enqueue(object : Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                Log.d("UserFragment", "Response diterima, kode: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("UserFragment", "Response body: $responseBody")

                    if (responseBody?.status == true) {
                        val pengaduanList = responseBody.data ?: emptyList()
                        Log.d("UserFragment", "Jumlah data yang diterima: ${pengaduanList.size}")

                        if (pengaduanList.isNotEmpty()) {
                            // Jika ada data, tampilkan RecyclerView & sembunyikan cardViewAlert
                            binding.recyclerViewPengaduanUser.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = PengaduanAdapter(requireContext(), pengaduanList, startForResult)
                                visibility = View.VISIBLE
                            }
                            binding.cardViewAlert.visibility = View.GONE
                        } else {
                            // Jika tidak ada data, tampilkan cardViewAlert & sembunyikan RecyclerView
                            binding.recyclerViewPengaduanUser.visibility = View.GONE
                            binding.cardViewAlert.visibility = View.VISIBLE
                        }
                    } else {
                        Log.e("UserFragment", "Gagal mengambil data, status response false")
                        binding.recyclerViewPengaduanUser.visibility = View.GONE
                        binding.cardViewAlert.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("UserFragment", "Response tidak berhasil, kode: ${response.code()}")
                    binding.recyclerViewPengaduanUser.visibility = View.GONE
                    binding.cardViewAlert.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pengaduan>>>, t: Throwable) {
                Log.e("UserFragment", "Error saat memuat data: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                binding.recyclerViewPengaduanUser.visibility = View.GONE
                binding.cardViewAlert.visibility = View.VISIBLE
            }
        })
    }

    private fun refreshData() {
        loadPengaduanData(userId)
    }
}
