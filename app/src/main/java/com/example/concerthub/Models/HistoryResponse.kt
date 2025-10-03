package com.example.concerthub.Models

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @SerializedName("id_history") val idHistory: Int,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("type_ticket") val typeTicket: String,
    @SerializedName("price") val price: Int,
    @SerializedName("amount") val amount: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("unique_code") val uniqueCode: String?,
    @SerializedName("paid") val paid: Int,
    @SerializedName("used") val used: Int,
    @SerializedName("datetime") val datetime: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("midtrans_order_id") val midtransOrderId: String?,
    @SerializedName("midtrans_snap_token") val midtransSnapToken: String?,
    @SerializedName("midtrans_redirect_url") val midtransRedirectUrl: String?
)
