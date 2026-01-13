package com.example.mukeatlist.viewmodel

data class BadgeUiState(
    val id: Int,
    val categoryId: String,
    val title: String, // e.g. "쯔양 맛집 부수기"
    val description: String, // e.g. "쯔양 맛집 완전 정복 완료!"
    val visitedCount: Int,
    val totalCount: Int,
    val isCompleted: Boolean
)
