package com.example.demochat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.demochat.model.UserClass
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
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        register = findViewById(R.id.buttonRegister)
        haveAccount = findViewById(R.id.textViewAlready_have_an_account)
        imageInsert = findViewById(R.id.buttonInsertImage)
        imageView = findViewById(R.id.imageViewProfileImage)


        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            selectedPhotoUri = it
            imageView.setImageURI(it)
            if (selectedPhotoUri != null) {
                imageInsert.alpha = 0f
            }
        }

        imageInsert.setOnClickListener {
            getImage.launch("image/*")
        }

        register.setOnClickListener {
            dialog = ProgressDialog(this)
            dialog.setMessage("Registering User")
            dialog.show()
            performRegister()
        }

        haveAccount.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }//onCreate

    private fun performRegister() {
        if (email.text.isNullOrEmpty() || password.text.isNullOrEmpty() || username.text.isNullOrEmpty() || imageView.drawable == null) {
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
                dialog.dismiss()
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d(tag, "${it.message}")
            }
    }//performRegister

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
                dialog.dismiss()
                Log.d(tag, "${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }//uploadImageToFirebase

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = UserClass(uid, username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "uploaded to DB")
                dialog.dismiss()
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Log.d(tag, "${it.message}")
            }
    }//saveUserToFirebaseDatabase

}//RegisterActivity

