package com.example.iconnet

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.iconnet.databinding.ActivityMainBinding
import android.content.Context
import com.example.iconnet.databinding.NavHeaderMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "")
        val userNama = sharedPreferences.getString("nama", null)
        val userEmail = sharedPreferences.getString("email", null)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Konfigurasi menu berdasarkan role
        setupMenu(navView.menu, role)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_user, R.id.nav_admin, R.id.nav_teknisi, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Mengakses elemen dalam nav_header_main.xml
        val headerView = navView.getHeaderView(0)
        val navHeaderBinding = NavHeaderMainBinding.bind(headerView)

        // Set properti di header (contoh)
        navHeaderBinding.imageView.setImageResource(R.mipmap.ic_launcher_round)
        navHeaderBinding.tvNavNama.text = userNama
        navHeaderBinding.tvNavRole.text = role
    }

    private fun setupMenu(menu: Menu, role: String?) {
        // Sembunyikan semua menu terlebih dahulu
        menu.findItem(R.id.nav_user)?.isVisible = false
        menu.findItem(R.id.nav_admin)?.isVisible = false
        menu.findItem(R.id.nav_teknisi)?.isVisible = false

        // Tampilkan menu sesuai dengan role
        when (role) {
            "Admin" -> menu.findItem(R.id.nav_admin)?.isVisible = true
            "User" -> menu.findItem(R.id.nav_user)?.isVisible = true
            "Teknisi" -> menu.findItem(R.id.nav_teknisi)?.isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
