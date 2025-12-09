package com.haris.semesterproject.authentication.data

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val user: User?
)