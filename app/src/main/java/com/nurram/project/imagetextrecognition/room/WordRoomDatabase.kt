package com.nurram.project.imagetextrecognition.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1)
abstract class WordRoomDatabase : RoomDatabase() {
    abstract fun mWordDao(): WordDao

    companion object {
        @Volatile
        private var db: WordRoomDatabase? = null

        fun getDatabase(application: Application): WordRoomDatabase? {
            if (db == null) {
                synchronized(WordRoomDatabase::class.java) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            application,
                            WordRoomDatabase::class.java, "pronunhelper_db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }

            return db
        }
    }
}