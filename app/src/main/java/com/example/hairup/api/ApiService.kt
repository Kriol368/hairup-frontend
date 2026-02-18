package com.example.hairup.api

import com.example.hairup.api.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    @GET("api/user/profile")
    fun getProfile(@Header("Authorization") token: String): Call<UserResponse>

    @PUT("api/user/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<UserResponse>

    @POST("api/user/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<Map<String, String>>
}