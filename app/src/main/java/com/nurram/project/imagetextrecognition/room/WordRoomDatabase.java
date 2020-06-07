package com.nurram.project.imagetextrecognition.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Word.class}, version = 1)
abstract class WordRoomDatabase extends RoomDatabase {
    abstract WordDao mWordDao();

    private static volatile WordRoomDatabase sDatabase;

    static WordRoomDatabase getDatabase(Context context) {
        if (sDatabase == null) {
            synchronized (WordRoomDatabase.class) {
                if (sDatabase == null) {
                    sDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
                            .build();
                }
            }
        }

        return sDatabase;
    }
}

