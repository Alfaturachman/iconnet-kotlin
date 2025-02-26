package com.example.iconnet.ui.teknisi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.iconnet.databinding.FragmentTeknisiBinding
import com.google.android.material.tabs.TabLayoutMediator

class TeknisiFragment : Fragment() {

    private var _binding: FragmentTeknisiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeknisiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager2
        val viewPager = binding.viewPagerTeknisi
        val tabLayout = binding.tabLayoutTeknisi

        val pagerAdapter = PengaduanPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = pagerAdapter
        viewPager.adapter?.notifyDataSetChanged()

        // Hubungkan TabLayout dengan ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Antrian"
                1 -> "Proses"
                2 -> "Selesai"
                3 -> "Batal"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}