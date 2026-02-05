package com.music42.ft_hangouts.db

data class Message(
    val id: Long = 0,
    val contactId: Long,
    val body: String,
    val isIncoming: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
