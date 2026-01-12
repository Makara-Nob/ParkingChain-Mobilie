package com.group.mobileparkingchain.utils

import java.util.Locale

object Formatters {
    fun currency(amount: Double, currency: String, decimals: Int = 2): String {
        val fmt = "%,.${decimals}f"
        return String.format(Locale.getDefault(), fmt, amount) + " " + currency
    }

    fun moneyWithSymbol(amount: Double, symbol: String, decimals: Int = 2): String {
        return symbol + String.format(Locale.getDefault(), "%,.${decimals}f", amount)
    }
}
