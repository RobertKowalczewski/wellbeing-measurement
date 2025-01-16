package com.example.projectmatrix

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.RED
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
import com.example.projectmatrix.connection.websocket.WebsocketConnectionService
import com.example.projectmatrix.csv.CsvService
import com.example.projectmatrix.dto.SmartwatchDataDto
import com.example.projectmatrix.extern.SavingService
import com.example.projectmatrix.storage.config.AppDatabase
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser
import com.example.projectmatrix.storage.service.analytics.AnalyticsService
import com.example.projectmatrix.storage.service.matrix.MatrixDataFactory
import com.example.projectmatrix.storage.service.matrix.MatrixDataService
import com.example.projectmatrix.storage.service.smartwatch.SmartwatchDataService
import com.example.projectmatrix.storage.service.user.WellbeingUserService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


data class Location(
    var latitude: Double,
    var longitude: Double,
    var imageName: String
)

class MainActivity : ComponentActivity() {

    private lateinit var currentLocation: android.location.Location
    private val clickCoordinates = mutableListOf<Pair<Double, Double>>()
    private var lastClickPosition: Pair<Double, Double> = Pair(0.0, 0.0)
    private var currentPoint: Int = 0
    private var totalPoints: Int = 0
    private var wellbeingUser: WellbeingUser? = null
    private lateinit var db: AppDatabase
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

//        GlobalScope.launch {
//            val smartwatchDataService = SmartwatchDataService(db.smartwatchDataRepository())
//            val data = smartwatchDataService.findAll()
//            runOnUiThread {
//                Log.d("watch_data", "Data from watch: " + data.size)
//
//                data.forEach { row ->
//                    Log.d("watch_data", row.toString())
//                }
//            }
//        }
        GlobalScope.launch {
            val analyticsService = AnalyticsService(db.wellbeingUserRepository())
            val matrixDataService = MatrixDataService(db.matrixDataRepository())

            val data = analyticsService.retrieveAllMatrixDataForWellbeingUserId(1L)
            val data2 = matrixDataService.findAll()

            runOnUiThread {

                Log.d("FULL", "DATA: " + data.toString())
            }

        }

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
        val ipAddress: TextView = findViewById(R.id.ipAddress)

        startWatchConnection(ipAddress)
        setRegisteredUsers()

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
            registeredUsersLayout.visibility = View.VISIBLE
            ipAddress.visibility = View.GONE
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
                goToNextScenarioStep()
            }
        }

        yesSureButton.setOnClickListener {
            goToNextScenarioStep()
        }

        noSureButton.setOnClickListener {
            areYouSureLayout.visibility = View.GONE
            areYouHereLayout.visibility = View.VISIBLE
        }

        finishButton.setOnClickListener {
            Toast.makeText(this, "Thank you for your participation!", Toast.LENGTH_LONG).show()
            sendResults();
            finish()
        }
    }

    private fun goToNextScenarioStep() {
        saveMatrixData()

        areYouSureLayout.visibility = View.GONE
        matrixText.visibility = View.VISIBLE
        matrixConfirmButton.visibility = View.VISIBLE
        circleView.visibility = View.INVISIBLE
        currentPoint++
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
    }

    private fun onFailedConnectionStart(ipAddress: TextView, message: String) {
        ipAddress.setTextColor(RED)
        ipAddress.text = message
    }

    private fun sendResults() {
        runBlocking {
            val job = GlobalScope.launch {

                Log.d("LAST", "SAVING...")

                val analyticsService = AnalyticsService(db.wellbeingUserRepository())
                val csvService = CsvService()
                val savingService = SavingService()

                val matrixData = wellbeingUser?.let {
                    analyticsService.retrieveAllMatrixDataForWellbeingUserId(it.id)
                }

                val smartwatchData = wellbeingUser?.let {
                    analyticsService.retrieveAllSmartwatchDataForWellbeingUserId(it.id)
                }

                val csvMatrixFile = csvService.matrixDataToCsvFile(matrixData)
                val csvSmartwatchFile = csvService.smartwatchDataToCsvFile(smartwatchData)

                if (csvMatrixFile.isEmpty) {
                    return@launch
                }

                savingService.saveCsv(streamToBytes(csvMatrixFile.get()), "result_matrix.csv", applicationContext);
                savingService.saveCsv(streamToBytes(csvSmartwatchFile.get()), "result_smartwatch.csv", applicationContext);
                Log.d("LAST", "Saved")
            }
            job.join()
        }
    }

    private fun streamToBytes(inputStream: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        var nRead: Int
        val data = ByteArray(1024)
        while ((inputStream.read(data, 0, data.size).also { nRead = it }) != -1) {
            buffer.write(data, 0, nRead)
        }
        return buffer.toByteArray()
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

    private fun saveMatrixData() {
        GlobalScope.launch {
            val matrixDataService = MatrixDataService(db.matrixDataRepository())
            val matrixDataFactory = MatrixDataFactory(matrixDataService, db.wellbeingUserRepository())

            val matrixData = matrixDataFactory.create(
                wellbeingUser,
                currentLocation.longitude,
                currentLocation.latitude,
                clickCoordinates.get(clickCoordinates.size - 1).first,
                clickCoordinates.get(clickCoordinates.size - 1).second
            )
            Log.d("MATRIX", "MATRIX: " + matrixData.toString())
            matrixDataService.save(matrixData)
        }
    }

    private fun goToMatrix(name: String, surname: String, phone: String) {
        setUser(name, surname, phone)
        Log.d("USER", "USER: " + wellbeingUser)
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
            if (lastClickPosition.first == 0.0 && lastClickPosition.second == 0.0) {
                Toast.makeText(this, "Please select a point on the matrix.", Toast.LENGTH_SHORT).show()
            } else {
                clickCoordinates.add(lastClickPosition)
                lastClickPosition = Pair(0.0, 0.0)
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
            .fallbackToDestructiveMigration()
            .build();
    }

    private fun setUser(name: String, surname: String, phone: String) {
        runBlocking {
            val job = GlobalScope.launch {
                val userService = WellbeingUserService(db.wellbeingUserRepository())
                wellbeingUser = userService.findOrCreateUser(name, surname, phone)

                runOnUiThread {
                    Log.i("main", "Data: " + wellbeingUser?.name + " " + wellbeingUser?.surname + " " + wellbeingUser?.creationTimestamp + " " + wellbeingUser?.modificationTimestamp)
                }
            }
            job.join()
        }
    }

    private fun setupMatrix(imageView: ImageView, circleView: ImageView) {
        imageView.visibility = View.VISIBLE
        registeredUsersLayout.visibility = View.GONE
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
                    lastClickPosition = Pair(normX.toDouble(), normY.toDouble())
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
            locationImageView.setImageResource(R.drawable.tree) // Установите изображение по умолчанию
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
                clickCoordinates.add(Pair(row.toDouble(), col.toDouble()))
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
            val userService = WellbeingUserService(db.wellbeingUserRepository())
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

    fun saveWatchData(watchData: SmartwatchDataDto) {
        if (wellbeingUser != null) {
            GlobalScope.launch {
                val smartwatchDataService = SmartwatchDataService(db.smartwatchDataRepository())

                val smartWatchData = smartwatchDataService.createFor(wellbeingUser)
                smartWatchData.heartRate = watchData.heartRate

                smartwatchDataService.save(smartWatchData)
            }
        }
    }
}