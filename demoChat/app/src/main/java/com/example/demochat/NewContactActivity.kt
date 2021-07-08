package com.example.demochat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class NewContactActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userArrayList: ArrayList<UserItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)
        supportActionBar?.title = "Select User"

        recyclerView = findViewById(R.id.recyclerViewUserList)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()

        getUserDataFromFirebase()

    }//onCreate

    private  fun getUserDataFromFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(eachSnapshot in snapshot.children){
                        val user = eachSnapshot.getValue(UserItem::class.java)
                        userArrayList.add(user!!)
                    }
                    recyclerView.adapter = MyAdapter(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }//getUserDataFromFirebase

}//NewMessageActivity
