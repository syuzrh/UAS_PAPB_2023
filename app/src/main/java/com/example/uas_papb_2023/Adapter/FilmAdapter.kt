package com.example.uas_papb_2023.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.Activity.DetailsActivity
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.RoomDatabase.FilmEntity2
import com.squareup.picasso.Picasso

class FilmAdapter(
    private var context: Context,
    private var filmList: List<FilmEntity2>,
    private var userRole: String,
    private val onLongItemClickListener: (FilmEntity2) -> Unit,
    private val onEditClickListener: (FilmEntity2) -> Unit
) : RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

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
        Picasso.get().load(film.imageUrl).into(holder.imgFilm)

        holder.imgFilm.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("title", film.title)
            Picasso.get().load(film.imageUrl).into(holder.imgFilm)
            intent.putExtra("imageUrl", film.imageUrl)
            intent.putExtra("rating", film.rating)
            intent.putExtra("storyline", film.storyline)
            intent.putExtra("director", film.director)
            intent.putExtra("genre", film.genre)
            Log.d("url film", film.imageUrl)

            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            onEditClickListener(film)
        }

        holder.itemView.setOnLongClickListener {
            onLongItemClickListener(film)
            true
        }
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun setData(context: Context, filmList: List<FilmEntity2>, userRole: String) {
        this.context = context
        this.filmList = filmList
        this.userRole = userRole
        notifyDataSetChanged()
    }
}
