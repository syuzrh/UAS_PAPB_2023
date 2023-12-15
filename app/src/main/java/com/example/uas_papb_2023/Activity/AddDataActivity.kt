package com.example.uas_papb_2023.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.Model.FilmModel
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.databinding.ActivityAddDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDataBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("films")
    private lateinit var filmDao: FilmDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        filmDao = FilmDatabase.getDatabase(applicationContext).filmDao()

        binding.btnAdd.setOnClickListener {
            addFilmToFirestoreAndRoom()
        }

        // Handle click backToAdmin
        binding.backToAdmin.setOnClickListener {
            finish()
        }
    }

    private fun addFilmToFirestoreAndRoom() {
        Log.d("AddDataActivity", "Trying to add film to Firestore")

        val title = binding.titleMv.text.toString()
        val imageUrl = binding.imgUrl.text.toString()
        val rating = binding.ratingMv.text.toString()
        val storyline = binding.storylineMv.text.toString()
        val director = binding.directorMv.text.toString()
        val genre = binding.genreMv.text.toString()

        // Validasi data sebelum ditambahkan
        if (title.isNotEmpty() && imageUrl.isNotEmpty() && rating.isNotEmpty() &&
            storyline.isNotEmpty() && director.isNotEmpty() && genre.isNotEmpty()
        ) {
            Log.d("AddDataActivity", "All data is present, adding to Firestore and Room")
            // Buat objek FilmModel
            val film = FilmModel(title, imageUrl, rating, storyline, director, genre)
            Log.d("AddDataActivity", "Film: $film")

            // Tambahkan data ke Firestore
            filmCollectionRef.add(film)
                .addOnSuccessListener {
                    // Setelah berhasil menambahkan ke Firestore, tambahkan juga ke Room Database
                    val filmEntity = FilmEntity(
                        title = title,
                        imageUrl = imageUrl,
                        rating = rating,
                        storyline = storyline,
                        director = director,
                        genre = genre
                    )
                    showToast("success to add film")

                    Log.d("AddDataActivity", "Film added to Firestore and Room")
                    // Setelah berhasil menambahkan, kembali ke halaman sebelumnya
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("AddDataActivity", "Error adding film to Firestore", e)
                    showToast("Failed to add film")
                }
        } else {
            // Tampilkan pesan error jika ada data yang kosong
            Log.d("AddDataActivity", "Some data is missing")
            showToast("Please complete all data")
        }
    }

    private fun insertFilmToRoom(filmEntity: FilmEntity) {
        GlobalScope.launch {
            filmDao.insertAll(filmEntity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
