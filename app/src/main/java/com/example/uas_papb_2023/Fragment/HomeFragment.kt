package com.example.uas_papb_2023.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Adapter.FilmAdapter
import com.example.uas_papb_2023.Model.FilmModel
import com.example.uas_papb_2023.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

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

        val user = auth.currentUser
        val userEmail = user?.email

        binding.emailUser.text = userEmail

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val filmList = generateFilmList()
        val adapter = FilmAdapter(requireContext(), filmList)
        recyclerView.adapter = adapter
    }

    private fun generateFilmList(): List<FilmModel> {
        return listOf(
            FilmModel("The Conjuring", "https://upload.wikimedia.org/wikipedia/id/1/1f/Conjuring_poster.jpg", "Rating 5/5", "Paranormal investigators Ed and Lorraine Warren work to help a family terrorized by an evil presence in their home.", "James Wan","Horror"),
            FilmModel("A Nightmare on Elm Street", "https://posterplus.com.au/files/2020/07/48899-nightmare-elm-street.jpg", "Rating 4/5", "A hitman with a unique characteristic—attacking young people through their dreams.", "Wes Craven","Horror"),
            FilmModel("Get Out", "https://upload.wikimedia.org/wikipedia/id/0/07/Get_Out_Jordan_Peele_Poster.jpg", "Rating 4.5/5","An African American man visits his white girlfriend's family, where dark mysteries and hypnotism reveal horrifying racial fears.","Jordan Peele","Horror"),
            FilmModel("Psycho", "https://www.filmposters.com/images/posters/2977.jpg", "Rating 4/5","A secretary embezzles money from her employer and goes on the run, checking into a remote motel run by a disturbed innkeeper.","Alfred Hitchcock","Horror"),
            FilmModel("The Shining", "https://i.pinimg.com/564x/b6/f9/28/b6f9288890a3c9d0b8db53c0dad645e4.jpg", "Rating 5/5","A writer and his family stay in an isolated hotel during the winter, where terrifying supernatural presences begin to haunt them.","Stanley Kubrick","Horror"),
            FilmModel("The Exorcist", "https://cinemags.org/wp-content/uploads/2023/08/The-Exorcist-Believer-p.jpg", "Rating 4.5/5","A young girl is suspected to be possessed by a demon, and a Catholic priest attempts an exorcism to save her.","William Friedkin","Horror")
        )
    }
}