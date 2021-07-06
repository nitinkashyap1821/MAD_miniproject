package com.example.talkwithme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {


    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var register: Button
    private lateinit var haveAccount: TextView
    private lateinit var imageInsert: Button
    private lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        register = findViewById(R.id.buttonRegister)
        haveAccount = findViewById(R.id.textViewAlready_have_an_account)
        imageInsert = findViewById(R.id.buttonInsertImage)
        imageView = findViewById(R.id.imageViewImageInsert)



        imageInsert.setOnClickListener {
            Log.d("RegisterActivity", "clicked image button")
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Please select..."
                ),
                0
            )
        }

        register.setOnClickListener {
            performRegister()
        }
        haveAccount.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            Log.d("RegisterActivity", "clicked image selected")

            val selectedPhotoUri = data.data
            Log.d("RegisterActivity", "$selectedPhotoUri")
            imageView.setImageURI(selectedPhotoUri)
        }
    }

    private fun performRegister() {
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Fill the above details", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity", "uid = ${it.result?.user?.uid}")

                uploadImageToFirebase()
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("RegisterActivity", "${it.message}")
            }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return

        val filename =  UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename" )

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "image uploaded")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "${it.message}")

            }


    }
}

