package com.example.concerthub.Models

import com.google.gson.annotations.SerializedName

data class TicketResponse(
    @SerializedName("id_ticket") val idTicket: Int,
    @SerializedName("ticket_type") val ticketType: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("sold") val sold: Int,
    @SerializedName("price") val price: Int,
    @SerializedName("date_start") val dateStart: String,
    @SerializedName("date_end") val dateEnd: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("organization_name") val organizationName: String
)
