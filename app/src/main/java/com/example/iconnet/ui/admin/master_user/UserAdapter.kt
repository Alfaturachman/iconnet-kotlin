package com.example.iconnet.ui.admin.master_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.model.AllUser

class UserAdapter(private val userList: List<AllUser>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaInstansi: TextView = view.findViewById(R.id.tvNama)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val username: TextView = view.findViewById(R.id.tvUsername)
        val roleName: TextView = view.findViewById(R.id.tvRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_master_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.namaInstansi.text = user.namaInstansi
        holder.email.text = user.email
        holder.username.text = user.username
        holder.roleName.text = user.roleName
    }

    override fun getItemCount(): Int = userList.size
}
