package com.example.iconnet.ui.admin.status_pengaduan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentSelesaiBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.PengaduanAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelesaiFragment : Fragment() {

    private var _binding: FragmentSelesaiBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PengaduanAdapter
    private val pengaduanList = mutableListOf<Pengaduan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelesaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPengaduanData() // Load data pengaduan dengan status 0
    }

    private fun setupRecyclerView() {
        adapter = PengaduanAdapter(pengaduanList)
        binding.recyclerViewPengaduanAdminSelesai.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPengaduanAdminSelesai.adapter = adapter
    }

    private fun loadPengaduanData() {
        RetrofitClient.instance.getPengaduan().enqueue(object : Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                if (response.isSuccessful) {
                    val allPengaduan = response.body()?.data ?: emptyList()

                    // Filter data berdasarkan status (3 = Selesai)
                    val SelesaiPengaduan = allPengaduan.filter { it.statusPengaduan.toInt() == 2 }

                    Log.d("PengaduanAPI", "Selesai Pengaduan: $SelesaiPengaduan")

                    pengaduanList.clear()
                    pengaduanList.addAll(SelesaiPengaduan)
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pengaduan>>>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
