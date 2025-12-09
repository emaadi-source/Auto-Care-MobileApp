package com.haris.semesterproject.customer.data

data class Booking(
    val id: String,
    val workshopName: String,
    val status: String,
    val date: String,
    val time: String,
    val address: String,
    val services: List<String>,
    val price: String
)
