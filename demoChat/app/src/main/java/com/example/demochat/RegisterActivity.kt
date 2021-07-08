package com.example.demochat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var register: Button
    private lateinit var haveAccount: TextView
    private lateinit var imageInsert: Button
    private lateinit var imageView: ImageView
    private val tag: String = "RegisterActivity"
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        register = findViewById(R.id.buttonRegister)
        haveAccount = findViewById(R.id.textViewAlready_have_an_account)
        imageInsert = findViewById(R.id.buttonInsertImage)
        imageView = findViewById(R.id.imageViewProfileImage)


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


    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedPhotoUri = data.data
            imageView.setImageURI(selectedPhotoUri)
            imageInsert.alpha = 0f
        }
    }

    private fun performRegister() {
        if (email.text.isNullOrEmpty() || password.text.isNullOrEmpty() || username.text.isNullOrEmpty()) {
            Toast.makeText(this, "Fill the above details", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(tag, "uid = ${it.result?.user?.uid}")
                uploadImageToFirebase()

            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d(tag, "${it.message}")
            }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("/images/$fileName")

        refStorage.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(tag, "image uploaded")

                refStorage.downloadUrl
                    .addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                    }

            }
            .addOnFailureListener {
                Log.d(tag, "${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "uploaded to DB")
                val intent = Intent(this,LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(tag, "${it.message}")
            }
    }

}

class User(val uid: String, val username: String, val profileImageUrl: String)
