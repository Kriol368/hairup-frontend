package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
private val RedCancel = Color(0xFFE53935)
private val LeatherBrown = Color(0xFF8B5E3C)
private val BlueAccent = Color(0xFF64B5F6)

private data class AppUser(
    val id: Int,
    val name: String,
    val email: String,
    val xp: Int,
    val level: String,
    val isAdmin: Boolean,
    val isActive: Boolean,
    val totalBookings: Int
)

private val mockUsers = listOf(
    // ── Peluqueros / Admins (de mockStylists) ──────────────────
    AppUser(1,  "Admin Principal",    "admin@hairup.com",   0,    "-",      isAdmin = true,  isActive = true,  totalBookings = 0),
    AppUser(2,  "Ana García",         "ana@hairup.com",     0,    "-",      isAdmin = true,  isActive = true,  totalBookings = 0),
    AppUser(3,  "Carlos López",       "carlos@hairup.com",  0,    "-",      isAdmin = true,  isActive = true,  totalBookings = 0),
    AppUser(4,  "Laura Martín",       "laura@hairup.com",   0,    "-",      isAdmin = true,  isActive = true,  totalBookings = 0),
    AppUser(5,  "Diego Ruiz",         "diego@hairup.com",   0,    "-",      isAdmin = true,  isActive = true,  totalBookings = 0),
    // ── Clientes ───────────────────────────────────────────────
    AppUser(6,  "María García",       "maria@email.com",    2350, "Platino", isAdmin = false, isActive = true,  totalBookings = 24),
    AppUser(7,  "Carlos López",       "carlos@email.com",   1450, "Oro",     isAdmin = false, isActive = true,  totalBookings = 15),
    AppUser(8,  "Ana Martínez",       "ana@email.com",      1100, "Oro",     isAdmin = false, isActive = true,  totalBookings = 11),
    AppUser(9,  "Diego Ruiz",         "diego@email.com",    620,  "Plata",   isAdmin = false, isActive = true,  totalBookings = 7),
    AppUser(10, "Laura Sánchez",      "laura@email.com",    480,  "Plata",   isAdmin = false, isActive = false, totalBookings = 5),
    AppUser(11, "Pedro Torres",       "pedro@email.com",    180,  "Bronce",  isAdmin = false, isActive = true,  totalBookings = 2),
    AppUser(12, "Sofía Fernández",    "sofia@email.com",    90,   "Bronce",  isAdmin = false, isActive = false, totalBookings = 1),
    AppUser(13, "Javier Gómez",       "javier@email.com",   1200, "Oro",     isAdmin = false, isActive = true,  totalBookings = 13),
    AppUser(14, "Lucía Herrera",      "lucia@email.com",    550,  "Plata",   isAdmin = false, isActive = true,  totalBookings = 6),
    AppUser(15, "Marcos Díaz",        "marcos@email.com",   230,  "Bronce",  isAdmin = false, isActive = true,  totalBookings = 3)
)

private sealed class UserAction {
    data class ToggleAdmin(val user: AppUser) : UserAction()
    data class ToggleActive(val user: AppUser) : UserAction()
}

