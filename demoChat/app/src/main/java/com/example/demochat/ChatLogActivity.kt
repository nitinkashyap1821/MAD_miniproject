package com.example.demochat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ChatLogActivity : AppCompatActivity() {

    private lateinit var recyclerViewChatLog: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var editTextMessageArea: EditText
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        supportActionBar?.title = intent.getStringExtra(NewContactActivity.USER_KEY_NAME).toString()
        recyclerViewChatLog = findViewById(R.id.recyclerViewChatLog)
        sendButton = findViewById(R.id.buttonSend)
        editTextMessageArea = findViewById(R.id.editTextMessageArea)

        recyclerViewChatLog.adapter = adapter
//        setupDummyData()
        listenForMessages()

        sendButton.setOnClickListener {
            sendMessage()
        }
    }//onCreate

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageClass::class.java)
                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text.toString()))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text.toString()))
                    }
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //not necessary
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //not necessary
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //not necessary
            }

            override fun onCancelled(error: DatabaseError) {
                //not necessary
            }

        })
    }

    private fun sendMessage() {

        val message = editTextMessageArea.text.toString()
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = intent.getStringExtra(NewContactActivity.USER_KEY_UID).toString()
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage =
            ChatMessageClass(ref.key.toString(), message, fromId, toId, System.currentTimeMillis())
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                editTextMessageArea.text.clear()
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
            }
    }//sendMessage
}//ChatLogActivity

class ChatFromItem(val text: String) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.chat_from_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        val textViewFromRow = viewHolder.itemView.findViewById<TextView>(R.id.textView_from_row)
        textViewFromRow.text = text
    }//bind
}//ChatFromItem


class ChatToItem(val text: String) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.chat_to_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewToRow = viewHolder.itemView.findViewById<TextView>(R.id.textView_to_row)
        textViewToRow.text = text
    }//bind
}//ChatToItem