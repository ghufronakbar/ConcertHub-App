package com.example.concerthub.Models


import com.google.gson.annotations.SerializedName

data class ItemEventModel(
    @SerializedName("id_event") val idEvent: Int,
    @SerializedName("organization_name") val organizationName: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("event_image") val eventImage: String,
    @SerializedName("event_type") val eventType: String,
    @SerializedName("event_status") val eventStatus: String
)
