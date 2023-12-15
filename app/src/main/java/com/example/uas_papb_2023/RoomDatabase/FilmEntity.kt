package com.example.uas_papb_2023.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String = "",
    var imageUrl: String = "",
    var rating: String = "",
    var storyline: String = "",
    var director: String = "",
    var genre: String = ""
) {
    constructor() : this(0, "", "", "", "", "", "")
}

