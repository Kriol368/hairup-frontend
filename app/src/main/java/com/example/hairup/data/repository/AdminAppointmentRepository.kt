package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AdminAppointmentsResponse
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.api.models.UpdateAppointmentRequest
import com.example.hairup.model.AdminAppointment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAppointmentRepository {

    private val TAG = "AdminAppointmentRepository"

    fun getAllAppointments(
        token: String,
        callback: (Result<List<AdminAppointment>>) -> Unit
    ) {
        Log.d(TAG, "Obteniendo todas las citas")

        RetrofitClient.apiService.getAllAppointments("Bearer $token").enqueue(object : Callback<AdminAppointmentsResponse> {
            override fun onResponse(
                call: Call<AdminAppointmentsResponse>,
                response: Response<AdminAppointmentsResponse>
            ) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.data?.map { it.toAdminAppointment() } ?: emptyList()
                    callback(Result.success(appointments))
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: "Error ${response.code()}"
                    } catch (e: Exception) {
                        "Error ${response.code()}"
                    }
                    callback(Result.failure(Exception(errorMsg)))
                }
            }

            override fun onFailure(call: Call<AdminAppointmentsResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun updateAppointmentStatus(
        token: String,
        appointmentId: Int,
        status: Int,
        callback: (Result<AppointmentResponse>) -> Unit
    ) {
        Log.d(TAG, "Actualizando cita $appointmentId a estado $status")

        val request = UpdateAppointmentRequest(status = status)

        RetrofitClient.apiService.updateAppointment("Bearer $token", appointmentId, request).enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(
                call: Call<AppointmentResponse>,
                response: Response<AppointmentResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Respuesta vacía")))
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
        Log.d(TAG, "Cancelando cita $appointmentId")

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