package com.music42.ft_hangouts

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.music42.ft_hangouts.db.Contact
import com.music42.ft_hangouts.db.ContactDbHelper

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: ContactDbHelper
    private var contactId: Long = -1
    private var contact: Contact? = null

    private lateinit var textViewInitial: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewPhone: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var layoutEmail: LinearLayout
    private lateinit var layoutAddress: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        dbHelper = ContactDbHelper(this)
        contactId = intent.getLongExtra("contact_id", -1)

        if (contactId == -1L) {
            finish()
            return
        }

        textViewInitial = findViewById(R.id.textViewInitial)
        textViewName = findViewById(R.id.textViewName)
        textViewPhone = findViewById(R.id.textViewPhone)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewAddress = findViewById(R.id.textViewAddress)
        layoutEmail = findViewById(R.id.layoutEmail)
        layoutAddress = findViewById(R.id.layoutAddress)

        val buttonSendMessage = findViewById<Button>(R.id.buttonSendMessage)
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        buttonSendMessage.setOnClickListener {
            val intent = Intent(this, ConversationActivity::class.java)
            intent.putExtra("contact_id", contactId)
            startActivity(intent)
        }

        buttonEdit.setOnClickListener {
            val intent = Intent(this, AddEditContactActivity::class.java)
            intent.putExtra("contact_id", contactId)
            startActivity(intent)
        }

        buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        title = getString(R.string.contact_details)
        applyHeaderColor()
    }

    override fun onResume() {
        super.onResume()
        loadContact()
        applyHeaderColor()
    }

    private fun loadContact() {
        contact = dbHelper.getContact(contactId)

        contact?.let {
            textViewInitial.text = it.getInitial()
            textViewName.text = it.getDisplayName()
            textViewPhone.text = it.phone

            if (it.email.isNotEmpty()) {
                textViewEmail.text = it.email
                layoutEmail.visibility = View.VISIBLE
            } else {
                layoutEmail.visibility = View.GONE
            }

            if (it.address.isNotEmpty()) {
                textViewAddress.text = it.address
                layoutAddress.visibility = View.VISIBLE
            } else {
                layoutAddress.visibility = View.GONE
            }
        } ?: run {
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_contact_title)
            .setMessage(R.string.delete_contact_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                dbHelper.deleteContact(contactId)
                Toast.makeText(this, R.string.contact_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun applyHeaderColor() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(App.getHeaderColor()))
        window.statusBarColor = App.getHeaderColor()
    }
}
