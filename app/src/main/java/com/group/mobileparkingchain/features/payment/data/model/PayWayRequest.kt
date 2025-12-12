package com.group.mobileparkingchain.features.payment.data.model

import com.google.gson.annotations.SerializedName

data class PayWayRequest(
    @SerializedName("req_time") val reqTime: String,
    @SerializedName("merchant_id") val merchantId: String,
    @SerializedName("tran_id") val tranId: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("items") val items: String, // Base64 encoded JSON array of items
    @SerializedName("shipping") val shipping: String = "0",
    @SerializedName("firstname") val firstName: String,
    @SerializedName("lastname") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("type") val type: String = "purchase",
    @SerializedName("payment_option") val paymentOption: String = "abapay_deeplink",
    @SerializedName("return_url") val returnUrl: String, // Base64 encoded
    @SerializedName("continue_success_url") val continueSuccessUrl: String? = null,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("hash") val hash: String
)
