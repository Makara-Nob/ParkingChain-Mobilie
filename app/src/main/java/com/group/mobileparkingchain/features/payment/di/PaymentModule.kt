package com.group.mobileparkingchain.features.payment.di

import android.content.Context
import com.group.mobileparkingchain.features.payment.data.repository.PaymentRepository
import com.group.mobileparkingchain.network.RetrofitInstance

class PaymentModule(private val context: Context) {
    val paymentRepository: PaymentRepository by lazy {
        PaymentRepository(RetrofitInstance.paymentApi)
    }
}
