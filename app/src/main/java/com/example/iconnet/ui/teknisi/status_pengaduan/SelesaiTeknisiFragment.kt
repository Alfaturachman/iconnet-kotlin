package com.example.iconnet.ui.teknisi.status_pengaduan

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
import com.example.iconnet.databinding.FragmentSelesaiTeknisiBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.teknisi.TugasAdapter
import com.example.iconnet.ui.user.PengaduanAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelesaiTeknisiFragment : Fragment() {

    private lateinit var binding: FragmentSelesaiTeknisiBinding
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
        binding = FragmentSelesaiTeknisiBinding.inflate(inflater, container, false)
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

        RetrofitClient.instance.getTugasTeknisi(requestBody).enqueue(object :
            Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                Log.d("UserFragment", "Response diterima, kode: ${response.code()}")

                if (response.isSuccessful) {
                    val allPengaduan = response.body()?.data ?: emptyList()
                    val antrianPengaduan = allPengaduan.filter { it.statusPengaduan.toInt() == 2 }

                    Log.d("UserFragment", "Antrian Pengaduan: $antrianPengaduan")

                    if (antrianPengaduan.isNotEmpty()) {
                        // Jika ada data, tampilkan RecyclerView & sembunyikan cardViewAlert
                        binding.recyclerViewPengaduanTeknisi.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = TugasAdapter(requireContext(), antrianPengaduan, startForResult)
                            visibility = View.VISIBLE
                        }
                        binding.cardViewAlert.visibility = View.GONE
                    } else {
                        // Jika tidak ada data, tampilkan cardViewAlert & sembunyikan RecyclerView
                        binding.recyclerViewPengaduanTeknisi.visibility = View.GONE
                        binding.cardViewAlert.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("UserFragment", "Response tidak berhasil, kode: ${response.code()}")
                    binding.recyclerViewPengaduanTeknisi.visibility = View.GONE
                    binding.cardViewAlert.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pengaduan>>>, t: Throwable) {
                Log.e("UserFragment", "Error saat memuat data: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                binding.recyclerViewPengaduanTeknisi.visibility = View.GONE
                binding.cardViewAlert.visibility = View.VISIBLE
            }
        })
    }

    private fun refreshData() {
        loadPengaduanData(userId)
    }
}
