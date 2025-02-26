package com.example.iconnet.ui.teknisi

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
import com.example.iconnet.ui.teknisi.detail.DetailTugasActivity

class TugasAdapter(
    private val context: Context,
    private val pengaduanList: List<Pengaduan>,
    private val startForResult: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvIsiPengaduan: TextView = view.findViewById(R.id.tvIsiPengaduan)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamat)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnDetail: ImageView = view.findViewById(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_tugas_teknisi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pengaduan = pengaduanList[position]
        holder.tvJudul.text = "${pengaduan.judulPengaduan}"
        holder.tvIsiPengaduan.text = pengaduan.isiPengaduan
        holder.tvAlamat.text = pengaduan.alamatPengaduan
        holder.tvTanggal.text = pengaduan.tglPengaduan

        holder.tvStatus.text = getStatusText(pengaduan.statusPengaduan.toInt())

        // Ambil konteks
        val context = holder.itemView.context

        // Warna Background status
        val statusColor = when (pengaduan.statusPengaduan.toInt()) {
            0 -> ContextCompat.getColorStateList(context, R.color.badge_warning)
            1 -> ContextCompat.getColorStateList(context, R.color.badge_primary)   // Proses (Biru)
            2 -> ContextCompat.getColorStateList(context, R.color.badge_success) // Selesai (Hijau)
            3 -> ContextCompat.getColorStateList(context, R.color.badge_danger)    // Batal (Merah)
            else -> ContextCompat.getColorStateList(context, R.color.badge_dark) // Default Hitam
        }

        // Warna Text status
        val statusTextColor = when (pengaduan.statusPengaduan.toInt()) {
            0 -> ContextCompat.getColor(context, R.color.white) // Antrian (Teks hitam agar kontras dengan kuning)
            1 -> ContextCompat.getColor(context, R.color.white) // Proses (Teks putih agar kontras dengan biru)
            2 -> ContextCompat.getColor(context, R.color.white) // Selesai (Teks putih agar kontras dengan hijau)
            3 -> ContextCompat.getColor(context, R.color.white) // Batal (Teks putih agar kontras dengan merah)
            else -> ContextCompat.getColor(context, R.color.white) // Default Putih
        }

        // Set warna background dan teks pada TextView status
        holder.tvStatus.backgroundTintList = statusColor
        holder.tvStatus.setTextColor(statusTextColor)

        // Handle klik pada tombol detail
        holder.btnDetail.setOnClickListener {
            handleDetailClick(pengaduan, context)
        }
    }

    override fun getItemCount(): Int = pengaduanList.size

    // Fungsi untuk menangani klik tombol detail
    private fun handleDetailClick(pengaduan: Pengaduan, context: android.content.Context) {
        val intent = Intent(context, DetailTugasActivity::class.java).apply {
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
