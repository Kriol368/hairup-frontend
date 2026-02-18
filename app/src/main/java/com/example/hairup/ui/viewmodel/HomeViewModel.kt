package com.example.hairup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.HomeRepository
import com.example.hairup.model.Level
import com.example.hairup.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val repository: HomeRepository = HomeRepository()
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState
    private val TAG = "HomeViewModel"

    data class NextAppointment(
        val serviceName: String,
        val date: String,
        val time: String,
        val stylistName: String
    )

    data class HomeData(
        val user: User,
        val nextAppointment: NextAppointment?,
        val currentLevel: Level,
        val nextLevel: Level?
    )

    sealed class HomeState {
        object Loading : HomeState()
        data class Success(val data: HomeData) : HomeState()
        data class Error(val message: String) : HomeState()
    }

    fun loadHomeData() {
        val token = sessionManager.getToken()
        val user = sessionManager.getUser()

        if (token.isNullOrEmpty() || user == null) {
            _homeState.value = HomeState.Error("No hay sesión activa")
            return
        }

        _homeState.value = HomeState.Loading
        Log.d(TAG, "Cargando datos para usuario: ${user.name}")

        var levelsLoaded = false
        var appointmentLoaded = false
        var levelsResult: Result<List<Level>>? = null
        var appointmentResult: Result<AppointmentResponse?>? = null

        fun tryCombineResults() {
            if (levelsLoaded && appointmentLoaded) {
                Log.d(TAG, "Ambas llamadas completadas")

                // Verificar si hubo error en niveles
                if (levelsResult?.isFailure == true) {
                    val error = levelsResult!!.exceptionOrNull()
                    Log.e(TAG, "Error en niveles: ${error?.message}")
                    _homeState.value = HomeState.Error(error?.message ?: "Error al cargar niveles")
                    return
                }

                // Verificar si hubo error en cita (que no sea 404)
                if (appointmentResult?.isFailure == true) {
                    val error = appointmentResult!!.exceptionOrNull()
                    Log.e(TAG, "Error en cita: ${error?.message}")
                    _homeState.value = HomeState.Error(error?.message ?: "Error al cargar cita")
                    return
                }

                val levels = levelsResult?.getOrNull() ?: emptyList()
                val appointment = appointmentResult?.getOrNull()

                Log.d(TAG, "Niveles cargados: ${levels.size}")
                Log.d(TAG, "Cita recibida: ${if (appointment != null) "Sí" else "No"}")

                val currentLevel = levels.find { it.id == user.levelId }
                    ?: Level(id = 1, name = "Bronce", required = 0, reward = "")

                val nextLevel = levels.find { it.id == user.levelId + 1 }

                Log.d(TAG, "Nivel actual: ${currentLevel.name} (ID: ${currentLevel.id})")
                Log.d(TAG, "Siguiente nivel: ${nextLevel?.name ?: "Ninguno"}")

                // La parte de creación del NextAppointment sigue igual:
                val nextAppointment = if (appointment != null) {
                    Log.d(TAG, "Creando NextAppointment con: ${appointment.serviceName}")
                    NextAppointment(
                        serviceName = appointment.serviceName!!, // Ya sabemos que no es null por la validación
                        date = appointment.date!!,
                        time = appointment.time!!,
                        stylistName = appointment.stylistName!!
                    )
                } else {
                    Log.d(TAG, "No hay cita, nextAppointment = null")
                    null
                }

                val homeData = HomeData(
                    user = user,
                    nextAppointment = nextAppointment,
                    currentLevel = currentLevel,
                    nextLevel = nextLevel
                )

                Log.d(TAG, "HomeData creado correctamente")
                _homeState.value = HomeState.Success(homeData)
            }
        }

        repository.getLevels(token) { result ->
            viewModelScope.launch {
                levelsResult = result
                levelsLoaded = true
                Log.d(TAG, "Niveles cargados, levelsLoaded = true")
                tryCombineResults()
            }
        }

        repository.getNextAppointment(token) { result ->
            viewModelScope.launch {
                appointmentResult = result
                appointmentLoaded = true
                Log.d(TAG, "Cita cargada, appointmentLoaded = true")
                tryCombineResults()
            }
        }
    }
}