package com.example.uas_papb_2023.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.Activity.LoginRegisterActivity
import com.example.uas_papb_2023.R
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var userEmail: TextView
    private lateinit var userType: TextView
    private lateinit var logoutButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()

        profileImage = view.findViewById(R.id.profileImage)
        userEmail = view.findViewById(R.id.email_user)
        userType = view.findViewById(R.id.type)
        logoutButton = view.findViewById(R.id.logoutButton)

        val user = auth.currentUser
        val userEmailText = user?.email ?: "Email"
        userEmail.text = userEmailText

        userType.text = "Public User"

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
