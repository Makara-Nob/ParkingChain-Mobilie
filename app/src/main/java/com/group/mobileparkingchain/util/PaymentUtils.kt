package com.group.mobileparkingchain.util

object PaymentUtils {

    fun formatCardNumber(input: String): String {
        val digits = input.filter { it.isDigit() }.take(16)
        return digits.chunked(4).joinToString(" ")
    }

    fun formatExpiryDate(input: String): String {
        val digits = input.filter { it.isDigit() }.take(4)
        return when {
            digits.length >= 3 -> digits.substring(0, 2) + "/" + digits.substring(2)
            digits.length >= 1 -> digits
            else -> ""
        }
    }
}
