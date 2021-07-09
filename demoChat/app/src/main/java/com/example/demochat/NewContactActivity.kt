package com.example.demochat

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewContactActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)
        supportActionBar?.title = "Select User"
        recyclerView = findViewById(R.id.recyclerViewUserList)

        getUserDataFromFirebaseDatabase()
    }//onCreate

    private fun getUserDataFromFirebaseDatabase() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(UserClass::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // not necessary
            }

        })
    }
}//NewMessageActivity

class UserItem(private val user: UserClass) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.user_rows_new_contact

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val userName = viewHolder.itemView.findViewById<TextView>(R.id.tvUsername)
        val userProfileImage =
            viewHolder.itemView.findViewById<ImageView>(R.id.imageViewProfileImageRow)
        userName.text = user.username
        Picasso.get().load(user.profileImageUrl).into(userProfileImage)


    }//bind


}//PeopleItem