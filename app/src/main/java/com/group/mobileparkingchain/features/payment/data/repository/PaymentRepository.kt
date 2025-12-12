package com.group.mobileparkingchain.features.payment.data.repository

import com.group.mobileparkingchain.features.payment.data.model.ApiResponse
import com.group.mobileparkingchain.features.payment.data.model.CheckPaymentRequest
import com.group.mobileparkingchain.features.payment.data.model.CreatePaymentRequest
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.data.model.PaymentStatusData
import com.group.mobileparkingchain.features.payment.data.remote.PaymentApiService
import com.group.mobileparkingchain.features.payment.domain.repository.IPaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PaymentRepository(
    private val paymentApiService: PaymentApiService
) : IPaymentRepository {

    override suspend fun createPayment(
        bookingId: String,
        amount: Double,
        currency: String,
        description: String?,
        paymentMethod: String
    ): Result<Payment> = safeApiCall {
        paymentApiService.createPayment(
            CreatePaymentRequest(
                bookingId = bookingId,
                amount = amount,
                currency = currency,
                description = description,
                paymentMethod = paymentMethod
            )
        )
    }.mapCatching { it.data }

    override suspend fun checkPaymentStatus(md5: String): Result<PaymentStatusData> = safeApiCall {
        paymentApiService.checkPaymentStatus(CheckPaymentRequest(md5))
    }.mapCatching { 
        it.data ?: throw Exception("Payment status data is missing")
    }

    override suspend fun confirmPayment(paymentId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = paymentApiService.confirmPayment(paymentId)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Failed to confirm payment"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserTransactions(userId: String): Result<List<Payment>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = paymentApiService.getUserTransactions(userId)
                if (response.isSuccessful) {
                    val payments = response.body()
                    if (payments != null) {
                        Result.success(payments)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Failed to fetch transactions"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Unknown error"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(e.message()))
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
