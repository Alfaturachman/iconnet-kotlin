package com.example.iconnet.ui.teknisi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.iconnet.ui.teknisi.status_pengaduan.AntrianTeknisiFragment
import com.example.iconnet.ui.teknisi.status_pengaduan.ProsesTeknisiFragment
import com.example.iconnet.ui.teknisi.status_pengaduan.SelesaiTeknisiFragment
import com.example.iconnet.ui.teknisi.status_pengaduan.BatalTeknisiFragment

class PengaduanPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AntrianTeknisiFragment()
            1 -> ProsesTeknisiFragment()
            2 -> SelesaiTeknisiFragment()
            3 -> BatalTeknisiFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}