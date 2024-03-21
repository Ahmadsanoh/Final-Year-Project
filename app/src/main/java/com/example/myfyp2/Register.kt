package com.example.myfyp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password_toggle)
        val phoneEditText = findViewById<EditText>(R.id.phone)
        val registerButton = findViewById<Button>(R.id.btn_registration)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Initially, disable the Register button
        registerButton.isEnabled = false


        // Add TextChangedListeners to all three fields
        emailEditText.addTextChangedListener {
            validateFields(emailEditText, passwordEditText, phoneEditText, registerButton)
        }

        passwordEditText.addTextChangedListener {
            validateFields(emailEditText, passwordEditText, phoneEditText, registerButton)
        }

        phoneEditText.addTextChangedListener {
            validateFields(emailEditText, passwordEditText, phoneEditText, registerButton)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phone = phoneEditText.text.toString()

            // Perform registration if fields are valid
            if (validateEmail(emailEditText) && validatePassword(passwordEditText) && validatePhone(phoneEditText)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration success, update UI with the registered user's information
                            val user: FirebaseUser? = auth.currentUser
                            user?.let {
                                // Store user data in Firebase Database
                                val userId = it.uid
                                val userData = HashMap<String, Any>()
                                userData["email"] = email
                                userData["phone"] = phone
                                // Add more user data as needed

                                database.reference.child("users").child(userId).setValue(userData)
                            }
                            // Proceed to the next activity or handle success accordingly
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                        } else {
                            // If registration fails, display a message to the user.
                            // You can customize the error handling here
                            showToast("Registration failed: ${task.exception?.message}")
                        }
                    }
            } else {
                // Handle invalid fields or show an error message to the user
                showToast("Invalid fields or missing information")
            }
        }
    }

    private fun validateEmail(emailEditText: EditText?): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val email = emailEditText?.text.toString().trim()
        return !email.isNullOrEmpty() && email.matches(emailPattern.toRegex())
    }

    private fun validatePassword(passwordEditText: EditText?): Boolean {
        val passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}\$"
        val password = passwordEditText?.text.toString().trim()
        return !password.isNullOrEmpty() && password.matches(passwordPattern.toRegex())
    }

    private fun validatePhone(phoneEditText: EditText?): Boolean {
        val phonePattern = "[0-9]+"
        val phone = phoneEditText?.text.toString().trim()
        return !phone.isNullOrEmpty() && phone.matches(phonePattern.toRegex())
    }


    private fun validateFields(
        emailEditText: EditText?,
        passwordEditText: EditText?,
        phoneEditText: EditText?,
        registerButton: Button?
    ) {
        val email = emailEditText?.text.toString().trim()
        val password = passwordEditText?.text.toString().trim()
        val phone = phoneEditText?.text.toString().trim()

        // Enable the Register button if all fields are not empty
        registerButton?.isEnabled = !email.isNullOrEmpty() && !password.isNullOrEmpty() && !phone.isNullOrEmpty()
    }


    // Validation functions remain unchanged...

    private fun showToast(message: String) {
        Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
    }
}
