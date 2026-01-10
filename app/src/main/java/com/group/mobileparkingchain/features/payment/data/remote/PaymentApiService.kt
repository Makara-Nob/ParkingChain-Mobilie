package com.group.mobileparkingchain.features.payment.data.remote

import com.group.mobileparkingchain.features.payment.data.model.ApiResponse
import com.group.mobileparkingchain.features.payment.data.model.CheckPaymentRequest
import com.group.mobileparkingchain.features.payment.data.model.ConfirmPaymentResponse
import com.group.mobileparkingchain.features.payment.data.model.CreatePaymentRequest
import com.group.mobileparkingchain.features.payment.data.model.Payment
import com.group.mobileparkingchain.features.payment.data.model.PaywayStatusData
import com.group.mobileparkingchain.features.payment.data.model.PaymentStatusApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface PaymentApiService {
    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<ApiResponse<Payment>>

    @POST("payments/check-payment")
    suspend fun checkPaymentStatus(@Body request: CheckPaymentRequest): Response<PaymentStatusApiResponse>

    @POST("payments/{paymentId}/confirm")
    suspend fun confirmPayment(@Path("paymentId") paymentId: String): Response<ApiResponse<ConfirmPaymentResponse>>

    @GET("payments/payway/{paymentId}/status")
    suspend fun getPaymentStatus(@Path("paymentId") paymentId: String): Response<ApiResponse<PaywayStatusData>>

    @GET("payments/user/{userId}")
    suspend fun getUserTransactions(@Path("userId") userId: String): Response<ApiResponse<List<Payment>>>

    @GET("payments/me")
    suspend fun getMyTransactions(): Response<ApiResponse<List<Payment>>>
}
