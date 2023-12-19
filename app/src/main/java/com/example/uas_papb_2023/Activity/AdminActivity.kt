package com.example.uas_papb_2023.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Adapter.FilmAdapter
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.example.uas_papb_2023.databinding.ActivityAdminBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var filmDao: FilmDao
    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("films")
    private val filmListLiveData: MutableLiveData<List<FilmEntity>> by lazy {
        MutableLiveData<List<FilmEntity>>()
    }
    companion object {
        private const val ADD_DATA_REQUEST_CODE = 1
    }

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferencesKey = "userLoggedIn"
    private val sharedPreferencesRole = "userRole"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi adapter dengan parameter yang sesuai
        adapter = FilmAdapter(this, mutableListOf(), "ADMIN") { filmEntity ->
            showDeleteConfirmationDialog(filmEntity)
        }

        // Inisialisasi filmDao dengan instance dari FilmDatabase
        filmDao = FilmDatabase.getDatabase(applicationContext).filmDao()

        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        val btnAdd: FloatingActionButton = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivityForResult(intent, ADD_DATA_REQUEST_CODE)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        if (isOnline()) {
            loadFilmFromFirestore()
        } else {
            getFilmDataFromRoom()
        }
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

    private fun showDeleteConfirmationDialog(filmEntity: FilmEntity) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus film ini?")

        builder.setPositiveButton("Ya") { _, _ ->
            // Hapus film dari Firestore
            deleteFilmFromFirestore(filmEntity)

            // Hapus film dari RoomDatabase
            deleteFilmFromRoom(filmEntity)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // Batal
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFilmFromFirestore(filmEntity: FilmEntity) {
        filmCollectionRef
            .whereEqualTo("title", filmEntity.title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    filmCollectionRef.document(document.id).delete()
                    Log.d("AdminActivity", "Film deleted from Firestore")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminActivity", "Error deleting film from Firestore", e)
            }
    }

    private fun deleteFilmFromRoom(filmEntity: FilmEntity) {
        GlobalScope.launch {
            filmDao.delete(filmEntity)
            Log.d("AdminActivity", "Film deleted from RoomDatabase")

            // Setelah menghapus data, perbarui status login dan peran pengguna di SharedPreferences
            sharedPreferences.edit {
                putBoolean(sharedPreferencesKey, true)
                putString(sharedPreferencesRole, "ADMIN")
            }
        }
    }

    private fun loadFilmFromFirestore() {
        Log.d("AdminActivity", "Mengambil data dari Firestore")
        filmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("AdminActivity", "Error listening for film changes: ", error)
                return@addSnapshotListener
            }
            val films = snapshots?.toObjects(FilmEntity::class.java)
            if (films != null) {
                filmListLiveData.postValue(films)
                observeFilmChanges()
                Log.d("AdminActivity", "Data berhasil diambil dari Firestore: ${films.size} item")

                // Setelah mengambil data, perbarui status login dan peran pengguna di SharedPreferences
                sharedPreferences.edit {
                    putBoolean(sharedPreferencesKey, true)
                    putString(sharedPreferencesRole, "ADMIN")
                }
            }
        }
    }

    private fun getFilmDataFromRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("AdminActivity", "Trying to get films from Room")
                val filmList = filmDao.getAllFilmsList()

                runOnUiThread {
                    // Setelah mengambil data, perbarui status login dan peran pengguna di SharedPreferences
                    adapter.setData(this@AdminActivity, filmList, "ADMIN")
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error retrieving films from Room", e)
            }
        }
    }

    private fun observeFilmChanges() {
        filmListLiveData.observe(this) { filmList ->
            // Perbarui adapter dengan data yang diperoleh dari LiveData
            adapter.setData(this@AdminActivity, filmList ?: listOf(), "ADMIN")
        }
    }
}
