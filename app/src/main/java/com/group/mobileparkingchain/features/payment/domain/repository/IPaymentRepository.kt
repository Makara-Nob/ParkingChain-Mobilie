package com.group.mobileparkingchain.features.payment.domain.repository

import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.data.model.PaymentStatusData

interface IPaymentRepository {
    suspend fun createPayment(
        bookingId: String,
        amount: Double,
        currency: String = "USD",
        description: String? = null,
        paymentMethod: String
    ): Result<Payment>

    suspend fun checkPaymentStatus(md5: String): Result<PaymentStatusData>

    suspend fun confirmPayment(paymentId: String): Result<Boolean>
    
    suspend fun getUserTransactions(userId: String): Result<List<Payment>>
}
