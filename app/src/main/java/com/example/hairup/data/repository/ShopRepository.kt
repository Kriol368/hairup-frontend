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

    fun purchaseProducts(
        token: String,
        items: List<PurchaseItem>,
        callback: (Result<PurchaseResponse>) -> Unit
    ) {
        Log.d(TAG, "Realizando compra de ${items.size} productos")

        val request = PurchaseRequest(items)

        RetrofitClient.apiService.purchaseProducts("Bearer $token", request).enqueue(object : Callback<PurchaseResponse> {
            override fun onResponse(
                call: Call<PurchaseResponse>,
                response: Response<PurchaseResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                        ?: callback(Result.failure(Exception("Respuesta vacía")))
                } else {
                    callback(Result.failure(Exception("Error ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<PurchaseResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}