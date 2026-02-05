package com.music42.ft_hangouts.db

data class Contact(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = ""
) {
    fun getDisplayName(): String {
        val name = "$firstName $lastName".trim()
        return if (name.isNotEmpty()) name else phone
    }

    fun getInitial(): String {
        return when {
            firstName.isNotEmpty() -> firstName.first().uppercaseChar().toString()
            lastName.isNotEmpty() -> lastName.first().uppercaseChar().toString()
            phone.isNotEmpty() -> phone.first().toString()
            else -> "?"
        }
    }
}
