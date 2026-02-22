package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.api.models.AppointmentsResponse
import com.example.hairup.api.models.CreateAppointmentRequest
import com.example.hairup.api.models.UpdateAppointmentRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentRepository {

    private val TAG = "AppointmentRepository"

    fun getUserAppointments(token: String, callback: (Result<List<AppointmentResponse>>) -> Unit) {
        Log.d(TAG, "Obteniendo todas las citas")

        RetrofitClient.apiService.getUserAppointments("Bearer $token").enqueue(object : Callback<AppointmentsResponse> {
            override fun onResponse(
                call: Call<AppointmentsResponse>,
                response: Response<AppointmentsResponse>
            ) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.data ?: emptyList()
                    callback(Result.success(appointments))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<AppointmentsResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun createAppointment(
        token: String,
        serviceId: Int,
        date: String,
        time: String,
        barberId: Int,
        callback: (Result<AppointmentResponse>) -> Unit
    ) {
        val request = CreateAppointmentRequest(serviceId, date, time, barberId)

        RetrofitClient.apiService.createAppointment("Bearer $token", request).enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(
                call: Call<AppointmentResponse>,
                response: Response<AppointmentResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                        ?: callback(Result.failure(Exception("Respuesta vacía")))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun cancelAppointment(
        token: String,
        appointmentId: Int,
        callback: (Result<Boolean>) -> Unit
    ) {
        val request = UpdateAppointmentRequest(status = 3)

        RetrofitClient.apiService.updateAppointment("Bearer $token", appointmentId, request).enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(
                call: Call<AppointmentResponse>,
                response: Response<AppointmentResponse>
            ) {
                if (response.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun deleteAppointment(
        token: String,
        appointmentId: Int,
        callback: (Result<Boolean>) -> Unit
    ) {
        RetrofitClient.apiService.deleteAppointment("Bearer $token", appointmentId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                if (response.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}