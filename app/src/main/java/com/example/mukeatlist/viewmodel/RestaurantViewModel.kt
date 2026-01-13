package com.example.mukeatlist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mukeatlist.data.model.Restaurant
import com.example.mukeatlist.data.repository.RestaurantRepository
import com.example.mukeatlist.data.repository.VisitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine // 이걸 꼭 확인하세요!
import kotlinx.coroutines.flow.stateIn


class RestaurantViewModel(
    private val repository: RestaurantRepository,
    private val visitRepository: VisitRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<com.example.mukeatlist.data.model.Category>>(emptyList())
    val categories: StateFlow<List<com.example.mukeatlist.data.model.Category>> = _categories
    // 🌟 추가: 카테고리와 상관없이 "전체 식당"을 들고 있는 Flow
    private val _allRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants
    
    val visitedIds: StateFlow<Set<String>> = visitRepository.visitedIds

    val visitedRestaurants: StateFlow<List<Restaurant>> = combine(
        _allRestaurants,
        visitedIds
    ) { allRes, ids ->
        allRes.filter { ids.contains(it.id) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadData()
    }


    private fun loadData() {
        viewModelScope.launch {
            val categoryList = withContext(Dispatchers.IO) {
                repository.getCategories()
            }
            _categories.value = categoryList
            _allRestaurants.value = categoryList.flatMap { it.restaurants }
            
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
    
    fun toggleVisit(restaurantId: String) {
        visitRepository.toggleVisit(restaurantId)
    }
}

class RestaurantViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            val visitRepo = VisitRepository.getInstance(context)
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(RestaurantRepository(context), visitRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
