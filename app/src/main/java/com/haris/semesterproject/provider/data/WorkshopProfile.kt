package com.haris.semesterproject.provider.data

import java.io.Serializable

data class WorkshopProfile(
    val workshop_name: String?,
    val owner_name: String?,
    val description: String?,
    val address: String?,
    val city: String?,
    val pincode: String?,
    val contact_email: String?,
    val contact_phone: String?,
    val image_1: String?, // Base64 String
    val image_2: String?  // Base64 String
) : Serializable

data class ProfileResponse(
    val error: Boolean,
    val data: WorkshopProfile?
)