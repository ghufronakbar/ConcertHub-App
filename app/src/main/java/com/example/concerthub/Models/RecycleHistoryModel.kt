package com.example.concerthub.Models

import com.google.gson.annotations.SerializedName

data class RecycleHistoryModel (
    @SerializedName("id_history") val idHistory: Int,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("type_ticket") val typeTicket: String,
    val price: Int,
    val amount: Int,
    val total: Int,
    @SerializedName("unique_code") val uniqueCode: String?,
    @SerializedName("midtrans_snap_token") val midtrans_snap_token: String?,
    val paid: Int,
    val used: Int,
    val datetime: String,
    val fullname: String,
    val email: String,
    val phone: String
)