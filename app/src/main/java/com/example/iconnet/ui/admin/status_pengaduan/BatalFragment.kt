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
import com.example.iconnet.databinding.FragmentBatalBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.PengaduanAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BatalFragment : Fragment() {

    private var _binding: FragmentBatalBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PengaduanAdapter
    private val pengaduanList = mutableListOf<Pengaduan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPengaduanData() // Load data pengaduan dengan status 0
    }

    private fun setupRecyclerView() {
        adapter = PengaduanAdapter(pengaduanList)
        binding.recyclerViewPengaduanAdminBatal.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPengaduanAdminBatal.adapter = adapter
    }

    private fun loadPengaduanData() {
        RetrofitClient.instance.getPengaduan().enqueue(object : Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pengaduan>>>,
                response: Response<ApiResponse<List<Pengaduan>>>
            ) {
                if (response.isSuccessful) {
                    val allPengaduan = response.body()?.data ?: emptyList()

                    // Filter data berdasarkan status (4 = Batal)
                    val BatalPengaduan = allPengaduan.filter { it.statusPengaduan.toInt() == 4 }

                    Log.d("PengaduanAPI", "Batal Pengaduan: $BatalPengaduan")

                    pengaduanList.clear()
                    pengaduanList.addAll(BatalPengaduan)
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
