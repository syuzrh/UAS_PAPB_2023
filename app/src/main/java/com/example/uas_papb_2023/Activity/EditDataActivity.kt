package com.example.uas_papb_2023.Activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity2
import com.example.uas_papb_2023.databinding.ActivityEditDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDataBinding
    private lateinit var filmEntity2: FilmEntity2
    private lateinit var firestore: FirebaseFirestore
    private lateinit var filmDao: FilmDao
    private lateinit var edtTitle: EditText
    private lateinit var upImage: ImageView
    private lateinit var edtRating: EditText
    private lateinit var edtStoryline: EditText
    private lateinit var edtDirector: EditText
    private lateinit var edtGenre: EditText
    private lateinit var btnEdit: Button
    private var imageUri: Uri? = null
    private val STORAGE = FirebaseStorage.getInstance().reference.child("images")
    private val APP = FirebaseFirestore.getInstance()
    private val MOVIES = APP.collection("films")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backToAdmin.setOnClickListener {
            finish()
        }

        firestore = FirebaseFirestore.getInstance()
        filmDao = FilmDatabase.getDatabase(applicationContext).filmDao()

        edtTitle = findViewById(R.id.edt_title)
        edtRating = findViewById(R.id.edt_rating)
        edtStoryline = findViewById(R.id.edt_storyline)
        edtDirector = findViewById(R.id.edt_director)
        edtGenre = findViewById(R.id.edt_genre)
        btnEdit = findViewById(R.id.btn_edit)
        upImage = findViewById(R.id.up_image)

        filmEntity2 = intent.getParcelableExtra("filmEntity2")!!

        displayCurrentData()

        btnEdit.setOnClickListener {
            val updatedTitle = edtTitle.text.toString()
            val updatedRating = edtRating.text.toString()
            val updatedStoryline = edtStoryline.text.toString()
            val updatedDirector = edtDirector.text.toString()
            val updatedGenre = edtGenre.text.toString()

            if (updatedTitle.isEmpty() || updatedRating.isEmpty() ||
                updatedStoryline.isEmpty() || updatedDirector.isEmpty() || updatedGenre.isEmpty()
            ) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateData(updatedTitle, updatedRating, updatedStoryline, updatedDirector, updatedGenre)
            finish()
        }

        with(binding) {
            upImage.setOnClickListener {
                resultLauncher.launch("image/*")
            }
        }
    }

    private fun displayCurrentData() {
        edtTitle.setText(filmEntity2.title)
        Picasso.get().load(Uri.parse(filmEntity2.imageUrl)).into(binding.upImage)
        edtRating.setText(filmEntity2.rating)
        edtStoryline.setText(filmEntity2.storyline)
        edtDirector.setText(filmEntity2.director)
        edtGenre.setText(filmEntity2.genre)
    }

    private fun updateData(
        updatedTitle: String,
        updatedRating: String,
        updatedStoryline: String,
        updatedDirector: String,
        updatedGenre: String
    ) {
        if (isOnline()) {
            updateDataInFirestore(
                updatedTitle,
                updatedRating,
                updatedStoryline,
                updatedDirector,
                updatedGenre
            )
        } else {
            updateDataInRoom(
                updatedTitle,
                updatedRating,
                updatedStoryline,
                updatedDirector,
                updatedGenre
            )
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        binding.upImage.setImageURI(uri)
        imageUri = uri
    }

    private fun updateDataInFirestore(
        updatedTitle: String,
        updatedRating: String,
        updatedStoryline: String,
        updatedDirector: String,
        updatedGenre: String
    ) {
        val updatedFilm = FilmEntity2(
            id = filmEntity2.id,
            title = updatedTitle,
            imageUrl = filmEntity2.imageUrl,
            rating = updatedRating,
            storyline = updatedStoryline,
            director = updatedDirector,
            genre = updatedGenre
        )

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val imageRef = STORAGE.child("${System.currentTimeMillis()}${imageUri?.lastPathSegment}")

                imageRef.putFile(imageUri!!)
                    .addOnCompleteListener { storageTask ->
                        if (storageTask.isSuccessful) {
                            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                updatedFilm.imageUrl = downloadUrl.toString()

                                Log.d("EditDataActivity", "ID dokumen yang akan diperbarui: ${filmEntity2.id}")

                                MOVIES.document(filmEntity2.id)
                                    .update(
                                        "title", updatedFilm.title,
                                        "imageUrl", updatedFilm.imageUrl,
                                        "rating", updatedFilm.rating,
                                        "storyline", updatedFilm.storyline,
                                        "director", updatedFilm.director,
                                        "genre", updatedFilm.genre
                                    )
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            showToast("Data berhasil diupdate di Firestore")
                                        } else {
                                            showToast("Gagal mengupdate data di Firestore: ${firestoreTask.exception?.message}")
                                            Log.e("EditDataActivity", "Error updating data in Firestore", firestoreTask.exception)
                                        }
                                    }
                            }
                        } else {
                            showToast("Gagal mengunggah gambar ke Firebase Storage: ${storageTask.exception?.message}")
                            Log.e("EditDataActivity", "Error updating data in Firestore ${storageTask.exception?.message}")
                        }
                    }
            } catch (e: Exception) {
                Log.e("EditDataActivity", "Error updating data in Firestore", e)
                showToast("Gagal mengupdate data di Firestore")
            }
        }
    }

    private fun updateDataInRoom(
        updatedTitle: String,
        updatedRating: String,
        updatedStoryline: String,
        updatedDirector: String,
        updatedGenre: String
    ) {
        val updatedFilm = FilmEntity2(
            id = filmEntity2.id,
            title = updatedTitle,
            imageUrl = filmEntity2.imageUrl,
            rating = updatedRating,
            storyline = updatedStoryline,
            director = updatedDirector,
            genre = updatedGenre
        )

        GlobalScope.launch(Dispatchers.IO) {
            filmDao.update(updatedFilm)
            Log.d("EditDataActivity", "Film updated in RoomDatabase")
            showToast("Data berhasil diupdate di RoomDatabase")
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@EditDataActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
