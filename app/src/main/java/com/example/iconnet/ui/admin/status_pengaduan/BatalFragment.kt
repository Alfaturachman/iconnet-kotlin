package com.example.iconnet.ui.admin.status_pengaduan

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentBatalBinding
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.PengaduanAdapter
import com.example.iconnet.ui.admin.tambah_tugas.TambahTugasActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BatalFragment : Fragment() {

    private var _binding: FragmentBatalBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PengaduanAdapter
    private val pengaduanList = mutableListOf<Pengaduan>()

    // Deklarasikan ActivityResultLauncher
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData() // Refresh data setelah activity selesai
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPengaduanData() // Load awal data
    }

    private fun setupRecyclerView() {
        adapter = PengaduanAdapter(pengaduanList, startForResult) // Kirim startForResult
        binding.recyclerViewPengaduanAdminBatal.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPengaduanAdminBatal.adapter = adapter
    }

    private fun loadPengaduanData() {
        RetrofitClient.instance.getPengaduan().enqueue(object : Callback<ApiResponse<List<Pengaduan>>> {
            override fun onResponse(call: Call<ApiResponse<List<Pengaduan>>>, response: Response<ApiResponse<List<Pengaduan>>>) {
                if (response.isSuccessful) {
                    val allPengaduan = response.body()?.data ?: emptyList()
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

    private fun refreshData() {
        loadPengaduanData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
