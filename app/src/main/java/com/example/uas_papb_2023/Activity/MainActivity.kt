package com.example.uas_papb_2023.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.Fragment.AccountFragment
import com.example.uas_papb_2023.Fragment.HomeFragment
import com.example.uas_papb_2023.databinding.ActivityMainBinding
import com.example.uas_papb_2023.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedPreferencesKey = "userLoggedIn"
    private val sharedPreferencesRole = "userRole"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_account -> replaceFragment(AccountFragment())
            }
            true
        }

        if (!isLoggedIn()) {
            Log.d("Cek Login", "Pengguna belum login atau memiliki peran selain \"USER\"")
            redirectToLogin()
        } else {
            replaceFragment(HomeFragment())
        }
    }

    private fun isLoggedIn(): Boolean {
        val isLoggedIn = sharedPreferences.getBoolean(sharedPreferencesKey, false)
        val userRole = sharedPreferences.getString(sharedPreferencesRole, "")

        return isLoggedIn && userRole == "USER"
    }

    private fun redirectToLogin() {
        val intent = Intent(this@MainActivity, LoginRegisterActivity::class.java)
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
