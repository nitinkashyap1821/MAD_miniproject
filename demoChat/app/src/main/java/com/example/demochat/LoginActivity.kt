package com.example.demochat

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var logIn: Button
    private lateinit var backToRegister: TextView
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        backToRegister = findViewById(R.id.textViewBack_to_registration)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        logIn = findViewById(R.id.buttonLogin)

        logIn.setOnClickListener {
            dialog = ProgressDialog(this)
            dialog.setMessage("Logging")
            dialog.show()
            performLogin()
        }

        backToRegister.setOnClickListener {
            finish()
        }
    }//onCreate

    private fun performLogin() {
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Fill the above details", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                dialog.dismiss()
                val intent = Intent(this, LatestMessageActivity::class.java)
                startActivity(intent)
                Log.d("Main", "uid = ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("Main", "uid = ${it.message}")
            }
    }//performLogin

}//LoginActivity