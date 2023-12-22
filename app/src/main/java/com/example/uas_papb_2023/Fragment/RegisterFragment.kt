package com.example.uas_papb_2023.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.Activity.LoginRegisterActivity
import com.example.uas_papb_2023.Model.UserModel
import com.example.uas_papb_2023.Model.UserRole
import com.example.uas_papb_2023.Notif.NotifReceiver
import com.example.uas_papb_2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var userCollection = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = FirebaseAuth.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.email_user)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val registerButton = view.findViewById<Button>(R.id.btnRegister)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Isi email dan password", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val newUser = UserModel(auth.currentUser?.uid, email, password, UserRole.USER)
                    saveUserToFirestore(newUser)
                    Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                    NotifReceiver.showNotification(requireContext(), "Registrasi berhasil! Silahkan Login.")

                    saveLoginStatus(true)
                    finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal registrasi. Periksa kembali email dan password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(newUser: UserModel) {
        userCollection.document(newUser.id ?: "").set(newUser)
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan data pengguna di Firestore.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun finish() {
        val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
