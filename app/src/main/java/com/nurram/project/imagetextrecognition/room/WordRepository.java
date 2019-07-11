package com.nurram.project.imagetextrecognition.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;
    private AsyncParams params;

    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.mWordDao();
        mAllWords = mWordDao.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        params = new AsyncParams("insert", word);
        new wordAsyncTask(mWordDao).execute(params);
    }

    public void delete(Word word) {
        params = new AsyncParams("delete", word);
        new wordAsyncTask(mWordDao).execute(params);
    }

    private static class wordAsyncTask extends AsyncTask<AsyncParams, Void, Void> {
        private WordDao mAsyncTaskDao;

        wordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AsyncParams... params) {
            if (params[0].key.equals("insert")) {
                mAsyncTaskDao.insert(params[0].word);
            } else {
                mAsyncTaskDao.deleteWord(params[0].word);
            }

            return null;
        }
    }

    private static class AsyncParams {
        String key;
        Word word;

        AsyncParams(String key, Word word) {
            this.key = key;
            this.word = word;
        }
    }
}
