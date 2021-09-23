package com.nurram.project.imagetextrecognition

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nurram.project.imagetextrecognition.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.capturedImageMenu.setOnClickListener {
            val intent = Intent(this@MenuActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.inputTextMenu.setOnClickListener {
            val intent = Intent(this@MenuActivity, TtsActivity::class.java)
            startActivity(intent)
        }

        binding.exitApp.setOnClickListener { finish() }
    }
}