package com.haris.semesterproject.customer.data

data class NewBooking(
    val bookingId: String,
    val workshopName: String,
    val status: String,
    val date: String,
    val time: String,
    val address: String, // add this
    val city: String,    // add this
    val services: List<String>,
    val price: String
)
