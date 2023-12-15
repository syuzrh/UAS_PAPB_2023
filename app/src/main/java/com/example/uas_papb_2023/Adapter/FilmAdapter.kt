package com.example.uas_papb_2023.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.Activity.DetailsActivity
import com.example.uas_papb_2023.Model.FilmModel
import com.example.uas_papb_2023.R
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.RoomDatabase.FilmEntity

class FilmAdapter(private val context: Context, private var filmList: List<FilmEntity>) :
    RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFilm: ImageView = itemView.findViewById(R.id.imgFilm)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val imgRating: TextView = itemView.findViewById(R.id.imgRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = filmList[position]

        Glide.with(context)
            .load(film.imageUrl)
            .into(holder.imgFilm)

        holder.txtTitle.text = film.title
        holder.imgRating.text = film.rating

        holder.imgFilm.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("title", film.title)
            intent.putExtra("imageUrl", film.imageUrl)
            intent.putExtra("rating", film.rating)
            intent.putExtra("storyline", film.storyline)
            intent.putExtra("director", film.director)
            intent.putExtra("genre", film.genre)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun setData(newFilmList: List<FilmEntity>) {
        filmList = newFilmList
        notifyDataSetChanged()
    }
}
