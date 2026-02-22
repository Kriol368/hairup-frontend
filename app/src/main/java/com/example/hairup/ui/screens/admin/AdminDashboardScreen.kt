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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.model.mockStylists
import com.example.hairup.model.mockStylistAppointments

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val AmberYellow = Color(0xFFFFC107)
private val BlueAccent = Color(0xFF64B5F6)

@Composable
fun AdminDashboardScreen(stylistId: Int = 0) {
    val stylist = mockStylists.find { it.id == stylistId } ?: mockStylists.first()
    val isGenericAdmin = stylistId == 0

    // Citas filtradas para este peluquero (o todas si es admin general)
    val allAppointments = remember(stylistId) {
        if (isGenericAdmin) mockStylistAppointments
        else mockStylistAppointments.filter { it.stylistId == stylistId }
    }
    val todayAppointments = remember(allAppointments) {
        allAppointments.filter { it.isToday }
    }
    val pendingCount = remember(todayAppointments) {
        todayAppointments.count { !it.confirmed }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header card personalizado
        DashboardHeaderCard(
            name = stylist.name,
            specialty = stylist.specialty,
            isGenericAdmin = isGenericAdmin
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isGenericAdmin) "Resumen general de hoy" else "Tu resumen de hoy",
            style = MaterialTheme.typography.titleMedium,
            color = Gold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // KPIs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange,
                iconColor = Gold,
                label = "Citas hoy",
                value = "${todayAppointments.size}",
                subtitle = if (isGenericAdmin) "en el salón" else "tuyas"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconColor = AmberYellow,
                label = "Pendientes",
                value = "$pendingCount",
                subtitle = "por confirmar"
            )
            if (isGenericAdmin) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Group,
                    iconColor = BlueAccent,
                    label = "Peluqueros",
                    value = "${mockStylists.size - 1}",
                    subtitle = "activos hoy"
                )
            } else {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Star,
                    iconColor = GoldLight,
                    label = "Confirmadas",
                    value = "${todayAppointments.count { it.confirmed }}",
                    subtitle = "listas para hoy"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de citas de hoy
        if (todayAppointments.isNotEmpty()) {
            Text(
                text = if (isGenericAdmin) "Citas de hoy (todas)" else "Tus citas de hoy",
                style = MaterialTheme.typography.titleMedium,
                color = Gold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            todayAppointments.forEach { appt ->
                MiniAppointmentRow(
                    clientName = appt.clientName,
                    serviceName = appt.serviceName,
                    time = appt.time,
                    confirmed = appt.confirmed
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes citas para hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DashboardHeaderCard(
    name: String,
    specialty: String,
    isGenericAdmin: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GoldDark, Gold, GoldLight, Gold, GoldDark)
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGenericAdmin) Icons.Default.Settings else Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Hola, $name",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = specialty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Dom, 22 Feb 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextGray)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun MiniAppointmentRow(
    clientName: String,
    serviceName: String,
    time: String,
    confirmed: Boolean
) {
    val statusColor = if (confirmed) GreenConfirmed else AmberYellow
    val statusLabel = if (confirmed) "Confirmada" else "Pendiente"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.width(52.dp)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(36.dp)
                    .background(Gold.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
                Text(
                    text = serviceName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
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
                    fontSize = 10.sp
                )
            }
        }
    }
}
