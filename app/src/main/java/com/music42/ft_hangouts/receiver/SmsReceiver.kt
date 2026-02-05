package com.music42.ft_hangouts.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.music42.ft_hangouts.ConversationActivity
import com.music42.ft_hangouts.db.ContactDbHelper
import com.music42.ft_hangouts.db.Message

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val dbHelper = ContactDbHelper(context)

        for (smsMessage in messages) {
            val sender = smsMessage.originatingAddress ?: continue
            val body = smsMessage.messageBody ?: continue

            // Find contact by phone number
            val contact = dbHelper.getContactByPhone(sender)

            if (contact != null) {
                // Save message to database
                val message = Message(
                    contactId = contact.id,
                    body = body,
                    isIncoming = true
                )
                dbHelper.insertMessage(message)

                // Notify conversation activity
                val broadcastIntent = Intent(ConversationActivity.ACTION_SMS_RECEIVED)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }
}
