package com.example.talkwithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signUp: Button
    private lateinit var logIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        name = findViewById(R.id.etName)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        confirmPassword = findViewById(R.id.etConfirmPassword)
        signUp = findViewById(R.id.btnSignUp)
        logIn = findViewById(R.id.btnLogin)

        auth = FirebaseAuth.getInstance()

        signUp.setOnClickListener {
            if (name.text.isNullOrEmpty()) {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.text.isNullOrEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (password.text.isNullOrEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (confirmPassword.text.isNullOrEmpty()) {
                Toast.makeText(this, "Retype the password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if (password.text.toString() != confirmPassword.text.toString()) {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            registerUser(name.text.toString(), email.text.toString(), password.text.toString())

        }

        logIn.setOnClickListener {
            val intent = Intent(this@RegisterationActivity,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registerUser(userName: String, Temail: String, Tpassword: String) {
        auth.createUserWithEmailAndPassword(Temail, Tpassword)
            .addOnCompleteListener(this) { it ->
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    val database = Firebase.database
                    val myref = database.getReference("messages")
//                    databaseReference =
//                        FirebaseDatabase.getInstance().getReference("Users")
//
//                    val hashMap:HashMap<String,String> = HashMap()
//                    hashMap.put("userId",userId)
//                    hashMap.put("userName",userName)
//                    hashMap.put("profileImage","")
//
//                    databaseReference.setValue(hashMap)
                            myref.setValue("hello")
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                //go to dashBoard
                                name.setText("")
                                email.setText("")
                                password.setText("")
                                confirmPassword.setText("")
                                val intent =
                                    Intent(this@RegisterationActivity, HomeActivity::class.java)
                                startActivity(intent)
                            }
                        }
                }
            }
            .addOnFailureListener {

                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()

            }
    }
}