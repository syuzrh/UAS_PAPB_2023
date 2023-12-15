package com.example.uas_papb_2023.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: FilmEntity)

    @Query("SELECT * FROM films")
    fun getAllFilmsList(): List<FilmEntity>
}
