package com.example.my_note.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteData::class], version = 4, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {

    abstract val noteDao: Dao

    companion object {

        private var dataBase: NoteDataBase? = null
        fun getDatabase(context: Context): NoteDataBase {

            if (dataBase == null) {

                dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDataBase::class.java,
                    "noteDataBase.db"
                )
                    .allowMainThreadQueries()
                    .build()

            }
            return dataBase!!
        }

    }

}