package com.example.uas_papb_2023.RoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: FilmEntity2)

    @Query("SELECT * FROM films")
    fun getAllFilmsList(): List<FilmEntity2>

    @Delete
    fun delete(filmEntity2: FilmEntity2)

    @Update
    fun update(films: FilmEntity2)

}
