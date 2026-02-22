package com.example.hairup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.ProductResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.ShopRepository
import com.example.hairup.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val sessionManager: SessionManager,
    private val repository: ShopRepository = ShopRepository()
) : ViewModel() {

    private val _shopState = MutableStateFlow<ShopState>(ShopState.Loading)
    val shopState: StateFlow<ShopState> = _shopState

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val TAG = "ShopViewModel"

    data class CartItem(
        val product: Product,
        var quantity: Int
    )

    sealed class ShopState {
        object Loading : ShopState()
        data class Success(val products: List<Product>) : ShopState()
        data class Error(val message: String) : ShopState()
    }

    fun loadProducts() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _shopState.value = ShopState.Error("No hay sesión activa")
            return
        }

        _shopState.value = ShopState.Loading
        Log.d(TAG, "Cargando productos...")

        repository.getProducts(token) { result ->
            viewModelScope.launch {
                result.fold(
                    onSuccess = { productResponses ->
                        val products = productResponses.map { it.toProduct() }
                        Log.d(TAG, "Productos cargados: ${products.size}")
                        _shopState.value = ShopState.Success(products)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Error cargando productos", exception)
                        _shopState.value = ShopState.Error(exception.message ?: "Error desconocido")
                    }
                )
            }
        }
    }

    // Funciones del carrito
    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentCart.add(CartItem(product = product, quantity = 1))
        }

        _cartItems.value = currentCart
        Log.d(TAG, "Producto añadido al carrito. Total items: ${currentCart.size}")
    }

    fun removeFromCart(productId: Int) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == productId } ?: return

        if (existingItem.quantity > 1) {
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity - 1)
        } else {
            currentCart.remove(existingItem)
        }

        _cartItems.value = currentCart
    }

    fun deleteFromCart(productId: Int) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.removeAll { it.product.id == productId }
        _cartItems.value = currentCart
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    fun getCartItems(): List<CartItem> = _cartItems.value
}