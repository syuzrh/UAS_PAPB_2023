package com.example.uas_papb_2023.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uas_papb_2023.Fragment.LoginFragment
import com.example.uas_papb_2023.Fragment.RegisterFragment

class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment() // Ubah urutan fragmen
            1 -> RegisterFragment() // Ubah urutan fragmen
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Login" // Ubah urutan tab
            1 -> "Register" // Ubah urutan tab
            else -> null
        }
    }
}
