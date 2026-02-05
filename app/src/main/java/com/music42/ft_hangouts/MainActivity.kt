package com.music42.ft_hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.music42.ft_hangouts.adapter.ContactAdapter
import com.music42.ft_hangouts.db.ContactDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: ContactDbHelper
    private lateinit var adapter: ContactAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewEmpty: TextView

    private var isReturningFromBackground = false

    companion object {
        private const val SMS_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = ContactDbHelper(this)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        textViewEmpty = findViewById(R.id.textViewEmpty)
        val fabAddContact = findViewById<FloatingActionButton>(R.id.fabAddContact)

        adapter = ContactAdapter(emptyList()) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("contact_id", contact.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAddContact.setOnClickListener {
            startActivity(Intent(this, AddEditContactActivity::class.java))
        }

        requestSmsPermissions()
        applyHeaderColor()
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
        applyHeaderColor()

        // Show background time toast
        val backgroundTime = App.getBackgroundTime()
        if (backgroundTime > 0 && isReturningFromBackground) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val formattedTime = dateFormat.format(Date(backgroundTime))
            Toast.makeText(
                this,
                getString(R.string.app_was_in_background, formattedTime),
                Toast.LENGTH_LONG
            ).show()
            App.clearBackgroundTime()
        }
        isReturningFromBackground = false
    }

    override fun onPause() {
        super.onPause()
        App.setBackgroundTime(System.currentTimeMillis())
        isReturningFromBackground = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val colorResId = when (item.itemId) {
            R.id.color_blue -> R.color.header_blue
            R.id.color_green -> R.color.header_green
            R.id.color_red -> R.color.header_red
            R.id.color_purple -> R.color.header_purple
            R.id.color_orange -> R.color.header_orange
            else -> return super.onOptionsItemSelected(item)
        }

        App.setHeaderColor(colorResId)
        applyHeaderColor()
        return true
    }

    private fun loadContacts() {
        val contacts = dbHelper.getAllContacts()
        adapter.updateContacts(contacts)

        if (contacts.isEmpty()) {
            textViewEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textViewEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun applyHeaderColor() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(App.getHeaderColor()))
        window.statusBarColor = App.getHeaderColor()
    }

    private fun requestSmsPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                SMS_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, R.string.sms_permission_required, Toast.LENGTH_LONG).show()
            }
        }
    }
}
