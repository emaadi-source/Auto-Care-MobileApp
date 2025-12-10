package com.haris.semesterproject.customer.data

import com.google.gson.annotations.SerializedName

data class BookingItem(
    @SerializedName("booking_id") val bookingId: String,
    @SerializedName("provider_id") val providerId: String,
    @SerializedName("total_price") val totalPrice: String,
    @SerializedName("booking_date") val bookingDate: String,
    @SerializedName("booking_time") val bookingTime: String?,
    @SerializedName("status") val status: String,
    @SerializedName("workshop_name") val workshopName: String?,
    @SerializedName("vehicle_model") val vehicleModel: String?,
    @SerializedName("vehicle_number") val vehicleNumber: String?,
    @SerializedName("problem_description") val problemDescription: String?
)
