package com.example.iconnet.ui.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.iconnet.ui.admin.status_pengaduan.AntrianFragment
import com.example.iconnet.ui.admin.status_pengaduan.ProsesFragment
import com.example.iconnet.ui.admin.status_pengaduan.SelesaiFragment
import com.example.iconnet.ui.admin.status_pengaduan.BatalFragment

class PengaduanPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AntrianFragment()
            1 -> ProsesFragment()
            2 -> SelesaiFragment()
            3 -> BatalFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}