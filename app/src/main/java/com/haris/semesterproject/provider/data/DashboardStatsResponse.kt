package com.haris.semesterproject.provider.data

data class DashboardStatsResponse(
    val error: Boolean,
    val total_bookings: Int,
    val earnings: Double,
    val pending_today: Int,
    val completed_today: Int
)