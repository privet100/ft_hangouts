package com.music42.ft_hangouts.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.music42.ft_hangouts.R
import com.music42.ft_hangouts.db.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private var messages: List<Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutMessage: LinearLayout = view.findViewById(R.id.layoutMessage)
        val textViewMessage: TextView = view.findViewById(R.id.textViewMessage)
        val textViewTime: TextView = view.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.textViewMessage.text = message.body
        holder.textViewTime.text = timeFormat.format(Date(message.timestamp))

        val layoutParams = holder.layoutMessage.layoutParams as FrameLayout.LayoutParams

        if (message.isIncoming) {
            layoutParams.gravity = Gravity.START
            holder.layoutMessage.setBackgroundResource(R.drawable.message_background)
        } else {
            layoutParams.gravity = Gravity.END
            holder.layoutMessage.setBackgroundResource(R.drawable.message_background_outgoing)
        }

        holder.layoutMessage.layoutParams = layoutParams
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
