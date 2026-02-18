package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.ProfileRepository
import com.example.hairup.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState

    private var currentUser: User? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _profileState.value = ProfileState.Error("No hay sesión activa")
            return
        }

        _profileState.value = ProfileState.Loading

        repository.getProfile(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { user ->
                    currentUser = user
                    _profileState.value = ProfileState.Success(user)
                }, onFailure = { exception ->
                    _profileState.value =
                        ProfileState.Error(exception.message ?: "Error al cargar perfil")
                })
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _updateState.value = UpdateState.Error("No hay sesión activa")
            return
        }

        _updateState.value = UpdateState.Loading

        repository.updateProfile(token, name, email, phone) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { updatedUser ->
                    sessionManager.saveAuthData(token, updatedUser)
                    currentUser = updatedUser
                    _profileState.value = ProfileState.Success(updatedUser)
                    _updateState.value = UpdateState.Success
                }, onFailure = { exception ->
                    _updateState.value =
                        UpdateState.Error(exception.message ?: "Error al actualizar")
                })
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _passwordState.value = PasswordState.Error("No hay sesión activa")
            return
        }

        _passwordState.value = PasswordState.Loading

        repository.changePassword(token, currentPassword, newPassword) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = {
                    _passwordState.value = PasswordState.Success
                }, onFailure = { exception ->
                    _passwordState.value =
                        PasswordState.Error(exception.message ?: "Error al cambiar contraseña")
                })
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    fun resetPasswordState() {
        _passwordState.value = PasswordState.Idle
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val user: User) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    sealed class PasswordState {
        object Idle : PasswordState()
        object Loading : PasswordState()
        object Success : PasswordState()
        data class Error(val message: String) : PasswordState()
    }
}