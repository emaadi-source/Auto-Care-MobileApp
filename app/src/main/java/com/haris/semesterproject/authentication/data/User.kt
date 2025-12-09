package com.haris.semesterproject.authentication.data

data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone: String,
    val role: String
)