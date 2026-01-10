package com.group.mobileparkingchain.features.payment.data.model

enum class PaymentStatus { PENDING, PAID, FAILED, CANCELLED, REFUNDED, EXPIRED }

enum class Currency { USD, KHR }

// Payment methods supported by the API
object PaymentMethod {
    const val ABA = "aba"
    const val KHQR = "payway"
    const val CASH = "cash"
    const val CARD = "card"
}

// Payment entity returned by API
data class Payment(
    val paymentId: String,
    val qrString: String,
    val qrImage: String? = null,
    val deeplinkUrl: String,
    val md5: String? = null,
    val amount: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)

// Generic API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

// Transaction data returned when payment is verified
data class TransactionData(
    val hash: String,
    val fromAccountId: String,
    val toAccountId: String,
    val currency: String,
    val amount: Double,
    val description: String
)

// Payment status check response
data class PaymentStatusData(
    val paymentId: String,
    val status: String,
    val transactionData: TransactionData?,
    val verifiedAt: String?
)

data class PaymentStatusApiResponse(
    val success: Boolean,
    val message: String,
    val data: PaymentStatusData?
)

data class PaywayStatusData(
    val paymentId: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val paidAt: String?,
    val createdAt: String?,
    val expiresAt: String?
)

// Request models
data class CreatePaymentRequest(
    val bookingId: String,
    val amount: Double,
    val currency: String = "USD",
    val description: String? = null,
    val paymentMethod: String = PaymentMethod.KHQR,
    val qrImageTemplate: String? = "template3_color"
)

data class CheckPaymentRequest(
    val md5: String
)

data class ConfirmPaymentResponse(
    val bookingUpdated: Boolean
)
