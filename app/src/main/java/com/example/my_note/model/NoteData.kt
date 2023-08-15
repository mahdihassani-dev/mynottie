package com.example.my_note.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_note")
data class NoteData(

    @PrimaryKey(autoGenerate = true)
    val id :Int? = null ,

    val title: String,
    val details: String,
    val date: String,
    val time: String,
)