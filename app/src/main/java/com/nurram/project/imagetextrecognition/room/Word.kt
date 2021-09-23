package com.nurram.project.imagetextrecognition.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
class Word(
    @PrimaryKey
    val word: String
)