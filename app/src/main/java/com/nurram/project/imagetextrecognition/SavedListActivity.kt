package com.nurram.project.imagetextrecognition

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nurram.project.imagetextrecognition.databinding.ActivitySavedListBinding
import com.nurram.project.imagetextrecognition.room.Word
import com.nurram.project.imagetextrecognition.room.WordRepository
import com.nurram.project.imagetextrecognition.util.ExcludeAdapter
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class SavedListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedListBinding
    private lateinit var repository: WordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.pengecualianToolbar)
        supportActionBar?.title = getString(R.string.whitelist)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.listPengecualian.layoutManager = LinearLayoutManager(this)
        binding.listPengecualian.addItemDecoration(
            DividerItemDecoration(
            this,
            DividerItemDecoration.VERTICAL)
        )

        val adapter = ExcludeAdapter {
            repository.delete(it)
            Toast.makeText(
                this,
                "${it.word} ${getString(R.string.deleted)}",
                Toast.LENGTH_SHORT)
                .show()
        }
        repository = WordRepository(application)
        repository.getAll().observe(this) { words ->
            adapter.addData(words as ArrayList<Word>)
            binding.listPengecualian.adapter = adapter
        }
    }
}