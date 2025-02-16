package com.example.iconnet.ui.admin.master_user

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.AllUser
import com.example.iconnet.model.DeleteRequest
import com.example.iconnet.ui.admin.master_user.edit.EditUserActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAdapter(
    private val userList: List<AllUser>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onUserDeleted: () -> Unit // Callback untuk refresh data
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaInstansi: TextView = view.findViewById(R.id.tvNama)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val username: TextView = view.findViewById(R.id.tvUsername)
        val roleName: TextView = view.findViewById(R.id.tvRole)
        val btnHapus: ImageView = view.findViewById(R.id.btnHapus)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_master_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        val context = holder.itemView.context

        holder.namaInstansi.text = user.namaInstansi
        holder.email.text = user.email
        holder.username.text = user.username
        holder.roleName.text = user.roleName

        // Handle button edit
        holder.btnEdit.setOnClickListener {
            handleButtonEditClick(user, context)
        }

        // Handle button hapus
        holder.btnHapus.setOnClickListener {
            handleButtonHapus(user, context)
        }
    }

    override fun getItemCount(): Int = userList.size

    // Button Edit
    private fun handleButtonEditClick(user: AllUser, context: Context) {
        val intent = Intent(context, EditUserActivity::class.java).apply {
            putExtra("id_user", user.id)
            putExtra("nama_pelanggan", user.namaInstansi)
            putExtra("alamat", user.alamat)
            putExtra("nomor_hp", user.noHp)
            putExtra("email", user.email)
            putExtra("username", user.username)
            putExtra("id_role", user.idRole)
            putExtra("role", user.roleName)
        }
        startForResult.launch(intent)
    }

    // Button Hapus
    private fun handleButtonHapus(user: AllUser, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus akun ${user.namaInstansi} - ${user.roleName}?")
            .setPositiveButton("Hapus") { _, _ ->
                // Panggil API untuk menghapus user
                RetrofitClient.instance.deleteUser(DeleteRequest(user.id)).enqueue(object : Callback<ApiResponse<DeleteRequest>> {
                    override fun onResponse(call: Call<ApiResponse<DeleteRequest>>, response: Response<ApiResponse<DeleteRequest>>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(context, "User berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            onUserDeleted() // Panggil callback untuk refresh data
                        } else {
                            Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Gagal menghapus user!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<DeleteRequest>>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}