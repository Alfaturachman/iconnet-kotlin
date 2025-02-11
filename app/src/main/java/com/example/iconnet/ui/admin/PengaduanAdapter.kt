package com.example.iconnet.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.model.Pengaduan

class PengaduanAdapter(private val pengaduanList: List<Pengaduan>) :
    RecyclerView.Adapter<PengaduanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIdPelanggan: TextView = view.findViewById(R.id.tvIdPelanggan)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvIsiPengaduan: TextView = view.findViewById(R.id.tvIsiPengaduan)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_pengaduan_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pengaduan = pengaduanList[position]

        holder.tvIdPelanggan.text = "ID Pengaduan #${pengaduan.idPelanggan}"
        holder.tvNama.text = pengaduan.namaUser
        holder.tvIsiPengaduan.text = pengaduan.isiPengaduan
        holder.tvTanggal.text = pengaduan.tglPengaduan

        // Konversi status menjadi teks yang sesuai
        holder.tvStatus.text = getStatusText(pengaduan.statusPengaduan.toInt())

        // Ubah warna status berdasarkan status pengaduan
        val context = holder.itemView.context
        val statusColor = when (pengaduan.statusPengaduan.toInt()) {
            0 -> ContextCompat.getColor(context, R.color.yellow) // Antrian (Kuning)
            1 -> ContextCompat.getColor(context, R.color.blue)   // Proses (Biru)
            2 -> ContextCompat.getColor(context, R.color.primary)  // Selesai (Hijau)
            3 -> ContextCompat.getColor(context, R.color.red)    // Batal (Merah)
            else -> Color.BLACK // Default warna hitam jika status tidak valid
        }
        holder.tvStatus.setBackgroundColor(statusColor)
    }

    override fun getItemCount(): Int = pengaduanList.size

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