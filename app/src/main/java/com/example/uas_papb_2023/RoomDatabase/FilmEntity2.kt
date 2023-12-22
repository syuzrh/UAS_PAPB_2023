package com.example.uas_papb_2023.RoomDatabase

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class FilmEntity2(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var imageUrl: String = "",
    var rating: String = "",
    var storyline: String = "",
    var director: String = "",
    var genre: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(imageUrl)
        parcel.writeString(rating)
        parcel.writeString(storyline)
        parcel.writeString(director)
        parcel.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmEntity2> {
        override fun createFromParcel(parcel: Parcel): FilmEntity2 {
            return FilmEntity2(parcel)
        }

        override fun newArray(size: Int): Array<FilmEntity2?> {
            return arrayOfNulls(size)
        }
    }
}
