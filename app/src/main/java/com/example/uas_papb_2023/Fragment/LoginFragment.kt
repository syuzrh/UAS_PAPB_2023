package com.example.uas_papb_2023.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.Activity.AdminActivity
import com.example.uas_papb_2023.Activity.MainActivity
import com.example.uas_papb_2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var user = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.email_user)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Isi email dan password", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                user.whereEqualTo("email", email).get().addOnSuccessListener {
                    if (task.isSuccessful) {
                        // Simpan informasi pengguna di SharedPreferences
                        saveUserInfoToSharedPreferences(it)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal login. Periksa kembali email dan password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun saveUserInfoToSharedPreferences(querySnapshot: QuerySnapshot) {
        if (!querySnapshot.isEmpty) {
            val userDocument = querySnapshot.documents[0]

            val sharedPreferences =
                requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            editor.putString("email", userDocument.getString("email"))
            editor.putString("userRole", userDocument.getString("userRole"))
            editor.apply()

            onSuccessLogin()
        }
    }

    private fun onSuccessLogin() {
        val sharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
        val userRole = sharedPreferences.getString("userRole", "")
        Log.d("LoginFragment", "User Role: $userRole")

        sharedPreferences.edit {
            putBoolean("userLoggedIn", true)
            putString("userRole", userRole)
        }

        when (userRole) {
            "ADMIN" -> {
                Log.d("LoginFragment", "Redirect to AdminActivity")
                val intent = Intent(requireContext(), AdminActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            else -> {
                Log.d("LoginFragment", "Redirect to MainActivity")
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}
