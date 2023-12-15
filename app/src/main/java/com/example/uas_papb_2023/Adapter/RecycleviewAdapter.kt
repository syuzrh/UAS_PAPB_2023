package com.example.uas_papb_2023.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_papb_2023.RoomDatabase.FilmEntity
import com.example.uas_papb_2023.databinding.ItemFilmBinding
import com.squareup.picasso.Picasso

class RecycleviewAdapter(private val film: MutableList<FilmEntity>):
    RecyclerView.Adapter<RecycleviewAdapter.FilmViewHolder>() {

    inner class FilmViewHolder(val binding:ItemFilmBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = ItemFilmBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FilmViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return film.size
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.binding.apply {
            Picasso.get().load(film[position].imageUrl).into(imgFilm)
            txtTitle.text = film[position].title
            imgRating.text = film[position].rating
        }
    }

    fun setData(list: List<FilmEntity>){
        film.clear()
        film.addAll(list)
        notifyDataSetChanged()
    }
}
