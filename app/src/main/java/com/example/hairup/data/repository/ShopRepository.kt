package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopRepository {

    private val TAG = "ShopRepository"

    fun getProducts(token: String, callback: (Result<List<ProductResponse>>) -> Unit) {
        Log.d(TAG, "Obteniendo productos del backend")

        RetrofitClient.apiService.getProducts("Bearer $token").enqueue(object : Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>
            ) {
                if (response.isSuccessful) {
                    val products = response.body()?.data ?: emptyList()
                    callback(Result.success(products))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getCategories(token: String, callback: (Result<List<CategoryResponse>>) -> Unit) {
        Log.d(TAG, "Obteniendo categorías")

        RetrofitClient.apiService.getCategories("Bearer $token").enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body()?.data ?: emptyList()
                    callback(Result.success(categories))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun createCategory(
        token: String,
        request: CreateCategoryRequest,
        callback: (Result<CategorySuccessResponse>) -> Unit
    ) {
        Log.d(TAG, "Creando categoría: ${request.name}")

        RetrofitClient.apiService.createCategory("Bearer $token", request).enqueue(object : Callback<CategorySuccessResponse> {
            override fun onResponse(
                call: Call<CategorySuccessResponse>,
                response: Response<CategorySuccessResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                        ?: callback(Result.failure(Exception("Respuesta vacía")))
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: "Error ${response.code()}"
                    } catch (e: Exception) {
                        "Error ${response.code()}"
                    }
                    callback(Result.failure(Exception(errorMsg)))
                }
            }

            override fun onFailure(call: Call<CategorySuccessResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun purchaseProducts(
        token: String,
        items: List<PurchaseItem>,
        callback: (Result<PurchaseResponse>) -> Unit
    ) {
        Log.d(TAG, "Realizando compra de ${items.size} productos")

        // Calcular puntos totales de los productos
        var totalPoints = 0

        // Primero obtenemos los productos para saber sus puntos
        getProducts(token) { productsResult ->
            productsResult.fold(
                onSuccess = { products ->
                    // Calcular puntos sumando (points * quantity) de cada item
                    totalPoints = items.sumOf { item ->
                        val product = products.find { it.id == item.productId }
                        (product?.points ?: 0) * item.quantity
                    }

                    // Llamar al nuevo endpoint con los puntos totales
                    val request = AddPointsRequest(totalPoints)
                    RetrofitClient.apiService.addPoints("Bearer $token", request).enqueue(object : Callback<AddPointsResponse> {
                        override fun onResponse(
                            call: Call<AddPointsResponse>,
                            response: Response<AddPointsResponse>
                        ) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    // Convertir AddPointsResponse a PurchaseResponse
                                    val purchaseResponse = PurchaseResponse(
                                        success = body.success,
                                        message = body.message,
                                        xpEarned = body.xpEarned,
                                        pointsEarned = body.pointsEarned,
                                        newXp = body.newXp,
                                        newPoints = body.newPoints
                                    )
                                    callback(Result.success(purchaseResponse))
                                } else {
                                    callback(Result.failure(Exception("Respuesta vacía")))
                                }
                            } else {
                                callback(Result.failure(Exception("Error ${response.code()}")))
                            }
                        }

                        override fun onFailure(call: Call<AddPointsResponse>, t: Throwable) {
                            callback(Result.failure(t))
                        }
                    })
                },
                onFailure = { exception ->
                    callback(Result.failure(exception))
                }
            )
        }
    }
}