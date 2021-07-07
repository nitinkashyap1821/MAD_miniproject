package com.example.talkwithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var logIn: Button
    private lateinit var backToRegister: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        backToRegister = findViewById(R.id.textViewBack_to_registration)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        logIn = findViewById(R.id.buttonLogin)

        logIn.setOnClickListener {
            performLogin()
        }

        backToRegister.setOnClickListener {
            finish()
        }
    }

    private fun performLogin() {
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Fill the above details", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Log.d("Main", "uid = ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("Main", "uid = ${it.message}")
            }
    }

}