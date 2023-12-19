package com.example.uas_papb_2023.Activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.Model.FilmModel
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.databinding.ActivityAddDataBinding
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
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

        if (title.isNotEmpty() && imageUrl.isNotEmpty() && rating.isNotEmpty() &&
            storyline.isNotEmpty() && director.isNotEmpty() && genre.isNotEmpty()
        ) {
            Log.d("AddDataActivity", "All data is present, adding to Firestore and Room")

            val filmEntity = FilmEntity(
                title = title,
                imageUrl = imageUrl,
                rating = rating,
                storyline = storyline,
                director = director,
                genre = genre
            )

            if (isOnline()) {
                // Jika online, tambahkan data ke Firestore
                filmCollectionRef.add(filmEntity)
                    .addOnSuccessListener {
                        showToast("Film berhasil ditambah ke Firestore")
                        Log.d("AddDataActivity", "Film added to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddDataActivity", "Error adding film to Firestore", e)
                        showToast("Gagal upload data ke Firestore")
                    }
            }

            // Selalu tambahkan data ke Room
            insertFilmToRoom(filmEntity)
            showToast("Film ditambah ke RoomDatabase")

            finish()
        } else {
            Log.d("AddDataActivity", "Some data is missing")
            showToast("Lengkapi semua data")
        }
    }

    private fun insertFilmToRoom(filmEntity: FilmEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            filmDao.insertAll(filmEntity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected ?: false
        }
    }
}
