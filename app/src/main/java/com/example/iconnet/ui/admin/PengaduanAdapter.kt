package com.example.iconnet.ui.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.model.Pengaduan
import com.example.iconnet.ui.admin.tambah_tugas.TambahTugasActivity

class PengaduanAdapter(private val pengaduanList: List<Pengaduan>) :
    RecyclerView.Adapter<PengaduanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIdPelanggan: TextView = view.findViewById(R.id.tvIdPelanggan)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvIsiPengaduan: TextView = view.findViewById(R.id.tvIsiPengaduan)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.status)
        val btnDetail: ImageView = view.findViewById(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_pengaduan_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pengaduan = pengaduanList[position]
        // Ambil konteks
        val context = holder.itemView.context

        holder.tvIdPelanggan.text = "ID Pengaduan #${pengaduan.idPengaduan}"
        holder.tvNama.text = pengaduan.namaUser
        holder.tvIsiPengaduan.text = pengaduan.isiPengaduan
        holder.tvTanggal.text = pengaduan.tglPengaduan
        holder.tvStatus.text = getStatusText(pengaduan.statusPengaduan.toInt())

        // Tentukan warna background berdasarkan status
        val statusColor = when (pengaduan.statusPengaduan.toInt()) {
            0 -> ContextCompat.getColorStateList(context, R.color.yellow)
            1 -> ContextCompat.getColorStateList(context, R.color.blue)
            2 -> ContextCompat.getColorStateList(context, R.color.primary)
            3 -> ContextCompat.getColorStateList(context, R.color.red)
            else -> ContextCompat.getColorStateList(context, R.color.black)
        }

        // Set background tint
        holder.tvStatus.backgroundTintList = statusColor

        // Handle klik pada tombol detail
        holder.btnDetail.setOnClickListener {
            handleAmbilClick(pengaduan, context)
        }
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

    // Fungsi untuk menangani klik tombol detail
    private fun handleAmbilClick(pengaduan: Pengaduan, context: android.content.Context) {
        val intent = Intent(context, TambahTugasActivity::class.java).apply {
            putExtra("id_pengaduan", pengaduan.idPengaduan)
            putExtra("tanggal_pengaduan", pengaduan.tglPengaduan)
            putExtra("nama_pelanggan", pengaduan.namaUser)
            putExtra("judul_pengaduan", pengaduan.judulPengaduan)
            putExtra("isi_pengaduan", pengaduan.isiPengaduan)
        }
        context.startActivity(intent)
    }
}