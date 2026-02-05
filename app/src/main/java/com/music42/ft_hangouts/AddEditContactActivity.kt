package com.music42.ft_hangouts

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.music42.ft_hangouts.db.Contact
import com.music42.ft_hangouts.db.ContactDbHelper

class AddEditContactActivity : AppCompatActivity() {

    private lateinit var dbHelper: ContactDbHelper
    private var contactId: Long = -1

    private lateinit var editTextFirstName: TextInputEditText
    private lateinit var editTextLastName: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_contact)

        dbHelper = ContactDbHelper(this)
        contactId = intent.getLongExtra("contact_id", -1)

        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextAddress = findViewById(R.id.editTextAddress)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)

        if (contactId != -1L) {
            title = getString(R.string.edit_contact)
            loadContact()
            buttonDelete.visibility = View.VISIBLE
        } else {
            title = getString(R.string.new_contact)
            buttonDelete.visibility = View.GONE
        }

        buttonSave.setOnClickListener {
            saveContact()
        }

        buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        applyHeaderColor()
    }

    override fun onResume() {
        super.onResume()
        applyHeaderColor()
    }

    private fun loadContact() {
        val contact = dbHelper.getContact(contactId)
        contact?.let {
            editTextFirstName.setText(it.firstName)
            editTextLastName.setText(it.lastName)
            editTextPhone.setText(it.phone)
            editTextEmail.setText(it.email)
            editTextAddress.setText(it.address)
        }
    }

    private fun saveContact() {
        val phone = editTextPhone.text.toString().trim()

        if (phone.isEmpty()) {
            Toast.makeText(this, R.string.phone_required, Toast.LENGTH_SHORT).show()
            return
        }

        val contact = Contact(
            id = if (contactId != -1L) contactId else 0,
            firstName = editTextFirstName.text.toString().trim(),
            lastName = editTextLastName.text.toString().trim(),
            phone = phone,
            email = editTextEmail.text.toString().trim(),
            address = editTextAddress.text.toString().trim()
        )

        if (contactId != -1L) {
            dbHelper.updateContact(contact)
        } else {
            dbHelper.insertContact(contact)
        }

        Toast.makeText(this, R.string.contact_saved, Toast.LENGTH_SHORT).show()
        finish()
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
