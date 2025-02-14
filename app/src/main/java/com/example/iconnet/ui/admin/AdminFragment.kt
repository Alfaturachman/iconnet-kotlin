package com.example.iconnet.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentAdminBinding
import com.example.iconnet.model.Pengaduan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        loadPengaduanData()

        return root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDaftarStok.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadPengaduanData() {
        Log.d("PengaduanAPI", "Memulai permintaan data pengaduan...")

        RetrofitClient.instance.getPengaduan().enqueue(object : Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                Log.d("PengaduanAPI", "Response diterima: ${response.raw()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("PengaduanAPI", "Body Response: $responseBody")

                    if (responseBody?.status == true) {
                        val pengaduanList = responseBody.data ?: emptyList()
                        Log.d("PengaduanAPI", "Data pengaduan berhasil diambil, jumlah: ${pengaduanList.size}")

                        binding.recyclerViewDaftarStok.adapter = PengaduanAdapter(pengaduanList)
                    } else {
                        Log.e("PengaduanAPI", "Gagal mendapatkan data, pesan: ${responseBody?.message}")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("PengaduanAPI", "Response tidak sukses, kode: ${response.code()}, pesan: ${response.message()}")
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pengaduan>>>, t: Throwable) {
                Log.e("PengaduanAPI", "Request gagal: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}