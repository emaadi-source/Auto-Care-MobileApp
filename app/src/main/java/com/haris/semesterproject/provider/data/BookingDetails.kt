package com.haris.semesterproject.provider.data

import java.io.Serializable

data class BookingDetails(
    val id: Int,
    val customer_id: Int,
    val booking_date: String,
    val total: String,
    var status: String, // Changed to 'var' so we can update it locally
    val vehicle_model: String?,
    val vehicle_number: String?,
    val problem_description: String?,
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val main_service: String?
) : Serializable