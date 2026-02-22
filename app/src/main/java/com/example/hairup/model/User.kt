package com.example.hairup.model

data class User(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val xp: Int = 0,
    val points: Int = 0,
    val levelId: Int = 1,
    val phone: String = "",
    val isAdmin: Boolean = false
)