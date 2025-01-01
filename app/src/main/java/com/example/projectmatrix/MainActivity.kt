package com.example.projectmatrix

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.random.Random

data class Location(
    var latitude: Double,
    var longitude: Double,
    var imageName: String
)

class MainActivity : ComponentActivity() {

    private lateinit var currentLocation: android.location.Location
    private val clickCoordinates = mutableListOf<Pair<Float, Float>>()
    private var lastClickPosition: Pair<Float, Float> = Pair(0.0f, 0.0f)
    private var currentPoint: Int = 0
    private var totalPoints: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var stopCoordinates: List<Location>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLocation = android.location.Location("fused")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        updateLastKnownLocation()

        setContentView(R.layout.activity_main)

        // Инициализация элементов интерфейса
        val myButton: Button = findViewById(R.id.myButton)
        val submitButton: Button = findViewById(R.id.submitButton)
        val myTextView: TextView = findViewById(R.id.myTextView)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextSurname: EditText = findViewById(R.id.editTextSurname)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)
        val imageView: ImageView = findViewById(R.id.imageView)
        val circleView: ImageView = findViewById(R.id.circleView)
        val matrixConfirmButton: Button = findViewById(R.id.matrixConfirmButton)
        val matrixText: ConstraintLayout = findViewById(R.id.matrixText)
        val locationImageView: ImageView = findViewById(R.id.locationImageView)
        val areYouHereLayout: ConstraintLayout = findViewById(R.id.areYouHereLayout)
        val yesButton: Button = findViewById(R.id.yesButton)
        val areYouSureLayout: ConstraintLayout = findViewById(R.id.areYouSureLayout)
        val yesSureButton: Button = findViewById(R.id.yesSureButton)
        val noSureButton: Button = findViewById(R.id.noSureButton)
        val finishLayout: ConstraintLayout = findViewById(R.id.finishLayout)
        val finishButton: Button = findViewById(R.id.finishButton)

        imageView.setImageResource(R.drawable.test)
        circleView.setImageResource(R.drawable.circle)

        // Инициализируем список локаций случайными значениями
        stopCoordinates = readScenarioFile("scenario1.scenario")
        if (stopCoordinates.isEmpty()) {
            Toast.makeText(this, "No locations found in scenario file", Toast.LENGTH_LONG).show()
            finish() // Закрываем приложение, если файл пуст
        }
        totalPoints = stopCoordinates.size
        myButton.setOnClickListener {
            myButton.visibility = View.GONE
            myTextView.visibility = View.GONE
            editTextName.visibility = View.VISIBLE
            editTextSurname.visibility = View.VISIBLE
            editTextPhone.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE
        }

        submitButton.setOnClickListener {
            val name = editTextName.text.toString()
            val surname = editTextSurname.text.toString()
            val phone = editTextPhone.text.toString()

            if (validateName(name) && validateSurname(surname) && validatePhone(phone)) {
                hideKeyboard()
                editTextName.visibility = View.GONE
                editTextSurname.visibility = View.GONE
                editTextPhone.visibility = View.GONE
                submitButton.visibility = View.GONE

                // Переходим на экран с матрицей
                matrixText.visibility = View.VISIBLE
                circleView.visibility = View.INVISIBLE
                matrixConfirmButton.visibility = View.VISIBLE

                setupMatrix(imageView, circleView)
                matrixConfirmButton.setOnClickListener {
                    if (lastClickPosition.first == 0.0f && lastClickPosition.second == 0.0f) {
                        Toast.makeText(this, "Please select a point on the matrix.", Toast.LENGTH_SHORT).show()
                    } else {
                        clickCoordinates.add(lastClickPosition)
                        lastClickPosition = Pair(0.0f, 0.0f)
                        circleView.visibility = View.INVISIBLE

                        if (currentPoint < totalPoints) {
                            showAreYouHereScreen(
                                stopCoordinates[currentPoint],
                                locationImageView,
                                areYouHereLayout,
                                yesButton
                            )
                        } else {
                            matrixText.visibility = View.GONE
                            finishLayout.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter correct data.", Toast.LENGTH_SHORT).show()
            }
        }

        yesButton.setOnClickListener {
            updateLastKnownLocation()
            val targetLocation = android.location.Location("target")
            targetLocation.latitude = stopCoordinates[currentPoint].latitude
            targetLocation.longitude = stopCoordinates[currentPoint].longitude
            val distance = currentLocation.distanceTo(targetLocation)

            if (distance > 50) {
                areYouHereLayout.visibility = View.GONE
                areYouSureLayout.visibility = View.VISIBLE
            } else {
                areYouHereLayout.visibility = View.GONE
                matrixText.visibility = View.VISIBLE
                matrixConfirmButton.visibility = View.VISIBLE
                circleView.visibility = View.INVISIBLE
                currentPoint++
            }
        }

        yesSureButton.setOnClickListener {
            areYouSureLayout.visibility = View.GONE
            matrixText.visibility = View.VISIBLE
            matrixConfirmButton.visibility = View.VISIBLE
            circleView.visibility = View.INVISIBLE
            currentPoint++
        }

        noSureButton.setOnClickListener {
            areYouSureLayout.visibility = View.GONE
            areYouHereLayout.visibility = View.VISIBLE
        }

        finishButton.setOnClickListener {
            Toast.makeText(this, "Thank you for your participation!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupMatrix(imageView: ImageView, circleView: ImageView) {
        imageView.visibility = View.VISIBLE
        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    circleView.x = imageView.x + x - circleView.width / 2
                    circleView.y = imageView.y + y - circleView.height / 2
                    circleView.visibility = View.VISIBLE

                    val imageWidth = imageView.width
                    val imageHeight = imageView.height

                    val normX = x / imageWidth
                    val normY = y / imageHeight
                    lastClickPosition = Pair(normX, normY)
                    true
                }
                else -> false
            }
        }
    }

    private fun showAreYouHereScreen(
        location: Location,
        locationImageView: ImageView,
        areYouHereLayout: ConstraintLayout,
        yesButton: Button
    ) {
        val matrixText: ConstraintLayout = findViewById(R.id.matrixText)
        val circleView: ImageView = findViewById(R.id.circleView)

        matrixText.visibility = View.GONE
        circleView.visibility = View.GONE
        areYouHereLayout.visibility = View.VISIBLE

        // Получаем идентификатор ресурса по имени
        val resId = resources.getIdentifier(location.imageName.substringBeforeLast("."), "drawable", packageName)
        if (resId != 0) {
            locationImageView.setImageResource(resId)
        } else {
            locationImageView.setImageResource(R.drawable.default_image) // Установите изображение по умолчанию
        }
    }



    private fun readScenarioFile(fileName: String): List<Location> {
        val locations = mutableListOf<Location>()
        try {
            val inputStream = assets.open(fileName)
            inputStream.bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    val parts = line.split(", ")
                    if (parts.size == 3) {
                        val latitude = parts[0].toDoubleOrNull()
                        val longitude = parts[1].toDoubleOrNull()
                        val imageName = parts[2]
                        if (latitude != null && longitude != null) {
                            locations.add(Location(latitude, longitude, imageName))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to read scenario file", Toast.LENGTH_SHORT).show()
        }
        return locations
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun validateName(name: String): Boolean {
        return name.matches(Regex("[a-zA-Zа-яА-Я]{3,}"))
    }

    private fun validateSurname(surname: String): Boolean {
        return surname.matches(Regex("[a-zA-Zа-яА-Я]{3,}"))
    }

    private fun validatePhone(phone: String): Boolean {
        return phone.matches(Regex("^\\+?\\d{10,}$"))
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            updateLastKnownLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastKnownLocation()
            }
        }
    }

    private fun updateLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
                    if (location != null) {
                        currentLocation = location
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}