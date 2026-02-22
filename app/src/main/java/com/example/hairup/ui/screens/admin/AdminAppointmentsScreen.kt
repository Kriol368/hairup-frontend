package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val AmberYellow = Color(0xFFFFC107)
private val RedCancel = Color(0xFFE53935)

private data class AdminAppointment(
    val id: Int,
    val clientName: String,
    val serviceName: String,
    val dateLabel: String,
    val time: String,
    val duration: Int,
    val price: Double,
    val confirmed: Boolean = false
)

private val mockToday = listOf(
    AdminAppointment(1, "Ana García", "Corte y Color", "Hoy, 22 Feb", "10:00", 90, 45.0, confirmed = true),
    AdminAppointment(2, "Carlos López", "Tinte", "Hoy, 22 Feb", "12:30", 60, 35.0),
    AdminAppointment(3, "Laura Martín", "Tratamiento capilar", "Hoy, 22 Feb", "15:00", 45, 25.0, confirmed = true),
    AdminAppointment(4, "Diego Ruiz", "Corte de pelo", "Hoy, 22 Feb", "17:30", 30, 15.0)
)

private val mockUpcoming = listOf(
    AdminAppointment(5, "María Sánchez", "Corte y Barba", "Lun, 23 Feb", "09:00", 40, 22.0),
    AdminAppointment(6, "Javier Torres", "Alisado", "Lun, 23 Feb", "11:00", 90, 40.0, confirmed = true),
    AdminAppointment(7, "Sofía Ruiz", "Coloración", "Mar, 24 Feb", "16:00", 120, 55.0),
    AdminAppointment(8, "Pedro Gómez", "Corte de pelo", "Mié, 25 Feb", "10:30", 30, 15.0),
    AdminAppointment(9, "Lucía Fernández", "Tinte + Mechas", "Jue, 26 Feb", "14:00", 150, 70.0)
)

@Composable
fun AdminAppointmentsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Hoy", "Próximas")

    var todayList by remember { mutableStateOf(mockToday) }
    var upcomingList by remember { mutableStateOf(mockUpcoming) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var targetAppointment by remember { mutableStateOf<AdminAppointment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkGray,
            contentColor = Gold,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Gold
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Gold else TextGray
                        )
                    }
                )
            }
        }

        val currentList = if (selectedTab == 0) todayList else upcomingList

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            currentList.forEach { appointment ->
                AdminAppointmentCard(
                    appointment = appointment,
                    onConfirmClick = {
                        targetAppointment = appointment
                        showConfirmDialog = true
                    },
                    onCancelClick = {
                        targetAppointment = appointment
                        showCancelDialog = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showConfirmDialog && targetAppointment != null) {
        val appt = targetAppointment!!
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false; targetAppointment = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Confirmar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Confirmar la cita de ${appt.clientName} a las ${appt.time}?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = appt.id
                        if (selectedTab == 0) {
                            todayList = todayList.map { if (it.id == id) it.copy(confirmed = true) else it }
                        } else {
                            upcomingList = upcomingList.map { if (it.id == id) it.copy(confirmed = true) else it }
                        }
                        showConfirmDialog = false
                        targetAppointment = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenConfirmed,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false; targetAppointment = null }) {
                    Text("Volver", color = Gold)
                }
            }
        )
    }

    if (showCancelDialog && targetAppointment != null) {
        val appt = targetAppointment!!
        AlertDialog(
            onDismissRequest = { showCancelDialog = false; targetAppointment = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Cancelar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Cancelar la cita de ${appt.clientName}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = appt.id
                        if (selectedTab == 0) {
                            todayList = todayList.filter { it.id != id }
                        } else {
                            upcomingList = upcomingList.filter { it.id != id }
                        }
                        showCancelDialog = false
                        targetAppointment = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false; targetAppointment = null }) {
                    Text("No, volver", color = Gold)
                }
            }
        )
    }
}

@Composable
private fun AdminAppointmentCard(
    appointment: AdminAppointment,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val statusColor = if (appointment.confirmed) GreenConfirmed else AmberYellow
    val statusIcon = if (appointment.confirmed) Icons.Default.CheckCircle else Icons.Default.Schedule
    val statusLabel = if (appointment.confirmed) "Confirmada" else "Pendiente"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusLabel,
                        tint = statusColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = appointment.serviceName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldLight
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = appointment.time,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Text(
                        text = appointment.dateLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${appointment.duration} min · ${appointment.price.toInt()}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!appointment.confirmed) {
                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenConfirmed.copy(alpha = 0.15f),
                            contentColor = GreenConfirmed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar", fontWeight = FontWeight.Medium)
                    }
                }
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel.copy(alpha = 0.12f),
                        contentColor = RedCancel
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
