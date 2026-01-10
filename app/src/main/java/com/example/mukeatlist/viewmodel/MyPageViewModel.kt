package com.example.mukeatlist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mukeatlist.data.repository.RestaurantRepository
import com.example.mukeatlist.data.repository.VisitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val visitRepository: VisitRepository
) : ViewModel() {

    private val _totalVisitedCount = MutableStateFlow(0)
    val totalVisitedCount: StateFlow<Int> = _totalVisitedCount

    private val _badges = MutableStateFlow<List<BadgeUiState>>(emptyList())
    val badges: StateFlow<List<BadgeUiState>> = _badges

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            // visitedIds가 변경될 때마다 재계산
            visitRepository.visitedIds.collect { visitedSet ->
                _totalVisitedCount.value = visitedSet.size
                calculateBadges(visitedSet)
            }
        }
    }

    private fun calculateBadges(visitedSet: Set<String>) {
        val categories = restaurantRepository.getCategories()
        val badgeList = categories.map { category ->
            // val total = category.restaurants.size  <-- 기존: 전체 개수
            val targetCount = 10 // 목표: 10개
            
            val visited = category.restaurants.count { visitedSet.contains(it.id) }
            val isCompleted = visited >= targetCount

            // 카테고리별 뱃지 이름 매핑
            val (title, desc) = when (category.id) {
                "tzuyang" -> "쯔양 맛집 부수기" to "쯔양 맛집 10곳 정복!"
                "blackwhitechef" -> "흑백요리사 마스터" to "셰프들의 맛집 10곳 정복!"
                "michelin" -> "미슐랭 가이드 투어" to "별들의 전쟁 10곳 승리!"
                else -> "${category.name} 정복" to "맛집 10곳 도장 깨기 완료!"
            }

            BadgeUiState(
                categoryId = category.id,
                title = if (isCompleted) "$title 완료" else title,
                description = desc,
                visitedCount = visited,
                totalCount = targetCount, // 분모를 10으로 표시
                isCompleted = isCompleted
            )
        }
        _badges.value = badgeList
    }
}

class MyPageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            val visitRepo = VisitRepository.getInstance(context)
            val restRepo = RestaurantRepository(context)
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(restRepo, visitRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
