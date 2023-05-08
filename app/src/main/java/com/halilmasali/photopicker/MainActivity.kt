package com.halilmasali.photopicker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.halilmasali.photopicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var filter: ActivityResultContracts.PickVisualMedia.VisualMediaType
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize first filter
        filter = ActivityResultContracts.PickVisualMedia.ImageOnly
        // Initialize media picker launcher
        mediaPickerResult()
        // Initialize pick media button listener
        btnPickMedia()
        // Initialize radioButton listener
        radioButtonListeners()

    }

    private fun btnPickMedia() {
        binding.btnPickMedia.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(filter))
            } else
                Toast.makeText(
                    this, "This is library that works on Android 13 and above.",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun radioButtonListeners() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioButtonImage.id -> {
                    filter = ActivityResultContracts.PickVisualMedia.ImageOnly
                }

                binding.radioButtonVideo.id -> {
                    filter = ActivityResultContracts.PickVisualMedia.VideoOnly
                }

                binding.radioButtonImageAndVideo.id -> {
                    filter = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                }
            }
        }
    }

    private fun mediaPickerResult() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val mediaType = contentResolver.getType(uri)
                if (mediaType?.startsWith("image/") == true) {
                    // If the media type is image, it is displayed with imageView.
                    binding.imageView.setImageURI(uri)
                    binding.videoView.visibility = View.INVISIBLE
                    binding.imageView.visibility = View.VISIBLE
                } else if (mediaType?.startsWith("video/") == true) {
                    // If the media type is video, it is displayed with videoView.
                    binding.videoView.setVideoURI(uri)
                    binding.videoView.start()
                    binding.imageView.visibility = View.INVISIBLE
                    binding.videoView.visibility = View.VISIBLE
                }
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
                Toast.makeText(this, "No media selected", Toast.LENGTH_LONG).show()
            }
        }
    }

}