@Composable
fun AdminUsersScreen() {
    var users by remember { mutableStateOf(mockUsers) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var pendingAction by remember { mutableStateOf<UserAction?>(null) }

    val filterOptions = listOf(
        "Todos" to users.size,
        "Clientes" to users.count { !it.isAdmin },
        "Admins" to users.count { it.isAdmin },
        "Deshabilitados" to users.count { !it.isActive }
    )

    val filteredUsers = remember(searchQuery, selectedFilter, users) {
        users
            .filter { user ->
                when (selectedFilter) {
                    "Clientes" -> !user.isAdmin
                    "Admins" -> user.isAdmin
                    "Deshabilitados" -> !user.isActive
                    else -> true
                }
            }
            .filter { user ->
                searchQuery.isBlank() ||
                        user.name.contains(searchQuery, ignoreCase = true) ||
                        user.email.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Título + contador
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Usuarios",
                style = MaterialTheme.typography.titleMedium,
                color = Gold
            )
            Text(
                text = "${users.size} registrados",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por nombre o email...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = LeatherBrown,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = Gold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Chips de filtro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { (label, count) ->
                val isSelected = selectedFilter == label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Gold else DarkGray)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedFilter = label }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "$label ($count)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CarbonBlack else TextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de usuarios
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ManageAccounts,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No se encontraron usuarios",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextGray
                        )
                    }
                }
            } else {
                filteredUsers.forEach { user ->
                    UserCard(
                        user = user,
                        onToggleAdmin = { pendingAction = UserAction.ToggleAdmin(user) },
                        onToggleActive = { pendingAction = UserAction.ToggleActive(user) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Diálogo de confirmación
    pendingAction?.let { action ->
        val isGivingAdmin = action is UserAction.ToggleAdmin && !(action as UserAction.ToggleAdmin).user.isAdmin
        val isEnabling = action is UserAction.ToggleActive && !(action as UserAction.ToggleActive).user.isActive

        val title: String
        val message: String
        val confirmLabel: String
        val btnColor: Color

        when (action) {
            is UserAction.ToggleAdmin -> {
                title = if (action.user.isAdmin) "Quitar privilegios de admin" else "Dar privilegios de admin"
                message = if (action.user.isAdmin)
                    "¿Quitar los privilegios de administrador a ${action.user.name}? Pasará a ser un cliente normal."
                else
                    "¿Dar privilegios de administrador a ${action.user.name}? Tendrá acceso completo al panel de admin."
                confirmLabel = if (action.user.isAdmin) "Sí, quitar admin" else "Sí, dar admin"
                btnColor = if (action.user.isAdmin) RedCancel else Gold
            }
            is UserAction.ToggleActive -> {
                title = if (action.user.isActive) "Deshabilitar usuario" else "Habilitar usuario"
                message = if (action.user.isActive)
                    "¿Deshabilitar la cuenta de ${action.user.name}? No podrá acceder a la app."
                else
                    "¿Habilitar la cuenta de ${action.user.name}? Recuperará el acceso a la app."
                confirmLabel = if (action.user.isActive) "Sí, deshabilitar" else "Sí, habilitar"
                btnColor = if (action.user.isActive) RedCancel else GreenConfirmed
            }
        }

        val btnTextColor = if (btnColor == Gold) CarbonBlack else White

        AlertDialog(
            onDismissRequest = { pendingAction = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = when (action) {
                            is UserAction.ToggleAdmin -> action.user.id
                            is UserAction.ToggleActive -> action.user.id
                        }
                        users = users.map { u ->
                            if (u.id == targetId) when (action) {
                                is UserAction.ToggleAdmin -> u.copy(isAdmin = !u.isAdmin)
                                is UserAction.ToggleActive -> u.copy(isActive = !u.isActive)
                            } else u
                        }
                        pendingAction = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnColor,
                        contentColor = btnTextColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(confirmLabel, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("Cancelar", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun UserCard(
    user: AppUser,
    onToggleAdmin: () -> Unit,
    onToggleActive: () -> Unit
) {
    val avatarColor = when {
        !user.isActive -> TextGray.copy(alpha = 0.5f)
        user.isAdmin -> Gold
        else -> BlueAccent
    }
    val nameColor = if (user.isActive) White else TextGray

    val initials = user.name
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    val levelColor = when (user.level) {
        "Platino" -> Color(0xFFB9F2FF)
        "Oro" -> Color(0xFFFFD700)
        "Plata" -> Color(0xFFC0C0C0)
        "Bronce" -> Color(0xFFCD7F32)
        else -> TextGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (user.isActive) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila superior: avatar + info + badges
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = avatarColor
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                // Badges rol + estado
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleBadge(isAdmin = user.isAdmin)
                    StatusBadge(isActive = user.isActive)
                }
            }

            // Info extra para clientes
            if (!user.isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(levelColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = user.level,
                            style = MaterialTheme.typography.labelSmall,
                            color = levelColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "${user.xp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${user.totalBookings} citas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))

            // Acciones
            if (user.id == 1) {
                // Admin principal — protegido
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Gold.copy(alpha = 0.07f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cuenta de administrador principal — no modificable",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Botón dar/quitar admin
                    Button(
                        onClick = onToggleAdmin,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isAdmin) RedCancel.copy(alpha = 0.12f) else Gold.copy(alpha = 0.15f),
                            contentColor = if (user.isAdmin) RedCancel else Gold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isAdmin) Icons.Default.ShieldMoon else Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isAdmin) "Quitar admin" else "Dar admin",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    // Botón habilitar/deshabilitar
                    Button(
                        onClick = onToggleActive,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isActive) RedCancel.copy(alpha = 0.12f) else GreenConfirmed.copy(alpha = 0.15f),
                            contentColor = if (user.isActive) RedCancel else GreenConfirmed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isActive) "Deshabilitar" else "Habilitar",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(isAdmin: Boolean) {
    val color = if (isAdmin) Gold else BlueAccent
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (isAdmin) "Admin" else "Cliente",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    val color = if (isActive) GreenConfirmed else RedCancel
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (isActive) "Activo" else "Deshabilitado",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}
