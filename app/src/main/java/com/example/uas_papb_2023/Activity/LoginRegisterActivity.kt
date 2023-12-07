package com.example.uas_papb_2023.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_papb_2023.Adapter.TabAdapter
import com.example.uas_papb_2023.Fragment.LoginFragment
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.databinding.ActivityLoginRegisterBinding

// Dalam LoginRegisterActivity
class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabAdapter = TabAdapter(supportFragmentManager)
        binding.viewPager.adapter = tabAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}
