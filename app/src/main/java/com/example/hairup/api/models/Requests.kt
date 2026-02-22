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

data class CreateAppointmentRequest(
    val serviceId: Int,
    val date: String,
    val time: String,
    val barberId: Int
)

data class UpdateAppointmentRequest(
    val date: String? = null,
    val time: String? = null,
    val status: Int? = null
)

data class RedeemRequest(
    val rewardId: Int
)

data class PurchaseItem(
    val productId: Int,
    val quantity: Int
)

data class PurchaseRequest(
    val items: List<PurchaseItem>
)