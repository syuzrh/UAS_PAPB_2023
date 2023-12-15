package com.example.uas_papb_2023.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.Adapter.TabAdapter
import com.example.uas_papb_2023.databinding.ActivityLoginRegisterBinding

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    private val sharedPreferencesKey = "userLoggedIn"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            "com.example.uas_papb_2023",
            Context.MODE_PRIVATE
        )

        val tabAdapter = TabAdapter(supportFragmentManager)
        binding.viewPager.adapter = tabAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    // Metode untuk mengatur status login di SharedPreferences
    fun setLoggedInStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(sharedPreferencesKey, isLoggedIn)
        editor.apply()
    }

    // Metode untuk mengarahkan pengguna ke MainActivity
    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Metode untuk mengeksekusi setelah pengguna berhasil login
    fun onSuccessLogin() {
        // Mengatur status login ke true
        setLoggedInStatus(true)
        // Mengarahkan pengguna ke MainActivity
        redirectToMain()
    }
}
