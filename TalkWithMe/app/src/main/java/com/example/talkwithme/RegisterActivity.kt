package com.example.talkwithme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var register: Button
    private lateinit var haveAccount: TextView
    private lateinit var imageInsert: Button
    private lateinit var imageView: ImageView
    val tag: String = "RegisterActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        register = findViewById(R.id.buttonRegister)
        haveAccount = findViewById(R.id.textViewAlready_have_an_account)
        imageInsert = findViewById(R.id.buttonInsertImage)
        imageView = findViewById(R.id.imageViewImageInsert)


        imageInsert.setOnClickListener {
            selectImage()
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

    private fun selectImage() {
        Log.d("RegisterActivity", "clicked image button")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            Toast.makeText(this@RegisterActivity, "clicked image selected", Toast.LENGTH_SHORT)
                .show()

            val selectedPhotoUri = data.data
            Toast.makeText(this@RegisterActivity, "$selectedPhotoUri", Toast.LENGTH_SHORT).show()
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

                uploadToFirebaseDatabase()

            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("RegisterActivity", "${it.message}")
            }
    }

    private fun uploadImageToFirebase() {
        /*if (selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

        refStorage.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(tag, "image uploaded")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "${it.message}")

            }*/
        if (selectedPhotoUri == null) return
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("upload/$fileName")


        storageReference.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(this@RegisterActivity, "image uploaded", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "image not uploaded", Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun uploadToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username.text.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "added to db", Toast.LENGTH_SHORT).show()
            }
    }
}

class User(val uid: String, val username: String)
