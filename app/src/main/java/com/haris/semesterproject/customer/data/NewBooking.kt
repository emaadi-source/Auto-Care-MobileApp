package com.haris.semesterproject.customer.data

data class NewBooking(
    val bookingId: String,
    val workshopName: String?,  // make nullable
    val status: String,
    val date: String,
    val time: String,
    val address: String,
    val services: List<String>,
    val price: String
)
