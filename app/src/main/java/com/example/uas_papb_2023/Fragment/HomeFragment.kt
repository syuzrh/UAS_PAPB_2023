package com.example.uas_papb_2023.Fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Adapter.FilmAdapter
import com.example.uas_papb_2023.Model.UserRole
import com.example.uas_papb_2023.RoomDatabase.FilmDatabase
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.example.uas_papb_2023.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("films")
    private lateinit var filmListLiveData: MutableLiveData<List<FilmEntity>>
    private lateinit var filmDatabase: FilmDatabase
    private lateinit var filmAdapter: FilmAdapter

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
        filmDatabase = FilmDatabase.getDatabase(requireContext())
        filmListLiveData = MutableLiveData()

        val user = auth.currentUser
        val userEmail = user?.email

        binding.emailUser.text = userEmail

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        filmAdapter = FilmAdapter(requireContext(), emptyList(), UserRole.ADMIN.toString()) { filmEntity ->
        }

        recyclerView.adapter = filmAdapter

        if (isConnectedToInternet()) {
            getFilmDataFromFirebase()
        } else {
            getFilmDataFromRoomDatabase()
        }

        observeFilmList()
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

    private fun getFilmDataFromFirebase() {
        val filmList = mutableListOf<FilmEntity>()

        filmCollectionRef.get().addOnSuccessListener { result ->
            for (document in result) {
                val film = document.toObject(FilmEntity::class.java)
                filmList.add(film)
            }
            filmListLiveData.value = filmList
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error getting data from Firestore", e)
        }
    }

    private fun getFilmDataFromRoomDatabase() {
        lifecycleScope.launch(Dispatchers.Main) {
            val filmList = withContext(Dispatchers.IO) {
                filmDatabase.filmDao().getAllFilmsList()
            }

            filmListLiveData.value = filmList
        }
    }

    private fun observeFilmList() {
        filmListLiveData.observe(viewLifecycleOwner, Observer { filmList ->
            filmAdapter.setData(requireContext(), filmList, UserRole.ADMIN.toString())
        })
    }
}
