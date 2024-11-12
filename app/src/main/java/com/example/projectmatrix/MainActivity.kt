package com.example.projectmatrix

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.ComponentActivity
import android.util.Log
import android.view.WindowManager


class MainActivity : ComponentActivity() {

    // array to save coordinates
    private val clickCoordinates = mutableListOf<Pair<Float, Float>>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var i = 1
        val myButton: Button = findViewById(R.id.myButton)
        val submitButton: Button = findViewById(R.id.submitButton)
        val myTextView: TextView = findViewById(R.id.myTextView)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextSurname: EditText = findViewById(R.id.editTextSurname)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)
        val imageView: ImageView = findViewById(R.id.imageView)

        val corData = listOf(
            "52.40633, 16.95103",
            "52.40588, 16.95057",
            "52.40567, 16.95037",
            "52.40517, 16.95155",
            "52.40467, 16.95108",
            "52.40423, 16.95064",
            "52.40376, 16.95062",
            "52.40326, 16.95002",
            "52.40302, 16.94988",
            "52.40227, 16.94951",
            "52.40180, 16.94904",
            "52.40158, 16.94975",
            "52.40121, 16.95072"
        )

        val coordinates = corData.map { line ->
            val (x, y) = line.split(", ").map { it.toDouble() }
            Pair(x, y)
        }

        myButton.setOnClickListener {
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
                showConfirmationPopup(this, "${coordinates[0]}", R.drawable.rofl) {
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

                            if (clickCoordinates.size % 1 == 0) {
                                // popup

                                val coordinate = coordinates[i]
                                showConfirmationPopup(this, "$coordinate", R.drawable.rofl) {
                                    val matrix = findViewById<GridLayout>(R.id.matrix)
                                    setupMatrixClicks(matrix)
                                }
                                i++
                            }

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

}








