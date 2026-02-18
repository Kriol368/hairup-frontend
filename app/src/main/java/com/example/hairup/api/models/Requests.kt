package com.example.hairup.api.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)