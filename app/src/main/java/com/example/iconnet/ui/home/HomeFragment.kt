package com.example.iconnet.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentHomeBinding
import com.example.iconnet.model.DashboardData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Check SharedPreferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)
        val userRole = sharedPreferences.getString("role", "0")

        when (userRole) {
            "Admin" -> {
                binding.rekapDataUser.visibility = View.VISIBLE
            }
            "Teknisi" -> {
                binding.rekapDataUser.visibility = View.GONE
            }
            "User" -> {
                binding.rekapDataUser.visibility = View.GONE
            }
        }

        fetchDashboardData()
        return root
    }

    private fun fetchDashboardData() {
        RetrofitClient.instance.getDashboardData().enqueue(object : Callback<ApiResponse<DashboardData>> {
            override fun onResponse(call: Call<ApiResponse<DashboardData>>, response: Response<ApiResponse<DashboardData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { dashboard ->
                        Log.d("API_RESPONSE", "Data sukses diterima: $dashboard")

                        val dashboardData = dashboard.data
                        dashboardData?.let {
                            binding.tvTotalUserPelanggan.text = it.totalUserPelanggan.toString()
                            binding.tvTotalUserTeknisi.text = it.totalUserTeknisi.toString()
                            binding.tvTotalPengaduanAntrian.text = it.totalPengaduanAntrian.toString()
                            binding.tvTotalPengaduanProses.text = it.totalPengaduanProses.toString()
                            binding.tvTotalPengaduanSelesai.text = it.totalPengaduanSelesai.toString()
                            binding.tvTotalPengaduanBatal.text = it.totalPengaduanBatal.toString()
                        }
                    }
                } else {
                    Log.e("API_RESPONSE", "Gagal mengambil data: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<DashboardData>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}