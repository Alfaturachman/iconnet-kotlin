package com.example.iconnet.ui.teknisi

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.ApiService
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentTeknisiBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.user.PengaduanAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeknisiFragment : Fragment() {

    private lateinit var binding: FragmentTeknisiBinding
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
        binding = FragmentTeknisiBinding.inflate(inflater, container, false)
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
    }

    private fun loadPengaduanData(userId: Int) {
        val requestBody = mapOf("user_id" to userId)

        Log.d("UserFragment", "Mengirim request ke API dengan user_id: $userId")

        apiService.getTugasTeknisi(requestBody).enqueue(object :
            Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                if (response.isSuccessful) {
                    Log.d("UserFragment", "Response sukses: ${response.body()}")

                    if (response.body()?.status == true) {
                        val pengaduanList = response.body()?.data ?: emptyList()
                        Log.d("UserFragment", "Jumlah data pengaduan: ${pengaduanList.size}")

                        binding.recyclerViewPengaduanTeknisi.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = PengaduanAdapter(requireContext(), pengaduanList, startForResult)
                        }
                    } else {
                        Log.w("UserFragment", "Response tidak sukses: ${response.body()?.message}")
                        Toast.makeText(requireContext(), "Gagal mengambil data: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("UserFragment", "Response gagal dengan kode: ${response.code()} dan body: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pengaduan>>>, t: Throwable) {
                Log.e("UserFragment", "Error saat mengambil data: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun refreshData() {
        loadPengaduanData(userId)
    }
}
