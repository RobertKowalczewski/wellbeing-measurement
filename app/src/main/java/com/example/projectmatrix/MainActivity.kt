package com.example.projectmatrix

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room.databaseBuilder
import com.example.projectmatrix.storage.config.AppDatabase
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser
import com.example.projectmatrix.storage.service.user.WellbeingUserService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private val totalPoints: Int = 12
    private var wellbeingUser: WellbeingUser? = null
    private var db: AppDatabase? = null
    private lateinit var myButton: Button
    private lateinit var submitButton: Button
    private lateinit var myTextView: TextView
    private lateinit var editTextName: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var imageView: ImageView
    private lateinit var circleView: ImageView
    private lateinit var matrixConfirmButton: Button
    private lateinit var matrixText: ConstraintLayout
    private lateinit var locationImageView: ImageView
    private lateinit var areYouHereLayout: ConstraintLayout
    private lateinit var yesButton: Button
    private lateinit var areYouSureLayout: ConstraintLayout
    private lateinit var yesSureButton: Button
    private lateinit var noSureButton: Button
    private lateinit var finishLayout: ConstraintLayout
    private lateinit var finishButton: Button
    private lateinit var registeredUsersLayout: LinearLayout

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var stopCoordinates: List<Location>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = setupDatabase()

        currentLocation = android.location.Location("fused")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        updateLastKnownLocation()

        setContentView(R.layout.activity_main)

        // Инициализация элементов интерфейса
        myButton = findViewById(R.id.myButton)
        submitButton = findViewById(R.id.submitButton)
        myTextView = findViewById(R.id.myTextView)
        editTextName = findViewById(R.id.editTextName)
        editTextSurname = findViewById(R.id.editTextSurname)
        editTextPhone = findViewById(R.id.editTextPhone)
        imageView = findViewById(R.id.imageView)
        circleView = findViewById(R.id.circleView)
        matrixConfirmButton = findViewById(R.id.matrixConfirmButton)
        matrixText = findViewById(R.id.matrixText)
        locationImageView = findViewById(R.id.locationImageView)
        areYouHereLayout = findViewById(R.id.areYouHereLayout)
        yesButton = findViewById(R.id.yesButton)
        areYouSureLayout = findViewById(R.id.areYouSureLayout)
        yesSureButton = findViewById(R.id.yesSureButton)
        noSureButton = findViewById(R.id.noSureButton)
        finishLayout = findViewById(R.id.finishLayout)
        finishButton = findViewById(R.id.finishButton)
        registeredUsersLayout = findViewById(R.id.registeredUsersLayout)

        imageView.setImageResource(R.drawable.test)
        circleView.setImageResource(R.drawable.circle)
        setRegisteredUsers()

        // Инициализируем список локаций случайными значениями
        stopCoordinates = generateRandomLocations(totalPoints)

        myButton.setOnClickListener {
            myButton.visibility = View.GONE
            myTextView.visibility = View.GONE
            editTextName.visibility = View.VISIBLE
            editTextSurname.visibility = View.VISIBLE
            editTextPhone.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE
            registeredUsersLayout.visibility = View.VISIBLE
        }

        submitButton.setOnClickListener {
            val name = editTextName.text.toString()
            val surname = editTextSurname.text.toString()
            val phone = editTextPhone.text.toString()

            if (validateName(name) && validateSurname(surname) && validatePhone(phone)) {
                goToMatrix(name, surname, phone)
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

    private fun goToMatrix(name: String, surname: String, phone: String) {
        setUser(name, surname, phone)

        hideKeyboard()
        editTextName.visibility = View.GONE
        editTextSurname.visibility = View.GONE
        editTextPhone.visibility = View.GONE
        submitButton.visibility = View.GONE
        registeredUsersLayout.visibility = View.GONE

        // Переходим на экран с матрицей
        matrixText.visibility = View.VISIBLE
        circleView.visibility = View.INVISIBLE
        matrixConfirmButton.visibility = View.VISIBLE

        setupMatrix(imageView, circleView)
        matrixConfirmButton.setOnClickListener {
            if (lastClickPosition.first == 0.0f && lastClickPosition.second == 0.0f) {
                Toast.makeText(this, "Please select a point on the matrix.", Toast.LENGTH_SHORT)
                    .show()
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
    }

    private fun setupDatabase(): AppDatabase {
        return databaseBuilder(
            this,
            AppDatabase::class.java,
            "storage"
        )
            .build();
    }

    private fun setUser(name: String, surname: String, phone: String) {
        GlobalScope.launch {
            val userService = WellbeingUserService(db?.wellbeingUserRepository())
            wellbeingUser = userService.findOrCreateUser(name, surname, phone)

            runOnUiThread {
                Log.i("main", "USER: " + wellbeingUser)
                Log.i("main", "Data: " + wellbeingUser?.name + " " + wellbeingUser?.surname + " " + wellbeingUser?.creationTimestamp + " " + wellbeingUser?.modificationTimestamp)
            }
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

        val resId = resources.getIdentifier(location.imageName, "drawable", packageName)
        if (resId != 0) {
            locationImageView.setImageResource(resId)
        } else {
            locationImageView.setImageResource(R.drawable.tree)
        }
    }

    private fun generateRandomLocations(count: Int): List<Location> {
        val locations = mutableListOf<Location>()
        for (i in 1..count) {
            val lat = Random.nextDouble(52.0, 53.0)
            val lon = Random.nextDouble(16.0, 17.0)
            val imageName = "loc"
            locations.add(Location(lat, lon, imageName))
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

    private fun setRegisteredUsers() {
        GlobalScope.launch {
            val userService = WellbeingUserService(db?.wellbeingUserRepository())
            val wellBeingUsers = userService.findAll()

            runOnUiThread {
                setRegisteredUsersLayout(wellBeingUsers)
            }
        }
    }

    private fun setRegisteredUsersLayout(wellbeingUsers: List<WellbeingUser>) {
        registeredUsersLayout.removeAllViews()

        wellbeingUsers.forEach { user: WellbeingUser ->
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL

            val border = GradientDrawable()
            border.setColor(Color.WHITE)
            border.setStroke(2, Color.BLACK)
            border.cornerRadius = 8f
            row.background = border

            val name = TextView(this)
            name.setMinWidth(300)
            name.text = user.name
            name.setPadding(10, 40, 0, 40)
            row.addView(name)

            val surname = TextView(this)
            surname.setMinWidth(300)
            surname.text = user.surname
            row.addView(surname)

            val phone = TextView(this)
            phone.setMinWidth(300)
            phone.text = user.phoneNumber
            row.addView(phone)

            row.setOnClickListener {
                goToMatrix(user.name, user.surname, user.phoneNumber)
            }

            registeredUsersLayout.addView(row)
        }
    }
}
