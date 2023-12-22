package com.example.uas_papb_2023.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_papb_2023.R
import com.squareup.picasso.Picasso

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

        if (!isLoggedIn()) {
            redirectToLogin()
        } else {
            val title = intent.getStringExtra("title")
            val imageUrl = intent.getStringExtra("imageUrl")
            val rating = intent.getStringExtra("rating")
            val storyline = intent.getStringExtra("storyline")
            val director = intent.getStringExtra("director")
            val genre = intent.getStringExtra("genre")

            val imgFilm: ImageView = findViewById(R.id.filmImage)
            imgFilm.setImageURI(Uri.parse(imageUrl))
            Picasso.get().load(imageUrl).into(imgFilm)
            Log.d("url film", imgFilm.id.toString())
            val txtTitle: TextView = findViewById(R.id.title_id)
            val txtDirector: TextView = findViewById(R.id.director_id)
            val txtRating: TextView = findViewById(R.id.rate_id)
            val txtStoryline: TextView = findViewById(R.id.story_line_id)
            val txtGenre: TextView = findViewById(R.id.genre)

            val BackImageView: ImageView = findViewById(R.id.backToHome)
            BackImageView.setOnClickListener {
                finish()
            }

            txtTitle.text = title
            txtDirector.text = "$director |"
            txtRating.text = rating
            txtStoryline.text = storyline
            txtGenre.text = genre
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(sharedPreferencesKey, false)
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
