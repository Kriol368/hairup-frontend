package com.example.hairup.api.models

import com.example.hairup.model.Level
import com.example.hairup.model.User
import com.google.gson.annotations.SerializedName

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

data class AppointmentResponse(
    val id: Int? = null,
    val serviceName: String? = null,
    val serviceId: Int? = null,
    val date: String? = null,
    val time: String? = null,
    val stylistName: String? = null,
    val stylistId: Int? = null,
    val status: Int? = null
)
data class LevelResponse(
    val id: Int,
    val name: String,
    @SerializedName("requiredXp") val requiredXp: Int,
    val reward: String
)

data class LevelsResponse(
    val data: List<LevelResponse>
)

data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val available: Boolean,
    val points: Int,
    @SerializedName("categoryId") val categoryId: Int
) {
    fun toProduct(): com.example.hairup.model.Product {
        return com.example.hairup.model.Product(
            id = id,
            name = name,
            description = description,
            price = price,
            image = image,
            available = available
        )
    }
}

data class ProductsResponse(
    val data: List<ProductResponse>
)