package com.example.mukeatlist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.mukeatlist.data.repository.RestaurantRepository
import com.example.mukeatlist.data.repository.VisitRepository

// [주의] BadgeUiState에 'val id: Int'가 추가되어 있어야 합니다.
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
            // visitedIds가 변경될 때마다 뱃지 상태 재계산
            visitRepository.visitedIds.collect { visitedSet ->
                _totalVisitedCount.value = visitedSet.size
                calculateBadges(visitedSet)
            }
        }
    }

    private fun calculateBadges(visitedSet: Set<String>) {
        val categories = restaurantRepository.getCategories()
        val newBadgeList = mutableListOf<BadgeUiState>()

        // 카테고리별로 4단계(마스터, 골드, 실버, 브론즈) 뱃지를 생성합니다.
        categories.forEach { category ->

            // 1. 현재 카테고리의 방문 횟수 계산
            val currentCount = category.restaurants.count { visitedSet.contains(it.id) }

            // 2. 카테고리별 ID 시작점 (Column Index)
            // 흑백요리사(1,4,7,10), 미슐랭(2,5,8,11), 쯔양(3,6,9,12)
            val colIndex = when (category.id) {
                "blackwhitechef" -> 1
                "michelin" -> 2
                "tzuyang" -> 3
                else -> 0 // 예외 처리 (보이지 않게 하거나 기본값)
            }

            if (colIndex != 0) {
                // 3. 4가지 티어 생성 (Threshold, Offset, Title)
                val tiers = listOf(
                    Triple(10, 0, "마스터"), // 목표 10회, ID 오프셋 0 (1~3)
                    Triple(5, 3, "골드"),   // 목표 5회,  ID 오프셋 3 (4~6)
                    Triple(2, 6, "실버"),   // 목표 2회,  ID 오프셋 6 (7~9)
                    Triple(1, 9, "브론즈")  // 목표 1회,  ID 오프셋 9 (10~12)
                )

                tiers.forEach { (targetCount, offset, tierName) ->
                    // ID 계산: 오프셋 + 컬럼인덱스
                    // 예: 흑백요리사(1) 마스터(0) -> ID 1
                    // 예: 쯔양(3) 브론즈(9) -> ID 12
                    val badgeId = offset + colIndex
                    val isCompleted = currentCount >= targetCount

                    val descText = if (isCompleted) {
                        "${category.name} $tierName 등급 달성!"
                    } else {
                        "${targetCount}곳 방문 시 획득 가능"
                    }
                    newBadgeList.add(
                        BadgeUiState(
                            id = badgeId, // [중요] 계산된 ID 주입
                            categoryId = category.id,
                            title = "${category.name} $tierName",
                            description = descText,
                            visitedCount = currentCount,
                            totalCount = targetCount,
                            isCompleted = isCompleted
                        )
                    )
                }
            }
        }

        // 최종 리스트 업데이트
        _badges.value = newBadgeList
    }
}

class MyPageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            val visitRepo = VisitRepository.getInstance(context)
            // RestaurantRepository 생성자가 Context를 필요로 한다면 전달
            val restRepo = RestaurantRepository(context)
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(restRepo, visitRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}