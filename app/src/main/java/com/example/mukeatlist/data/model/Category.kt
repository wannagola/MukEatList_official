package com.example.mukeatlist.data.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val restaurants: List<Restaurant>
)
