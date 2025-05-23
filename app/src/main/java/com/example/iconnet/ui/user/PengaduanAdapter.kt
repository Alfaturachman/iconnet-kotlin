package com.example.iconnet.ui.user

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.tambah_tugas.TambahTugasActivity
import com.example.iconnet.ui.user.detail.DetailPengaduanActivity

class PengaduanAdapter(
    private val context: Context,
    private val pengaduanList: List<Pengaduan>,
    private val startForResult: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<PengaduanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvIsiPengaduan: TextView = view.findViewById(R.id.tvIsiPengaduan)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnDetail: ImageView = view.findViewById(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_pengaduan_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pengaduan = pengaduanList[position]
        holder.tvJudul.text = "${pengaduan.judulPengaduan}"
        holder.tvIsiPengaduan.text = pengaduan.isiPengaduan
        holder.tvTanggal.text = pengaduan.tglPengaduan

        holder.tvStatus.text = getStatusText(pengaduan.statusPengaduan.toInt())

        // Ambil konteks
        val context = holder.itemView.context

        // Warna Background status
        val statusColor = when (pengaduan.statusPengaduan.toInt()) {
            0 -> ContextCompat.getColorStateList(context, R.color.yellow) // Antrian (Kuning)
            1 -> ContextCompat.getColorStateList(context, R.color.blue)   // Proses (Biru)
            2 -> ContextCompat.getColorStateList(context, R.color.primary)  // Selesai (Hijau)
            3 -> ContextCompat.getColorStateList(context, R.color.red)    // Batal (Merah)
            else -> ContextCompat.getColorStateList(context, R.color.black) // Default Hitam
        }

        // Set background tint
        holder.tvStatus.backgroundTintList = statusColor

        // Handle klik pada tombol detail
        holder.btnDetail.setOnClickListener {
            handleDetailClick(pengaduan, context)
        }
    }

    override fun getItemCount(): Int = pengaduanList.size

    // Fungsi untuk menangani klik tombol detail
    private fun handleDetailClick(pengaduan: Pengaduan, context: android.content.Context) {
        val intent = Intent(context, DetailPengaduanActivity::class.java).apply {
            putExtra("id_pengaduan", pengaduan.idPengaduan?.toIntOrNull() ?: -1)
        }
        // Gunakan startForResult yang diterima dari konstruktor
        startForResult.launch(intent)
    }

    // Fungsi untuk mengonversi angka status menjadi teks
    private fun getStatusText(status: Int): String {
        return when (status) {
            0 -> "Antrian"
            1 -> "Proses"
            2 -> "Selesai"
            3 -> "Batal"
            else -> "Tidak Diketahui"
        }
    }
}
