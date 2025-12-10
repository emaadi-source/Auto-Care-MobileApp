package com.haris.semesterproject.provider.data

data class ServiceItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val type: ItemType, // SERVICE or PART
    // Specific to Parts
    val brand: String? = null,
    val stock: Int? = null,
    // Specific to Services
    val duration: String? = null,
    val is_active: Int = 1
)

enum class ItemType { SERVICE, PART }