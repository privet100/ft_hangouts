package com.music42.ft_hangouts.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 1

        // Contacts table
        private const val TABLE_CONTACTS = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FIRST_NAME = "first_name"
        private const val COLUMN_LAST_NAME = "last_name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ADDRESS = "address"

        // Messages table
        private const val TABLE_MESSAGES = "messages"
        private const val COLUMN_MESSAGE_ID = "id"
        private const val COLUMN_CONTACT_ID = "contact_id"
        private const val COLUMN_BODY = "body"
        private const val COLUMN_IS_INCOMING = "is_incoming"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createContactsTable = """
            CREATE TABLE $TABLE_CONTACTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FIRST_NAME TEXT,
                $COLUMN_LAST_NAME TEXT,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_ADDRESS TEXT
            )
        """.trimIndent()

        val createMessagesTable = """
            CREATE TABLE $TABLE_MESSAGES (
                $COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CONTACT_ID INTEGER NOT NULL,
                $COLUMN_BODY TEXT NOT NULL,
                $COLUMN_IS_INCOMING INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_CONTACT_ID) REFERENCES $TABLE_CONTACTS($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createContactsTable)
        db.execSQL(createMessagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Contact CRUD operations

    fun insertContact(contact: Contact): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
        }
        return db.insert(TABLE_CONTACTS, null, values)
    }

    fun updateContact(contact: Contact): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
        }
        return db.update(TABLE_CONTACTS, values, "$COLUMN_ID = ?", arrayOf(contact.id.toString()))
    }

    fun deleteContact(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_CONTACTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getContact(id: Long): Contact? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CONTACTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                Contact(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    firstName = it.getString(it.getColumnIndexOrThrow(COLUMN_FIRST_NAME)) ?: "",
                    lastName = it.getString(it.getColumnIndexOrThrow(COLUMN_LAST_NAME)) ?: "",
                    phone = it.getString(it.getColumnIndexOrThrow(COLUMN_PHONE)) ?: "",
                    email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
                    address = it.getString(it.getColumnIndexOrThrow(COLUMN_ADDRESS)) ?: ""
                )
            } else null
        }
    }

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CONTACTS,
            null, null, null, null, null,
            "$COLUMN_FIRST_NAME, $COLUMN_LAST_NAME"
        )

        cursor.use {
            while (it.moveToNext()) {
                contacts.add(
                    Contact(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        firstName = it.getString(it.getColumnIndexOrThrow(COLUMN_FIRST_NAME)) ?: "",
                        lastName = it.getString(it.getColumnIndexOrThrow(COLUMN_LAST_NAME)) ?: "",
                        phone = it.getString(it.getColumnIndexOrThrow(COLUMN_PHONE)) ?: "",
                        email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
                        address = it.getString(it.getColumnIndexOrThrow(COLUMN_ADDRESS)) ?: ""
                    )
                )
            }
        }
        return contacts
    }

    fun getContactByPhone(phone: String): Contact? {
        val db = readableDatabase
        val normalizedPhone = phone.replace(Regex("[^0-9+]"), "")
        val cursor = db.query(
            TABLE_CONTACTS,
            null,
            "REPLACE(REPLACE(REPLACE($COLUMN_PHONE, ' ', ''), '-', ''), '(', '') LIKE ?",
            arrayOf("%$normalizedPhone%"),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                Contact(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    firstName = it.getString(it.getColumnIndexOrThrow(COLUMN_FIRST_NAME)) ?: "",
                    lastName = it.getString(it.getColumnIndexOrThrow(COLUMN_LAST_NAME)) ?: "",
                    phone = it.getString(it.getColumnIndexOrThrow(COLUMN_PHONE)) ?: "",
                    email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
                    address = it.getString(it.getColumnIndexOrThrow(COLUMN_ADDRESS)) ?: ""
                )
            } else null
        }
    }

    // Message operations

    fun insertMessage(message: Message): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTACT_ID, message.contactId)
            put(COLUMN_BODY, message.body)
            put(COLUMN_IS_INCOMING, if (message.isIncoming) 1 else 0)
            put(COLUMN_TIMESTAMP, message.timestamp)
        }
        return db.insert(TABLE_MESSAGES, null, values)
    }

    fun getMessagesForContact(contactId: Long): List<Message> {
        val messages = mutableListOf<Message>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MESSAGES,
            null,
            "$COLUMN_CONTACT_ID = ?",
            arrayOf(contactId.toString()),
            null, null,
            "$COLUMN_TIMESTAMP ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                messages.add(
                    Message(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)),
                        contactId = it.getLong(it.getColumnIndexOrThrow(COLUMN_CONTACT_ID)),
                        body = it.getString(it.getColumnIndexOrThrow(COLUMN_BODY)),
                        isIncoming = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_INCOMING)) == 1,
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                    )
                )
            }
        }
        return messages
    }
}
