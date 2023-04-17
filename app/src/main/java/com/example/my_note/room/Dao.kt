package com.example.my_note.room

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Insert
    fun insertNote(noteData: NoteData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(noteData: NoteData)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateNote(noteData: NoteData)

    @Delete
    fun deleteNote(noteData: NoteData)

    @Query("DELETE FROM table_note")
    fun deleteAllData()

    @Query("SELECT * FROM table_note")
    fun getAllNote(): List<NoteData>

    @Query(
        "SELECT * FROM TABLE_NOTE " +
                "WHERE title || details LIKE '%' || :searching || '%' "
    )
    fun searchNote(searching: String): List<NoteData>


}