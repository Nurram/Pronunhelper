package com.nurram.project.imagetextrecognition

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nurram.project.imagetextrecognition.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var mBlockList = arrayListOf<String>()
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, INTENT_CAMERA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_CAMERA && resultCode == RESULT_OK) {

            lifecycleScope.launch {
                var bitmap: Bitmap

                binding.mainProgress.visibility = View.VISIBLE
                withContext(lifecycleScope.coroutineContext + Dispatchers.IO) {
                    bitmap = Picasso.get().load("file:$currentPhotoPath").get()
                }
                binding.mainProgress.visibility = View.GONE
                binding.capturedLayout.capturedImage.visibility = View.VISIBLE

                binding.capturedLayout.image.setImageBitmap(bitmap)
                binding.capturedLayout.processFab.setOnClickListener {
                    processTextRecog(bitmap)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.capturedLayout.processFab.isClickable = true
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply { currentPhotoPath = absolutePath }
    }

    private fun processTextRecog(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        mBlockList.clear()

        val mRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        mRecognizer.process(inputImage)
            .addOnSuccessListener { result: Text ->

                for (block in result.textBlocks) {
                    for (line in block.lines) {
                        val lineText = line.text
                        mBlockList.add(lineText)
                    }
                }

                val intent = ResultActivity.getIntent(this@MainActivity)
                intent.putStringArrayListExtra("block", mBlockList)
                startActivity(intent)

                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Something happened",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        const val INTENT_CAMERA = 101
    }
}