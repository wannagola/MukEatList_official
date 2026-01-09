package com.example.mukeatlist.data.repository

import android.content.Context
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.data.model.RestaurantData
import com.google.gson.Gson
import java.io.InputStreamReader

class RestaurantRepository(private val context: Context) {

    fun getCategories(): List<com.example.mukeatlist.data.model.Category> {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open("restaurant_data.json")
            val reader = InputStreamReader(inputStream)
            val data = Gson().fromJson(reader, RestaurantData::class.java)
            data.categories
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRestaurants(): List<Restaurant> {
        return getCategories().flatMap { it.restaurants }
    }
}
