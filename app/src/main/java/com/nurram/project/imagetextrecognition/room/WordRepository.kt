package com.nurram.project.imagetextrecognition.room

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class WordRepository(application: Application?) {
    private val mWordDao: WordDao

    fun getAll() =  mWordDao.getAllWords()
    fun insert(word: Word) = GlobalScope.launch { mWordDao.insert(word) }
    fun delete(word: Word) = GlobalScope.launch { mWordDao.deleteWord(word) }

    init {
        val db = WordRoomDatabase.getDatabase(application)
        mWordDao = db.mWordDao()
    }
}