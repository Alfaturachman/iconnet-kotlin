package com.example.iconnet.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    /**
     * Memformat tanggal dari string ke format yang diinginkan.
     *
     * @param tanggal String tanggal yang akan diformat.
     * @return String tanggal yang sudah diformat, atau "N/A" jika gagal.
     */

    fun formatTanggal(tanggal: String): String {
        return try {
            // Format input (sesuaikan dengan format asli tanggal)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date: Date = inputFormat.parse(tanggal) ?: return "N/A"

            // Format output
            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            "N/A" // Jika parsing gagal, kembalikan "N/A"
        }
    }
}