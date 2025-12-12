package com.group.mobileparkingchain.features.payment.data.model

import com.google.gson.annotations.SerializedName

data class PayWayResponse(
    @SerializedName("status") val status: PayWayStatus,
    @SerializedName("description") val description: String?,
    @SerializedName("abapay_deeplink") val abapayDeeplink: String?,
    @SerializedName("qr_string") val qrString: String?,
    @SerializedName("app_store") val appStore: String?,
    @SerializedName("play_store") val playStore: String?
)

data class PayWayStatus(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("tran_id") val tranId: String?
)
