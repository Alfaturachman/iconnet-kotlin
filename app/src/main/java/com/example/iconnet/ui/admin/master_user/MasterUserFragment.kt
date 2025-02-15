package com.example.iconnet.ui.admin.master_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iconnet.R
import com.example.iconnet.api.ApiResponse
import com.example.iconnet.api.RetrofitClient
import com.example.iconnet.model.AllUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MasterUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var userList: List<AllUser> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_master_user, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDaftarUser)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchUsers()
        return view
    }

    private fun fetchUsers() {
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<ApiResponse<List<AllUser>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AllUser>>>,
                response: Response<ApiResponse<List<AllUser>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    userList = response.body()?.data ?: listOf()
                    userAdapter = UserAdapter(userList)
                    recyclerView.adapter = userAdapter
                } else {
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AllUser>>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
