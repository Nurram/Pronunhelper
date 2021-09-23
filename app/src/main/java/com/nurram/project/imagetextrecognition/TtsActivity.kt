package com.nurram.project.imagetextrecognition

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.nurram.project.imagetextrecognition.databinding.ActivityTtsBinding
import com.nurram.project.imagetextrecognition.room.Word
import com.nurram.project.imagetextrecognition.room.WordRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class TtsActivity : AppCompatActivity(), OnInitListener {
    private lateinit var binding: ActivityTtsBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var repository: WordRepository

    private var mWords = listOf<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTtsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.ttsToolbar)
        supportActionBar?.title = "Text-To-Speech"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = WordRepository(application)
        repository.getAll()?.observe(this, { words: List<Word>? ->
            if (words != null) {
                mWords = words
            }
        })

        textToSpeech = TextToSpeech(this, this)
        binding.androidButton.setOnClickListener {
            val text = binding.ttsKata.text.toString().lowercase()
            var fromDb = false
            if (binding.switch1.isChecked) {
                for (word in mWords) {
                    if (word.word == text) {
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

        binding.ttsDaftarPeng.setOnClickListener {
            val intent = Intent(this@TtsActivity, SavedListActivity::class.java)
            startActivity(intent)
        }

        binding.pitchSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val convertedProgress = convertProgress(progress)
                textToSpeech.setPitch(convertedProgress)
                binding.pitchValue.text = convertedProgress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.speedSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val convertedProgress = convertProgress(progress)
                textToSpeech.setSpeechRate(convertedProgress)
                binding.speedValue.text = convertProgress(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setSpeechRate(0.8f)
            textToSpeech.language = Locale.US
        } else {
            Toast.makeText(
                this,
                getString(R.string.initiation_failed),
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun speak(text: String) {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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

    private fun convertProgress(progressInt: Int): Float {
        return .5f * progressInt
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
}