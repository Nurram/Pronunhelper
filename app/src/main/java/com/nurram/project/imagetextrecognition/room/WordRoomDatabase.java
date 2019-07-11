package com.nurram.project.imagetextrecognition.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Word.class}, version = 1)
public abstract class WordRoomDatabase extends RoomDatabase {
    abstract WordDao mWordDao();

    private static volatile WordRoomDatabase sDatabase;

    public static WordRoomDatabase getDatabase(Context context) {
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

