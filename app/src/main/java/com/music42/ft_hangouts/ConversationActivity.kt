package com.music42.ft_hangouts

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.music42.ft_hangouts.adapter.MessageAdapter
import com.music42.ft_hangouts.db.Contact
import com.music42.ft_hangouts.db.ContactDbHelper
import com.music42.ft_hangouts.db.Message

class ConversationActivity : AppCompatActivity() {

    private lateinit var dbHelper: ContactDbHelper
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var textViewEmpty: TextView

    private var contactId: Long = -1
    private var contact: Contact? = null

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadMessages()
        }
    }

    companion object {
        const val ACTION_SMS_RECEIVED = "com.music42.ft_hangouts.SMS_RECEIVED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        dbHelper = ContactDbHelper(this)
        contactId = intent.getLongExtra("contact_id", -1)

        if (contactId == -1L) {
            finish()
            return
        }

        contact = dbHelper.getContact(contactId)
        if (contact == null) {
            finish()
            return
        }

        title = contact?.getDisplayName() ?: getString(R.string.conversation)

        recyclerView = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        textViewEmpty = findViewById(R.id.textViewEmpty)
        val buttonSend = findViewById<ImageButton>(R.id.buttonSend)

        adapter = MessageAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        buttonSend.setOnClickListener {
            sendMessage()
        }

        applyHeaderColor()
    }

    override fun onResume() {
        super.onResume()
        loadMessages()
        applyHeaderColor()

        val filter = IntentFilter(ACTION_SMS_RECEIVED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(smsReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(smsReceiver)
        } catch (e: Exception) {
            // Receiver not registered
        }
    }

    private fun loadMessages() {
        val messages = dbHelper.getMessagesForContact(contactId)
        adapter.updateMessages(messages)

        if (messages.isEmpty()) {
            textViewEmpty.visibility = View.VISIBLE
        } else {
            textViewEmpty.visibility = View.GONE
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    private fun sendMessage() {
        val messageText = editTextMessage.text.toString().trim()
        if (messageText.isEmpty()) return

        val phone = contact?.phone ?: return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, R.string.sms_permission_required, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(messageText)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phone, null, messageText, null, null)
            }

            // Save message to database
            val message = Message(
                contactId = contactId,
                body = messageText,
                isIncoming = false
            )
            dbHelper.insertMessage(message)

            editTextMessage.text.clear()
            loadMessages()

            Toast.makeText(this, R.string.message_sent, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.message_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyHeaderColor() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(App.getHeaderColor()))
        window.statusBarColor = App.getHeaderColor()
    }
}
