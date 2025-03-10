package com.example.eventease.Models


import com.google.gson.annotations.SerializedName

data class EventDetailResponse(
    @SerializedName("id_event") val idEvent: Int,
    @SerializedName("id_organization") val idOrganization: Int,
    @SerializedName("organization_name") val organizationName: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("url_google_map") val urlGoogleMap: String,
    @SerializedName("event_image") val eventImage: String,
    @SerializedName("site_plan_image") val sitePlanImage: String,
    @SerializedName("status") val status: String,
    @SerializedName("payment_information") val paymentInformation: String,
    @SerializedName("event_start") val eventStart: String,
    @SerializedName("event_end") val eventEnd: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("total_type") val totalType: Int,
    @SerializedName("total_ticket") val totalTicket: Int,
    @SerializedName("total_sold") val totalSold: Int,
    @SerializedName("tickets") val tickets: List<Ticket>
)

data class Ticket(
    @SerializedName("id_ticket") val idTicket: Int,
    @SerializedName("ticket_type") val ticketType: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("sold") val sold: Int,
    @SerializedName("price") val price: Int,
    @SerializedName("date_start") val dateStart: String,
    @SerializedName("date_end") val dateEnd: String
)