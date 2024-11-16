package com.example.projectmatrix

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Color.RED
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.ComponentActivity
import android.util.Log
import com.example.projectmatrix.connection.websocket.WebsocketConnectionService
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.BufferedReader
import kotlin.properties.Delegates

data class Location(
    var latitude: Double,
    var longitude: Double,
    var imageName: String
)

class MainActivity : ComponentActivity() {

    //private var currentLatitude: Double = 0.0
    //private var currentLongitude: Double = 0.0

    private lateinit var currentLocation:android.location.Location

    // array to save coordinates
    private val clickCoordinates = mutableListOf<Pair<Float, Float>>()

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLocation = android.location.Location("fused")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        //run this every 2 seconds
        updateLastKnownLocation()
        // use functions of android.location.Location to measure distance between locations

        setContentView(R.layout.activity_main)
        var i = 1
        val myButton: Button = findViewById(R.id.myButton)
        val submitButton: Button = findViewById(R.id.submitButton)
        val myTextView: TextView = findViewById(R.id.myTextView)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextSurname: EditText = findViewById(R.id.editTextSurname)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)
        val imageView: ImageView = findViewById(R.id.imageView)
        val ipAddress: TextView = findViewById(R.id.ipAddress)

        startWatchConnection(ipAddress)

        val stopCoordinates = readScenario("scenario1.scenario")

        myButton.setOnClickListener {
            //print current location
            println("${currentLocation.latitude} ${currentLocation.longitude}")
            myButton.visibility = View.GONE
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
                myTextView.visibility = View.GONE
                editTextName.visibility = View.GONE
                editTextSurname.visibility = View.GONE
                editTextPhone.visibility = View.GONE
                submitButton.visibility = View.GONE

                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.test)
                //first cordinate
                showConfirmationPopup(
                    this,
                    "${stopCoordinates[0].latitude} ${stopCoordinates[0].longitude}",
                    R.drawable.rofl
                ) {
                    val matrix = findViewById<GridLayout>(R.id.matrix)
                    setupMatrixClicks(matrix)
                }

                imageView.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val drawable = imageView.drawable
                            val imageWidth = drawable.intrinsicWidth
                            val imageHeight = drawable.intrinsicHeight

                            val normX = event.x / imageWidth
                            val normY = event.y / imageHeight





                            clickCoordinates.add(Pair(normX, normY))

                            val coordinate = stopCoordinates[i]
                            showConfirmationPopup(this, "$coordinate", R.drawable.rofl) {
                                val matrix = findViewById<GridLayout>(R.id.matrix)
                                setupMatrixClicks(matrix)
                            }
                            i++

                            Toast.makeText(this, "$normX, $normY", Toast.LENGTH_SHORT).show()
                            true
                        }

                        else -> false
                    }
                }

                val landmark = "tree" // near building
                val imageRes = R.drawable.tree


            } else {
                Toast.makeText(this, "Please enter correct data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    private fun startWatchConnection(ipAddress: TextView) {
        val address = getLocalIpAddress()
        address?.let {
            try {
                val websocketService = WebsocketConnectionService(address, 5000, this)
                websocketService.start()
                onSuccessfulConnectionStart(ipAddress, address)
            } catch (ex: Exception) {
                onFailedConnectionStart(ipAddress, "Failed to start websocket server")
            }
        } ?: run {
            onFailedConnectionStart(ipAddress,"Failed to find IP address")
        }
    }

    private fun onSuccessfulConnectionStart(ipAddress: TextView, message: String) {
        ipAddress.setTextColor(BLACK)
        ipAddress.text = message
        Log.d("ActivityMain", "reached")
    }

    private fun onFailedConnectionStart(ipAddress: TextView, message: String) {
        ipAddress.setTextColor(RED)
        ipAddress.text = message
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val enumIpAddresses = networkInterface.inetAddresses
                while (enumIpAddresses.hasMoreElements()) {
                    val inetAddress = enumIpAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        Log.d("ActivityMain", "IPv4 Address: ${inetAddress.hostAddress}")
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    fun showConfirmationPopup(context: Context, landmark: String, imageRes: Int, onConfirm: () -> Unit) {

        AlertDialog.Builder(context)
            .setTitle("Are you close to $landmark?")
            .setIcon(imageRes)
            .setPositiveButton("Confirm") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()


    }

    // click handler for matrix
    fun setupMatrixClicks(matrix: GridLayout) {
        for (i in 0 until matrix.childCount) {
            val cell = matrix.getChildAt(i) as View
            cell.setOnClickListener {
                val row = i / matrix.columnCount
                val col = i % matrix.columnCount

                // Save coordinates to array
                clickCoordinates.add(Pair(row.toFloat(), col.toFloat()))
                Toast.makeText(this, "Clicked at row: $row, col: $col", Toast.LENGTH_SHORT).show()

                Log.d("MatrixClicks", "Click coordinates: $clickCoordinates")
            }
        }
    }

    private fun readScenario(fileName: String): List<Location> {
        val fileContent = assets.open(fileName).bufferedReader().use(BufferedReader::readText)

        val locations = fileContent.lines().mapNotNull { line ->
            val parts = line.split(", ")
            if (parts.size == 3) {
                val latitude = parts[0].toDoubleOrNull()
                val longitude = parts[1].toDoubleOrNull()
                val imageName = parts[2]
                if (latitude != null && longitude != null) {
                    Location(latitude, longitude, imageName)
                } else {
                    null
                }
            } else {
                null
            }
        }
        return locations
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
                        //currentLatitude = location.latitude
                        //currentLongitude = location.longitude
                        currentLocation = location
                        println("set current location $currentLocation")

                        // Example how to calculate the distance
                        //val results = FloatArray(1)
                        //val savedLatitude = 52.3984
                        //val savedLongitude = 16.9486
                        //android.location.Location.distanceBetween(savedLatitude, savedLongitude, currentLatitude, currentLongitude, results)
                        //val distanceInMeters = results[0]

                    } else {
                        throw IllegalArgumentException("Location is null")
                    }
                }
                .addOnFailureListener { e ->
                    throw IllegalArgumentException("Failed to get location: ${e.message}")
                }
        }
    }


}








