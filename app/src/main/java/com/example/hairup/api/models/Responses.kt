package com.example.hairup.api.models

import com.example.hairup.model.User

data class LoginResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val email: String,
    val name: String,
    val xp: Int,
    val admin: Boolean,
    val phone: String,
    val created: String,
    val levelId: Int
) {
    fun toUser(): User = User(
        id = id,
        email = email,
        name = name,
        xp = xp,
        levelId = levelId,
        phone = phone,
        isAdmin = admin,
        password = ""
    )
}