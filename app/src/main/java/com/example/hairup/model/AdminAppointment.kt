package com.example.hairup.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class AdminAppointment(
    val id: Int,
    val clientName: String,
    val clientPhone: String?,
    val serviceName: String,
    val date: String,
    val time: String,
    val stylistId: Int,
    val stylistName: String,
    val status: Int,
    val price: Double,
    val duration: Int
) {
    val isToday: Boolean
        get() = date == LocalDate.now().toString()

    val dateLabel: String
        get() {
            val parts = date.split("-")
            return if (parts.size == 3) "${parts[2]}/${parts[1]}" else date
        }

    val timeLabel: String
        get() = if (time.length >= 5) time.substring(0, 5) else time

    val statusText: String
        get() = when (status) {
            0 -> "Pendiente"
            1 -> "Confirmada"
            2 -> "Completada"
            3 -> "Cancelada"
            else -> "Desconocido"
        }

    val statusColor: Color
        get() = when (status) {
            0 -> Color(0xFFFFC107)  // Amber
            1 -> Color(0xFF4CAF50)  // Green
            2 -> Color(0xFF9E9E9E)  // Gray
            3 -> Color(0xFFE53935)  // Red
            else -> Color(0xFFB0B0B0)
        }

    val isPending: Boolean get() = status == 0
    val isConfirmed: Boolean get() = status == 1
    val isCompleted: Boolean get() = status == 2
    val isCancelled: Boolean get() = status == 3
}