package com.example.concerthub.Models

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val message: String,
    val currUser: Int
)