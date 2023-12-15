package com.example.uas_papb_2023.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.Fragment.AccountFragment
import com.example.uas_papb_2023.Fragment.FavoriteFragment
import com.example.uas_papb_2023.Fragment.HomeFragment
import com.example.uas_papb_2023.databinding.ActivityMainBinding
import com.example.uas_papb_2023.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedPreferencesKey = "userLoggedIn"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            "com.example.uas_papb_2023",
            Context.MODE_PRIVATE
        )

        // Cek apakah pengguna sudah login sebelumnya
        if (!isLoggedIn()) {
            // Pengguna belum login, arahkan ke halaman login
            redirectToLogin()
        } else {
            // Pengguna sudah login, lanjutkan dengan proses normal
            replaceFragment(HomeFragment())

            binding.bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> replaceFragment(HomeFragment())
                    R.id.nav_favorite -> replaceFragment(FavoriteFragment())
                    R.id.nav_account -> replaceFragment(AccountFragment())
                }
                true
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        // Mengambil status login dari SharedPreferences
        return sharedPreferences.getBoolean(sharedPreferencesKey, false)
    }

    private fun redirectToLogin() {
        // Mengarahkan pengguna ke halaman login
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
