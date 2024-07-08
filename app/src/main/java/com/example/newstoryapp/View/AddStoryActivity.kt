package com.example.newstoryapp.View

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.newstoryapp.Api.APISettings
import com.example.newstoryapp.Api.APISettings.Companion.getApiService
import com.example.newstoryapp.R
import com.example.newstoryapp.databinding.ActivityAddStoryBinding
import com.example.newstoryapp.response.AddStoryrespon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var userPreferences: SharedPreferences
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    companion object {
        private val REQUIRED_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_back)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.title = "Upload"


        binding.btnCamera.setOnClickListener { startPhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.sendStory.setOnClickListener { upload() }


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                else -> {
                    location = Location(LocationManager.GPS_PROVIDER) // DEFAULT VALUE KETIKA TIDAK ADA AKSES YANG DIBERIKAN (LAT 0, LONG 0)
                }
            }
        }


    // Fungsi untuk memeriksa apakah izin tertentu telah diberikan
    private fun checkPermission(permissions: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissions
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Fungsi untuk mendapatkan lokasi terakhir perangkat
    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private var getFile: File? = null


    private fun startPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application.toString()).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.newstoryapp.View",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherCamera.launch(intent)
        }
    }


    private lateinit var currentPhotoPath: String


    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            val result = memutarBitmap(
                BitmapFactory.decodeFile(myFile.path),
                true
            )

            getFile = playFileImage(myFile)
            binding.ImageViewResult.setImageBitmap(result)
        }
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriKoFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ImageViewResult.setImageURI(selectedImg)
        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose))
        launcherGallery.launch(chooser)
    }


  private fun upload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            userPreferences = getSharedPreferences(EnterActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val description = binding.descText.text.toString().toRequestBody()
            val lat = location.latitude.toFloat()
            val lon = location.longitude.toFloat()
            val token = userPreferences.getString(EnterActivity.TOKEN, "").toString()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val service= APISettings.getApiService().uploadImage("Bearer $token", imageMultipart, description, lat, lon)
            service.enqueue(object : Callback<AddStoryrespon> {
                override fun onResponse(
                    call: Call<AddStoryrespon>,
                    response: Response<AddStoryrespon>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            showLoading(true)
                            Toast.makeText(this@AddStoryActivity, resources.getString(R.string.validUpl), Toast.LENGTH_SHORT).show()
                        }
                        val intent = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@AddStoryActivity, resources.getString(R.string.inputDesc), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddStoryrespon>, t: Throwable) {
                    Toast.makeText(this@AddStoryActivity, resources.getString(R.string.failUpl), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@AddStoryActivity, resources.getString(R.string.inputImg), Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menampilkan atau menyembunyikan progress bar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
