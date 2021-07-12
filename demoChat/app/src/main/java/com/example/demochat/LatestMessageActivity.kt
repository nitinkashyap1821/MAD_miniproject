package com.example.demochat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.demochat.model.ChatMessageClass
import com.example.demochat.model.UserClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class LatestMessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessageMap = HashMap<String, ChatMessageClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        supportActionBar?.title = "Latest Messages"
        recyclerView = findViewById(R.id.recyclerViewLatestMessages)


        recyclerView.adapter = adapter

        listenForLatestMessage()

        fetchCurrentUserData()
        verifyUser()
    }//onCreate


    companion object {
        var currentUser: UserClass? = null
    }

    private fun listenForLatestMessage() {

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-message/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageClass::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refresh()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageClass::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refresh()

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

    }//listenForLatestMessage

    private fun refresh() {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageDataInserter(it))
        }

    }


    class LatestMessageDataInserter(val ChatMessage: ChatMessageClass) : Item<GroupieViewHolder>() {

        override fun getLayout() = R.layout.users_row_latest_message

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val imageView =
                viewHolder.itemView.findViewById<ImageView>(R.id.imageViewProfileImageRow)
            val tvUsername = viewHolder.itemView.findViewById<TextView>(R.id.tvUsername)
            val tvLatestMessage = viewHolder.itemView.findViewById<TextView>(R.id.tvLatestMessage)

            tvLatestMessage.text = ChatMessage.text

            val chatPartner: String
            if (ChatMessage.fromId == FirebaseAuth.getInstance().uid) {
                chatPartner = ChatMessage.toId.toString()
            } else {
                chatPartner = ChatMessage.fromId.toString()
            }
            val ref = FirebaseDatabase.getInstance().getReference("users/$chatPartner")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserClass::class.java)
                    tvUsername.text = user?.username
                    Picasso.get().load(user?.profileImageUrl).into(imageView)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }//bind
    }//UserItem

    private fun fetchCurrentUserData() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(UserClass::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}

        })


    }//fetchCurrentUserData

    private fun verifyUser() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }//verifyUser

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNewMessage -> {
                val intent = Intent(this, NewContactActivity::class.java)
                startActivity(intent)

            }
            R.id.menuSignOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homepage_navbar, menu)
        return super.onCreateOptionsMenu(menu)
    }//onCreateOptionsMenu
}//LatestMessageActivity