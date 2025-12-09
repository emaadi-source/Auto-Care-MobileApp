package com.haris.semesterproject.provider.data

data class Booking(
    val name: String,
    val service: String,
    val price: String,
    val status: String // "pending" or "confirmed"
)