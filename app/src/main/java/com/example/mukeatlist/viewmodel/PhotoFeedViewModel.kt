package com.example.mukeatlist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mukeatlist.data.model.FeedPhotoItem
import com.example.mukeatlist.data.repository.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PhotoFeedViewModel(
    private val repository: RestaurantRepository,
    private val visitRepository: com.example.mukeatlist.data.repository.VisitRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<List<FeedPhotoItem>>(emptyList())
    val photos: StateFlow<List<FeedPhotoItem>> = _photos
    
    val visitedIds: StateFlow<Set<String>> = visitRepository.visitedIds

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                val restaurants = repository.getRestaurants()
                val photoItems = mutableListOf<FeedPhotoItem>()
                
                restaurants.forEach { restaurant ->
                    // 1. Thumbnail
                    if (restaurant.thumbnailUrl.isNotBlank()) {
                        photoItems.add(
                            FeedPhotoItem(
                                id = UUID.randomUUID().toString(),
                                imageUrl = restaurant.thumbnailUrl,
                                restaurant = restaurant
                            )
                        )
                    }
                    // 2. Extra Photos
                    restaurant.photoUrls.forEach { url ->
                        if (url.isNotBlank()) {
                            photoItems.add(
                                FeedPhotoItem(
                                    id = UUID.randomUUID().toString(),
                                    imageUrl = url,
                                    restaurant = restaurant
                                )
                            )
                        }
                    }
                }
                // Shuffle for random feed look
                photoItems.shuffle()
                photoItems
            }
            _photos.value = items
        }
    }
    
    fun toggleVisit(restaurantId: String) {
        visitRepository.toggleVisit(restaurantId)
    }
}

class PhotoFeedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoFeedViewModel::class.java)) {
            val visitRepo = com.example.mukeatlist.data.repository.VisitRepository.getInstance(context)
            @Suppress("UNCHECKED_CAST")
            return PhotoFeedViewModel(RestaurantRepository(context), visitRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
