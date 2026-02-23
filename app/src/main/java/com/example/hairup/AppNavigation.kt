package com.example.hairup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hairup.data.SessionManager
import com.example.hairup.ui.screens.LoginScreen
import com.example.hairup.ui.screens.RegisterScreen
import com.example.hairup.ui.screens.ClientHomeScreen
import com.example.hairup.ui.screens.client.BookingScreen
import com.example.hairup.ui.screens.admin.AdminHomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { isAdmin, stylistId ->
                    if (isAdmin) {
                        navController.navigate("admin_home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("client_home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("client_home") {
            ClientHomeScreen(
                onNavigateToBooking = { navController.navigate("booking") },
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("booking") {
            BookingScreen(
                onBookingComplete = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "admin_home/{stylistId}",
            arguments = listOf(navArgument("stylistId") { type = NavType.IntType })
        ) { backStackEntry ->
            val stylistId = backStackEntry.arguments?.getInt("stylistId") ?: 0
            AdminHomeScreen(
                stylistId = stylistId,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
