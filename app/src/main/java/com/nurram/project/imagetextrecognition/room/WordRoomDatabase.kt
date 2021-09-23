package com.nurram.project.imagetextrecognition.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1)
abstract class WordRoomDatabase : RoomDatabase() {
    abstract fun mWordDao(): WordDao?

    companion object {
        @Volatile
        private var sDatabase: WordRoomDatabase? = null
        fun getDatabase(context: Context): WordRoomDatabase? {
            if (sDatabase == null) {
                synchronized(WordRoomDatabase::class.java) {
                    if (sDatabase == null) {
                        sDatabase = Room.databaseBuilder(
                            context.applicationContext,
                            WordRoomDatabase::class.java, "word_database"
                        )
                            .build()
                    }
                }
            }
            return sDatabase
        }
    }
}