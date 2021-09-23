package com.nurram.project.imagetextrecognition

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nurram.project.imagetextrecognition.databinding.ActivityMainBinding
import com.wonderkiln.camerakit.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageBitmap: Bitmap
    private lateinit var mediaPlayer: MediaPlayer

    private var isCapturedShow = false
    private var mBlockList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mBlockList = ArrayList()

        binding.cameraLayout.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(cameraKitEvent: CameraKitEvent) {}
            override fun onError(cameraKitError: CameraKitError) {}
            override fun onImage(cameraKitImage: CameraKitImage) {
                imageBitmap = cameraKitImage.bitmap

                binding.capturedLayout.capturedImage.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
                binding.capturedLayout.image.setImageBitmap(imageBitmap)
                isCapturedShow = true
            }

            override fun onVideo(cameraKitVideo: CameraKitVideo) {}
        })

        binding.buttonCamera.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.hold_until_finish),
                Toast.LENGTH_SHORT)
                .show()

            binding.buttonCamera.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
            binding.cameraLayout.captureImage()

            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.camera)
            mediaPlayer.start()
        }

        binding.capturedLayout.processFab.setOnClickListener {
            binding.capturedLayout.processFab.isClickable = false
            processTextRecog(imageBitmap)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.cameraLayout.start()
        binding.buttonCamera.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.capturedLayout.processFab.isClickable = true
    }

    override fun onStop() {
        super.onStop()
        binding.cameraLayout.stop()
        mediaPlayer.stop()
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