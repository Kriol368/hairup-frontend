package com.example.hairup.model

data class Stylist(
    val id: Int,
    val name: String,
    val email: String,
    val specialty: String
)

data class StylistAppointment(
    val id: Int,
    val clientName: String,
    val serviceName: String,
    val dateLabel: String,
    val time: String,
    val duration: Int,
    val price: Double,
    val confirmed: Boolean,
    val stylistId: Int,
    val isToday: Boolean
)

// Contraseña mock: cualquier valor sirve (sin backend)
// Para entrar como peluquero usar estos emails
val mockStylists = listOf(
    Stylist(0, "Admin Principal", "admin@hairup.com", "Administración general"),
    Stylist(1, "Ana García", "ana@hairup.com", "Coloración y Mechas"),
    Stylist(2, "Carlos López", "carlos@hairup.com", "Corte y Barba"),
    Stylist(3, "Laura Martín", "laura@hairup.com", "Tratamientos Capilares"),
    Stylist(4, "Diego Ruiz", "diego@hairup.com", "Corte Masculino")
)

val mockStylistAppointments = listOf(
    // ── Ana García (id=1) ──────────────────────────────────────
    StylistAppointment(1,  "María García",   "Corte y Color",   "Hoy, 22 Feb",   "10:00", 90,  45.0, confirmed = true,  stylistId = 1, isToday = true),
    StylistAppointment(2,  "Carlos López",   "Tinte",           "Hoy, 22 Feb",   "12:30", 60,  35.0, confirmed = false, stylistId = 1, isToday = true),
    StylistAppointment(3,  "Laura Sánchez",  "Mechas",          "Hoy, 22 Feb",   "15:00", 120, 65.0, confirmed = true,  stylistId = 1, isToday = true),
    StylistAppointment(4,  "Sofía Fernández","Coloración",      "Lun, 23 Feb",   "09:00", 120, 55.0, confirmed = false, stylistId = 1, isToday = false),
    StylistAppointment(5,  "Lucía Herrera",  "Tinte + Mechas",  "Mar, 24 Feb",   "14:00", 150, 70.0, confirmed = true,  stylistId = 1, isToday = false),
    StylistAppointment(6,  "Ana Martínez",   "Balayage",        "Jue, 26 Feb",   "11:00", 180, 90.0, confirmed = false, stylistId = 1, isToday = false),

    // ── Carlos López (id=2) ────────────────────────────────────
    StylistAppointment(7,  "Pedro Torres",   "Corte y Barba",   "Hoy, 22 Feb",   "09:30", 40,  22.0, confirmed = true,  stylistId = 2, isToday = true),
    StylistAppointment(8,  "Diego Ruiz",     "Corte de pelo",   "Hoy, 22 Feb",   "11:00", 30,  15.0, confirmed = false, stylistId = 2, isToday = true),
    StylistAppointment(9,  "Javier Gómez",   "Afeitado",        "Hoy, 22 Feb",   "13:30", 30,  18.0, confirmed = true,  stylistId = 2, isToday = true),
    StylistAppointment(10, "Marcos Díaz",    "Corte y Barba",   "Lun, 23 Feb",   "10:00", 40,  22.0, confirmed = false, stylistId = 2, isToday = false),
    StylistAppointment(11, "Ana Martínez",   "Corte de pelo",   "Mié, 25 Feb",   "11:30", 30,  15.0, confirmed = true,  stylistId = 2, isToday = false),

    // ── Laura Martín (id=3) ────────────────────────────────────
    StylistAppointment(12, "Ana Martínez",   "Mascarilla",      "Hoy, 22 Feb",   "10:30", 45,  25.0, confirmed = true,  stylistId = 3, isToday = true),
    StylistAppointment(13, "Lucía Herrera",  "Tratamiento",     "Hoy, 22 Feb",   "14:00", 45,  28.0, confirmed = false, stylistId = 3, isToday = true),
    StylistAppointment(14, "María García",   "Tratamiento cap.","Mar, 24 Feb",   "12:00", 60,  35.0, confirmed = false, stylistId = 3, isToday = false),
    StylistAppointment(15, "Sofía Fernández","Queratina",       "Jue, 26 Feb",   "15:00", 120, 60.0, confirmed = true,  stylistId = 3, isToday = false),

    // ── Diego Ruiz (id=4) ──────────────────────────────────────
    StylistAppointment(16, "Javier Gómez",   "Corte de pelo",   "Hoy, 22 Feb",   "09:00", 30,  15.0, confirmed = true,  stylistId = 4, isToday = true),
    StylistAppointment(17, "Marcos Díaz",    "Fade",            "Hoy, 22 Feb",   "11:30", 45,  20.0, confirmed = false, stylistId = 4, isToday = true),
    StylistAppointment(18, "Carlos López",   "Corte de pelo",   "Lun, 23 Feb",   "10:00", 30,  15.0, confirmed = true,  stylistId = 4, isToday = false),
    StylistAppointment(19, "Pedro Torres",   "Corte + Barba",   "Mié, 25 Feb",   "16:00", 40,  22.0, confirmed = false, stylistId = 4, isToday = false),

    // ── Admin Principal (id=0) ve todas ───────────────────────
    // (no tiene citas propias, ve el resumen general)
)
