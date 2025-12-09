package com.haris.semesterproject.customer.data

data class ServiceWhole(
    val id: Int,
    val name: String,
    val price: Double,
    val type: String,   // SERVICE or PART
    val brand: String
)
