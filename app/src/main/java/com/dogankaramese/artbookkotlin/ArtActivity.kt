package com.dogankaramese.artbookkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dogankaramese.artbookkotlin.databinding.ActivityArtBinding
import com.google.android.material.snackbar.Snackbar

class ArtActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    private lateinit var binding: ActivityArtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()


    }

    fun saveButtonClicked(view: View) {

            val artName=binding.artNameText.text.toString()
            val artistName=binding.artistNameText.text.toString()
        val year=binding.yearText.text.toString()
        if (selectedBitmap!=null){
            val smallBitmap=makeSmallerBitmap(selectedBitmap!!,300)

        }

    }

    private fun makeSmallerBitmap(image: Bitmap,maximumSize:Int): Bitmap{
        var width=image.width
        var height=image.height
        val bitmapRatio:Double=width.toDouble()/height.toDouble()

        if (bitmapRatio>1){
            //landscape
            width=maximumSize
            val scaleHeight=width/bitmapRatio
            height=scaleHeight.toInt()

        }else {
            //portrait
            height=maximumSize
            val scaleWidth=height*bitmapRatio
            width=scaleWidth.toInt()

        }
        return Bitmap.createScaledBitmap(image,width,height,true)

    }

    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Android 33 + -> READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)


                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }


        }
        else {
            //Android 32 - -> READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }



    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                       // binding.imageView.setImageURI(imageData)

                        if (imageData != null) {

                            try {
                                if (Build.VERSION.SDK_INT>=28) {
                                    val source = ImageDecoder.createSource(
                                        this@ArtActivity.contentResolver,
                                        imageData

                                    )
                                    selectedBitmap=ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }else
                                {
                                    selectedBitmap=MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    }
                }
            }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{

                Toast.makeText(this@ArtActivity,"Permission needed!",Toast.LENGTH_LONG).show()

            }


        }

    }


}