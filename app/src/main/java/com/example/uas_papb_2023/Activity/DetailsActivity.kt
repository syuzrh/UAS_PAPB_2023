package com.example.uas_papb_2023.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.R

class DetailsActivity : AppCompatActivity() {

    private val sharedPreferencesKey = "userLoggedIn"
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        sharedPreferences = getSharedPreferences(
            "com.example.uas_papb_2023",
            Context.MODE_PRIVATE
        )

        // Cek apakah pengguna sudah login sebelumnya
        if (!isLoggedIn()) {
            // Pengguna belum login, arahkan ke halaman login
            redirectToLogin()
        } else {
            // Pengguna sudah login, lanjutkan dengan proses normal
            val title = intent.getStringExtra("title")
            val imageUrl = intent.getStringExtra("imageUrl")
            val rating = intent.getStringExtra("rating")
            val storyline = intent.getStringExtra("storyline")
            val director = intent.getStringExtra("director")
            val genre = intent.getStringExtra("genre")

            val imgFilm: ImageView = findViewById(R.id.filmImage)
            val txtTitle: TextView = findViewById(R.id.title_id)
            val txtDirector: TextView = findViewById(R.id.director_id)
            val txtRating: TextView = findViewById(R.id.rate_id)
            val txtStoryline: TextView = findViewById(R.id.story_line_id)
            val txtGenre: TextView = findViewById(R.id.genre)

            val BackImageView: ImageView = findViewById(R.id.backToHome)
            BackImageView.setOnClickListener {
                finish()
            }

            Glide.with(this)
                .load(imageUrl)
                .into(imgFilm)

            txtTitle.text = title
            txtDirector.text = "$director |"
            txtRating.text = rating
            txtStoryline.text = storyline
            txtGenre.text = genre
        }
    }

    private fun isLoggedIn(): Boolean {
        // Mengambil status login dari SharedPreferences
        return sharedPreferences.getBoolean(sharedPreferencesKey, false)
    }

    private fun redirectToLogin() {
        // Mengarahkan pengguna ke halaman login
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
