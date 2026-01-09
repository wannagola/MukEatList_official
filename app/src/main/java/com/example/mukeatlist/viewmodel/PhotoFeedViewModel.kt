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

class PhotoFeedViewModel(private val repository: RestaurantRepository) : ViewModel() {

    private val _photos = MutableStateFlow<List<FeedPhotoItem>>(emptyList())
    val photos: StateFlow<List<FeedPhotoItem>> = _photos

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
}

class PhotoFeedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoFeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoFeedViewModel(RestaurantRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
