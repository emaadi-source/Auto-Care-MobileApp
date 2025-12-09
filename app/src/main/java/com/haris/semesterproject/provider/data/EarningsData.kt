package com.haris.semesterproject.provider.data

data class EarningsResponse(
    val total_earnings: Double,
    val monthly_earnings: Double,
    val history: List<EarningTransaction>
)

data class EarningTransaction(
    val id: Int,
    val date: String,
    val amount: Double,
    val customer: String,
    val service: String
)