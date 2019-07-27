package com.toon.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{

        val TAG = "ChatLog"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username

        setUpDummyData()

        send_button_chat_log.setOnClickListener {

            Log.d(TAG, "Attempt to send message.....")

            performSendMessage()

        }

    }


    class ChatMessage(val text: String)

    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(text)

        reference.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Save our chat message: ${reference.key}")

            }

    }

    private fun setUpDummyData() {

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(CharFromItem("FROM MEEESSSSAAAGGGE"))
        adapter.add(CharToItem("TO MMMMESSAAAGGGEE \n TO MMMEEESSSAAAGGGEEE"))
        adapter.add(CharFromItem("FROM "))
        adapter.add(CharToItem("TO"))
        adapter.add(CharFromItem("FROM MEEESSSSAAAGGGE"))
        adapter.add(CharToItem("TO "))
        adapter.add(CharFromItem("FROM "))
        adapter.add(CharToItem("TO MMMMESSAAAGGGEE \n TO MMMEEESSSAAAGGGEEE"))
        adapter.add(CharFromItem("FROM MEEESSSSAAAGGGE"))
        adapter.add(CharToItem("TO MMMMESSAAAGGGEE \n TO MMMEEESSSAAAGGGEEE"))

        recyclerView_chat_log.adapter = adapter

    }

}

class CharFromItem(val text : String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_from_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row

    }

}

class CharToItem(val text : String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_to_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row

    }

}