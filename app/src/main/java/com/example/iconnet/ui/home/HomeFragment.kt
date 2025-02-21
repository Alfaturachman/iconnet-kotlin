package com.example.iconnet.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.databinding.FragmentHomeBinding
import com.example.iconnet.model.DashboardData
import com.example.iconnet.model.TotalStatsResponse
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var userId: Int = -1
    private var userRole: String = "0"

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

        // SharedPreferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("UserSession", MODE_PRIVATE)
        userId = sharedPreferences.getInt("id_user", -1)
        userRole = sharedPreferences.getString("role", "0").toString()

        when (userRole) {
            "Admin" -> {
                fetchRekapDataPengaduanAdmin()
                fetchTotalStatsAdmin()
                binding.rekapDataUser.visibility = View.VISIBLE
                binding.rekapDataPengaduan.visibility = View.VISIBLE
                binding.laporanStatsAdmin.visibility = View.VISIBLE

            }
            "Teknisi" -> {
                fetchRekapDataPengaduanAdmin()
                binding.rekapDataUser.visibility = View.GONE
                binding.rekapDataPengaduan.visibility = View.VISIBLE
                binding.laporanStatsAdmin.visibility = View.VISIBLE

            }
            "User" -> {
                fetchRekapDataPengaduanUser(userId)
                fetchTotalStatsUser(userId)
                binding.rekapDataUser.visibility = View.GONE
                binding.rekapDataPengaduan.visibility = View.VISIBLE
                binding.laporanStatsAdmin.visibility = View.VISIBLE

            }
        }

        // Ambil waktu hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")) // Format: Januari 2025
        val bulanTahun = dateFormat.format(calendar.time)

        // Tampilkan di TextView
        binding.tvTotalStatsTime.text = "Statistik Pengaduan pada $bulanTahun"

        return root
    }

    private fun fetchRekapDataPengaduanAdmin() {
        RetrofitClient.instance.getRekapDataPengaduanAdmin().enqueue(object : Callback<ApiResponse<DashboardData>> {
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

    private fun fetchRekapDataPengaduanUser(userId: Int) {
        val requestBody = mapOf("id_user" to this.userId)

        RetrofitClient.instance.getRekapDataPengaduanUser(requestBody).enqueue(object : Callback<ApiResponse<DashboardData>> {
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

    private fun fetchTotalStatsAdmin() {
        RetrofitClient.instance.getStatsPengaduanAdmin().enqueue(object : Callback<ApiResponse<List<TotalStatsResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<TotalStatsResponse>>>,
                response: Response<ApiResponse<List<TotalStatsResponse>>>
            ) {
                if (response.isSuccessful) {
                    val pengaduanDataList = response.body()?.data
                    if (pengaduanDataList != null) {
                        setupBarChart(pengaduanDataList)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<TotalStatsResponse>>>, t: Throwable) {
                // Handle error
                t.printStackTrace()
            }
        })
    }

    private fun fetchTotalStatsUser(userId: Int) {
        val requestBody = mapOf("instansi_id" to this.userId)

        RetrofitClient.instance.getStatsPengaduanUser(requestBody).enqueue(object : Callback<ApiResponse<List<TotalStatsResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<TotalStatsResponse>>>,
                response: Response<ApiResponse<List<TotalStatsResponse>>>
            ) {
                if (response.isSuccessful) {
                    val pengaduanDataList = response.body()?.data
                    if (pengaduanDataList != null) {
                        setupBarChart(pengaduanDataList)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<TotalStatsResponse>>>, t: Throwable) {
                // Handle error
                t.printStackTrace()
            }
        })
    }

    private fun setupBarChart(pengaduanDataList: List<TotalStatsResponse>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        // Buat map untuk menyimpan data pengaduan per bulan
        val pengaduanMap = mutableMapOf<Int, TotalStatsResponse>()
        for (data in pengaduanDataList) {
            pengaduanMap[data.bulan.toInt()] = data
        }

        // Proses data untuk BarChart (1-12 bulan)
        for (bulan in 1..12) {
            val data = pengaduanMap[bulan]
            val totalPengaduan = data?.let {
                it.totalPengaduan.antrian + it.totalPengaduan.proses +
                        it.totalPengaduan.selesei + it.totalPengaduan.batal
            } ?: 0f // Jika data tidak ada, gunakan 0

            // **Gunakan bulan - 1 agar index sesuai dengan labels**
            entries.add(BarEntry((bulan - 1).toFloat(), totalPengaduan.toFloat()))
            labels.add(getNamaBulan(bulan)) // Fungsi untuk mendapatkan nama bulan
        }

        // Buat BarDataSet
        val dataSet = BarDataSet(entries, "Total Pengaduan")
        dataSet.color = resources.getColor(android.R.color.holo_blue_light)

        // **Menghapus angka desimal pada label nilai di atas bar**
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Ubah ke integer tanpa desimal
            }
        }

        // Buat BarData
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        // Atur data ke BarChart
        binding.laporanDataAdmin.data = barData
        binding.laporanDataAdmin.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.laporanDataAdmin.xAxis.labelCount = labels.size
        binding.laporanDataAdmin.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.laporanDataAdmin.axisLeft.axisMinimum = 0f
        binding.laporanDataAdmin.axisRight.isEnabled = false
        binding.laporanDataAdmin.description.isEnabled = false
        binding.laporanDataAdmin.animateY(1000)
        binding.laporanDataAdmin.invalidate()
    }

    // Fungsi untuk mendapatkan nama bulan berdasarkan angka bulan
    private fun getNamaBulan(bulan: Int): String {
        return when (bulan) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "Mei"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Agu"
            9 -> "Sep"
            10 -> "Okt"
            11 -> "Nov"
            12 -> "Des"
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}