package com.example.demochat

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.demochat.model.ChatMessageClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
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

        listenForMessages()

        sendButton.setOnClickListener {
            sendMessage()
        }
    }//onCreate

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = intent.getStringExtra(NewContactActivity.USER_KEY_UID).toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageClass::class.java)
                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser =
                            Uri.parse(LatestMessageActivity.currentUser?.profileImageUrl.toString())
                        adapter.add(ChatFromItem(chatMessage.text.toString(), currentUser))
                    } else {
                        val toUserImage: Uri = Uri.parse(
                            intent.getStringExtra(NewContactActivity.USER_KEY_IMAGE).toString()
                        )
                        adapter.add(ChatToItem(chatMessage.text.toString(), toUserImage))
                    }
                }

                recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }//listenForMessages

    private fun sendMessage() {

        val message = editTextMessageArea.text.toString()
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = intent.getStringExtra(NewContactActivity.USER_KEY_UID).toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val latestMessageRefFrom =
            FirebaseDatabase.getInstance().getReference("latest-message/$fromId/$toId")
        val latestMessageRefTo =
            FirebaseDatabase.getInstance().getReference("latest-message/$toId/$fromId")

        val chatMessage =
            ChatMessageClass(ref.key.toString(), message, fromId, toId, System.currentTimeMillis())
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                editTextMessageArea.text.clear()
                recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }
        toRef.setValue(chatMessage)
            .addOnSuccessListener {
                recyclerViewChatLog.scrollToPosition(adapter.itemCount - 2)
            }
        latestMessageRefFrom.setValue(chatMessage)
        latestMessageRefTo.setValue(chatMessage)
    }//sendMessage
}//ChatLogActivity

class ChatFromItem(val text: String, private val user: Uri) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.chat_from_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewFromRow = viewHolder.itemView.findViewById<TextView>(R.id.textView_from_row)
        val imageViewFromRow = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)

        textViewFromRow.text = text
        Picasso.get().load(user).into(imageViewFromRow)
    }//bind
}//ChatFromItem


class ChatToItem(val text: String, private val user: Uri) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.chat_to_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textViewToRow = viewHolder.itemView.findViewById<TextView>(R.id.textView_to_row)
        val imageViewToRow = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_to_row)

        textViewToRow.text = text
        Picasso.get().load(user).into(imageViewToRow)

    }//bind
}//ChatToItem