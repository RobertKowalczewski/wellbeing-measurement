package com.example.projectmatrix

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.widget.*


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myButton: Button = findViewById(R.id.myButton)
        val submitButton: Button = findViewById(R.id.submitButton)
        val myTextView: TextView = findViewById(R.id.myTextView)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextSurname: EditText = findViewById(R.id.editTextSurname)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)


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
                myTextView.text = "bla bla bla matrix matrix matrix"
                myTextView.visibility = View.VISIBLE
                editTextName.visibility = View.GONE
                editTextSurname.visibility = View.GONE
                editTextPhone.visibility = View.GONE
                submitButton.visibility = View.GONE
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
}







