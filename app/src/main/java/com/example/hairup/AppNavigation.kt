package com.example.hairup

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hairup.ui.screens.LoginScreen
import com.example.hairup.ui.screens.RegisterScreen
import com.example.hairup.ui.screens.ClientHomeScreen
import com.example.hairup.ui.screens.client.BookingScreen
import com.example.hairup.ui.screens.admin.AdminHomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { isAdmin, stylistId ->
                    if (isAdmin) {
                        navController.navigate("admin_home/$stylistId")
                    } else {
                        navController.navigate("client_home")
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
