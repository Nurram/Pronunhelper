package com.nurram.project.imagetextrecognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nurram.project.imagetextrecognition.databinding.ActivityMainBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageBitmap: Bitmap

    private var mediaPlayer: MediaPlayer? = null
    private var isCapturedShow = false
    private var mBlockList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCamera.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.hold_until_finish),
                Toast.LENGTH_SHORT
            )
                .show()

            binding.buttonCamera.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
            binding.cameraLayout.captureImage { _, capturedImage ->
                val savedPhoto = File(cacheDir, Calendar.getInstance().timeInMillis.toString())
                Log.d("TAG", "camputer")
                try {
                    val stream = FileOutputStream(savedPhoto.path)
                    stream.write(capturedImage)
                    stream.close()

                    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(capturedImage))
                    val out = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 75, out)
                    val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(capturedImage))

                    imageBitmap = decoded
                    binding.capturedLayout.capturedImage.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.capturedLayout.image.setImageBitmap(imageBitmap)
                    isCapturedShow = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something happened", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.camera)
            mediaPlayer!!.start()
        }

        binding.capturedLayout.processFab.setOnClickListener {
            binding.capturedLayout.processFab.isClickable = false
            processTextRecog(imageBitmap)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.cameraLayout.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.cameraLayout.onResume()
        binding.buttonCamera.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.capturedLayout.processFab.isClickable = true
    }

    override fun onPause() {
        super.onPause()
        binding.cameraLayout.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.cameraLayout.onStop()
        mediaPlayer?.stop()
    }

    override fun onBackPressed() {
        if (isCapturedShow) {
            binding.capturedLayout.capturedImage.visibility = View.GONE
            binding.progress.visibility = View.GONE
            binding.buttonCamera.visibility = View.VISIBLE
            isCapturedShow = false
        } else {
            super.onBackPressed()
        }
    }

    private fun processTextRecog(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        mBlockList.clear()

        val mRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        mRecognizer.process(inputImage)
            .addOnSuccessListener { result: Text ->
                val intent = ResultActivity.getIntent(this@MainActivity)
                binding.capturedLayout.capturedImage.visibility = View.GONE

                for (block in result.textBlocks) {
                    for (line in block.lines) {
                        val lineText = line.text
                        mBlockList.add(lineText)
                    }
                }

                intent.putStringArrayListExtra("block", mBlockList)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Something happened",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}