package com.example.uas_papb_2023.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.R

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

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

        // Menggunakan Glide untuk memuat gambar
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
