package com.example.uas_papb_2023.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Adapter.FilmAdapter
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.RoomDatabase.FilmDao
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity2
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
    private val filmListLiveData: MutableLiveData<List<FilmEntity2>> by lazy {
        MutableLiveData<List<FilmEntity2>>()
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

        adapter = FilmAdapter(this, mutableListOf(), "ADMIN",
            { filmEntity -> showDeleteConfirmationDialog(filmEntity) },
            { filmEntity -> editFilm(filmEntity) }
        )

        filmDao = FilmDatabase.getDatabase(applicationContext).filmDao()

        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        val btnAdd: FloatingActionButton = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivityForResult(intent, ADD_DATA_REQUEST_CODE)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

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

    private fun showDeleteConfirmationDialog(filmEntity2: FilmEntity2) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus film ini?")

        builder.setPositiveButton("Ya") { _, _ ->
            deleteFilmFromFirestore(filmEntity2)

            deleteFilmFromRoom(filmEntity2)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFilmFromFirestore(filmEntity2: FilmEntity2) {
        filmCollectionRef
            .whereEqualTo("title", filmEntity2.title)
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

    private fun deleteFilmFromRoom(filmEntity2: FilmEntity2) {
        GlobalScope.launch {
            filmDao.delete(filmEntity2)
            Log.d("AdminActivity", "Film deleted from RoomDatabase")

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
                Log.e("AdminActivity", "Error listening for film changes", error)
                return@addSnapshotListener
            }

            try {
                val films = snapshots?.toObjects(FilmEntity2::class.java)
                if (films != null) {
                    filmListLiveData.postValue(films)
                    observeFilmChanges()
                    Log.d("AdminActivity", "Data berhasil diambil dari Firestore: ${films.size} item")

                    sharedPreferences.edit {
                        putBoolean(sharedPreferencesKey, true)
                        putString(sharedPreferencesRole, "ADMIN")
                    }
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error converting Firestore snapshot to FilmEntity2", e)
            }
        }
    }


    private fun getFilmDataFromRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("AdminActivity", "Trying to get films from Room")
                val filmList = filmDao.getAllFilmsList()

                runOnUiThread {
                    adapter.setData(this@AdminActivity, filmList, "ADMIN")
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error retrieving films from Room", e)
            }
        }
    }

    private fun observeFilmChanges() {
        filmListLiveData.observe(this) { filmList ->
            adapter.setData(this@AdminActivity, filmList ?: listOf(), "ADMIN")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_logout -> {
                val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
                val userRole = sharedPreferences.getString("userRole", "")

                if (userRole == "ADMIN") {
                    logoutAdmin()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutAdmin() {
        val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean("userLoggedIn", false)
            putString("userRole", "")
        }

        val intent = Intent(this@AdminActivity, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun editFilm(filmEntity2: FilmEntity2) {
        if (isOnline()) {
            getFilmFromFirestoreForEdit(filmEntity2)
        } else {
            openEditForm(filmEntity2)
        }
    }

    private fun getFilmFromFirestoreForEdit(filmEntity2: FilmEntity2) {
        filmCollectionRef
            .whereEqualTo("title", filmEntity2.title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val film = document.toObject(FilmEntity2::class.java)
                    openEditForm(film)
                    break
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminActivity", "Error getting film data from Firestore for edit", e)
            }
    }

    private fun openEditForm(filmEntity2: FilmEntity2) {
        val intent = Intent(this@AdminActivity, EditDataActivity::class.java)
        intent.putExtra("filmEntity2", filmEntity2)
        startActivity(intent)
    }

}
