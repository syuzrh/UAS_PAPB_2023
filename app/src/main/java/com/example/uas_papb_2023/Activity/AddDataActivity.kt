package com.example.uas_papb_2023.Activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.databinding.ActivityAddDataBinding
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var filmDao: FilmDao
    private var imageUri:Uri? = null
    private val STORAGE = FirebaseStorage.getInstance().reference.child("images")
    private val APP = FirebaseFirestore.getInstance()
    private val MOVIES = APP.collection("films")

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

        with(binding){
            addImage.setOnClickListener {
                resultLauncher.launch("image/*")
            }
        }
    }

    private fun addFilmToFirestoreAndRoom() {
        Log.d("AddDataActivity", "Trying to add film to Firestore")

        val title = binding.titleMv.text.toString()
        val rating = binding.ratingMv.text.toString()
        val storyline = binding.storylineMv.text.toString()
        val director = binding.directorMv.text.toString()
        val genre = binding.genreMv.text.toString()
        val store = STORAGE.child(System.currentTimeMillis().toString())


        if (title.isNotEmpty() && rating.isNotEmpty() &&
            storyline.isNotEmpty() && director.isNotEmpty() && genre.isNotEmpty() && isOnline()
        ) {
            imageUri?.let{
                it1 -> store.putFile(it1).addOnCompleteListener(){
                    Log.d("errorrrrrr", it.exception.toString())
                    if (it.isSuccessful){
                        store.downloadUrl.addOnSuccessListener { uri->
                            val film = FilmEntity2(
                                title = title,
                                imageUrl = uri.toString(),
                                rating = rating,
                                storyline = storyline,
                                director = director,
                                genre = genre
                            )

                            MOVIES.add(film).addOnSuccessListener { res ->
                                film.id = res.id
                                res.set(film).addOnSuccessListener {
                                    Toast.makeText(this@AddDataActivity, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show()
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(this@AddDataActivity, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener(){err->
                                Toast.makeText(this@AddDataActivity, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Log.d("AddDataActivity", "Gagal upload gambar error : ${it.exception.toString()}")
                        Toast.makeText(this@AddDataActivity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            }

            Log.d("AddDataActivity", "All data is present, adding to Firestore and Room")

            val filmEntity2 = FilmEntity2(
                title = title,
                imageUrl = imageUri.toString(),
                rating = rating,
                storyline = storyline,
                director = director,
                genre = genre
            )

            insertFilmToRoom(filmEntity2)
            showToast("Film ditambah ke RoomDatabase")
            finish()
        } else {
            Log.d("AddDataActivity", "Some data is missing")
            showToast("Lengkapi semua data")
        }
    }

    private fun insertFilmToRoom(filmEntity2: FilmEntity2) {
        GlobalScope.launch(Dispatchers.IO) {
            filmDao.insertAll(filmEntity2)
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

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
            imageUri = it
            binding.addImage.setImageURI(it)
        }
}
