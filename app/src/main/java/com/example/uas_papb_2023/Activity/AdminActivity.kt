package com.example.uas_papb_2023.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filmDao = FilmDatabase.getDatabase(applicationContext).filmDao()

        val btnAdd: FloatingActionButton = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivityForResult(intent, ADD_DATA_REQUEST_CODE)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FilmAdapter(this, mutableListOf())
        recyclerView.adapter = adapter

        // Ambil data dari Firestore jika online, jika tidak ambil dari Room Database
        if (isOnline()) {
            loadFilmFromFirestore()
        } else {
            getFilmDataFromRoom()
        }
    }

    private fun isOnline(): Boolean {
        // Implementasi logika untuk memeriksa ketersediaan koneksi internet di sini
        // Return true jika online, false jika offline
        return true
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
            }
        }
    }

    private fun getFilmDataFromRoom() {
        lifecycleScope.launch {
            try {
                val filmList = filmDao.getAllFilmsList()
                Log.d("AdminActivity", "Number of films from Room: ${filmList.size}")

                // Tampilkan dalam RecyclerView
                runOnUiThread {
                    adapter.setData(filmList)
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error retrieving films from Room", e)
            }
        }
    }

    private fun observeFilmChanges() {
        filmListLiveData.observe(this) { film ->
            adapter.setData(film)
        }
    }
}
