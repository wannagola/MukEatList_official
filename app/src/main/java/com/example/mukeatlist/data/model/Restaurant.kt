package com.example.mukeatlist.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val categoryId: String,
    val mainMenu: String,
    val tags: List<String>,
    val address: String,
    val thumbnailUrl: String,
    val photoUrls: List<String>
)
