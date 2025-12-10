package com.haris.semesterproject.customer.data

data class BookingResponseNew(
    val success: Boolean,
    val bookings: List<BookingItem>
)
