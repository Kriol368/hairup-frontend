package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.ProductResponse
import com.example.hairup.api.models.ProductsResponse
import com.example.hairup.model.Product
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
                    Log.d(TAG, "Productos obtenidos: ${products.size}")
                    callback(Result.success(products))
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Sesión expirada"
                        403 -> "No autorizado"
                        else -> "Error ${response.code()}"
                    }
                    Log.e(TAG, "Error obteniendo productos: $errorMsg")
                    callback(Result.failure(Exception(errorMsg)))
                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                Log.e(TAG, "Error de red en getProducts", t)
                callback(Result.failure(t))
            }
        })
    }
}