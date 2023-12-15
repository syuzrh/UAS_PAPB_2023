package com.example.uas_papb_2023.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Adapter.FilmAdapter
import com.example.uas_papb_2023.Model.FilmModel
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.example.uas_papb_2023.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("films")

        val user = auth.currentUser
        val userEmail = user?.email

        binding.emailUser.text = userEmail

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dapatkan data film dari Firebase
        getFilmData()
    }

    private fun getFilmData() {
        val filmList = mutableListOf<FilmModel>()
        val recyclerView: RecyclerView = binding.recyclerView

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (filmSnapshot in snapshot.children) {
                    val film = filmSnapshot.getValue(FilmModel::class.java)
                    film?.let { filmList.add(it) }
                }

                // Setelah mendapatkan data dari Firebase, tampilkan dalam RecyclerView
                val adapter = FilmAdapter(requireContext(), convertToFilmEntities(filmList))
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                // Contoh: Menampilkan pesan kesalahan menggunakan Log
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun convertToFilmEntities(filmList: List<FilmModel>): List<FilmEntity> {
        return filmList.map { filmModel ->
            FilmEntity(
                title = filmModel.title,
                imageUrl = filmModel.imageUrl,
                rating = filmModel.rating,
                storyline = filmModel.storyline, // Pastikan properti ini ada
                director = filmModel.director,
                genre = filmModel.genre
            )
        }
    }

}
