package com.nurram.project.imagetextrecognition

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.nurram.project.imagetextrecognition.ResultActivity
import com.nurram.project.imagetextrecognition.databinding.ActivityResultBinding
import com.nurram.project.imagetextrecognition.room.Word
import com.nurram.project.imagetextrecognition.room.WordRepository
import com.nurram.project.imagetextrecognition.util.RecyclerAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class ResultActivity : AppCompatActivity(), OnInitListener {
    private lateinit var binding: ActivityResultBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var repository: WordRepository

    private var mBlocks = arrayListOf<String>()
    private var mWords = listOf<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.resultToolbar)
        supportActionBar?.title = getString(R.string.result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBlocks = if (savedInstanceState != null) {
            savedInstanceState.getStringArrayList("blockBack") as ArrayList<String>
        } else {
            intent.getStringArrayListExtra("block") as ArrayList<String>
        }

        repository = WordRepository(application)
        repository.getAll()?.observe(this) { words -> mWords = words }

        binding.resultCekSaved.setOnClickListener {
            val intent = Intent(this, SavedListActivity::class.java)
            startActivity(intent)
        }

        val adapter = RecyclerAdapter(mBlocks) { words ->
            val text = words.lowercase()
            var fromDb = false
            if (binding.switch1.isChecked) {
                for (element in mWords) {
                    if (element.word == text) {
                        fromDb = true
                        speak(text.lowercase())
                        break
                    }
                }
                if (!fromDb) {
                    checkAndSpeak(text)
                }
            } else {
                speak(text.lowercase())
            }
        }

        binding.textRecycler.layoutManager = LinearLayoutManager(this)
        binding.textRecycler.setHasFixedSize(true)
        binding.textRecycler.adapter = adapter
        binding.textRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        textToSpeech = TextToSpeech(this, this)
    }

    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("blockBack", mBlocks)
    }

    override fun onInit(status: Int) {
        initTts(status)
    }

    private fun initTts(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
            textToSpeech.setSpeechRate(0.8f)
        } else {
            Toast.makeText(
                this,
                getString(R.string.initiation_failed),
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkAndSpeak(text: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode: String ->
                if (languageCode == "en") {
                    speak(text)
                } else {
                    showDialog(text)
                }
            }
            .addOnFailureListener { showDialog("Something happened") }
    }

    private fun speak(text: String) {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun showDialog(text: String) {
        val builder = AlertDialog.Builder(
            this,
            android.R.style.Theme_Material_Light_Dialog_Alert)

        builder.setTitle(getString(R.string.word_not_english))
            .setMessage("${getString(R.string.add)} '$text' ${getString(R.string.to_whitelist)}?")
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                val word = Word(text)
                repository.insert(word)
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .show()
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, ResultActivity::class.java)
        }
    }
}