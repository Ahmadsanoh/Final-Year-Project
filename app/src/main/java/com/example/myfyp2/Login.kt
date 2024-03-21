package com.example.myfyp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        val loginButton = findViewById<Button>(R.id.btn_Login)
        val passwordEditText = findViewById<EditText>(R.id.password_toggle)
        val passwordVisibilityToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)

        passwordVisibilityToggle.setOnClickListener {
            // Toggle password visibility logic here
            // This part requires the ImageView with id 'passwordVisibilityToggle' in your XML
        }

        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.email)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateEmail(emailEditText) && validatePassword(passwordEditText)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser? = auth.currentUser
                            firebaseUser?.let {
                                val userId = it.uid
                                val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        // Retrieve user data from snapshot
                                        val userData = snapshot.value as Map<*, *>?
                                        // Process user data as needed
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle error
                                    }
                                })
                            }
                            showToast("Login Successful")
                            val intent = Intent(this@Login, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            showToast("Login Failed: ${task.exception?.message}")
                        }
                    }
            } else {
                if (!validateEmail(emailEditText)) {
                    showToast("Invalid email format. Please enter a valid email.")
                }
                if (!validatePassword(passwordEditText)) {
                    showToast("Password must contain at least one letter and one digit with a minimum length of 6 characters.")
                }
            }
        }

        val registerButton = findViewById<Button>(R.id.btn_Register)
        registerButton.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }
    }

    private fun validateEmail(emailEditText: EditText): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val email = emailEditText.text.toString().trim()
        return email.matches(emailPattern.toRegex())
    }

    private fun validatePassword(passwordEditText: EditText): Boolean {
        val passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}\$"
        val password = passwordEditText.text.toString().trim()
        return password.matches(passwordPattern.toRegex())
    }

    private fun showToast(message: String) {
        Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()
    }
}