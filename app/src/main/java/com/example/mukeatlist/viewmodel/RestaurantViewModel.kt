package com.example.mukeatlist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.data.repository.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantViewModel(private val repository: RestaurantRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<com.example.mukeatlist.data.model.Category>>(emptyList())
    val categories: StateFlow<List<com.example.mukeatlist.data.model.Category>> = _categories

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val categoryList = withContext(Dispatchers.IO) {
                repository.getCategories()
            }
            _categories.value = categoryList
            
            // Default to the first category if available, or stay null (All)
             if (categoryList.isNotEmpty()) {
                 onCategorySelected(categoryList.first().id)
             } else {
                 _restaurants.value = emptyList()
             }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _selectedCategoryId.value = categoryId
        val categoryList = _categories.value
        if (categoryId == "ALL") {
            _restaurants.value = categoryList.flatMap { it.restaurants }
        } else {
            val selected = categoryList.find { it.id == categoryId }
            _restaurants.value = selected?.restaurants ?: emptyList()
        }
    }
}

class RestaurantViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(RestaurantRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
