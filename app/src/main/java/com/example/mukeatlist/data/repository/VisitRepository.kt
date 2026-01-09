package com.example.mukeatlist.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VisitRepository private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("visit_prefs", Context.MODE_PRIVATE)
    private val key = "visited_ids"

    private val _visitedIds = MutableStateFlow<Set<String>>(loadVisitedIds())
    val visitedIds: StateFlow<Set<String>> = _visitedIds

    companion object {
        @Volatile
        private var INSTANCE: VisitRepository? = null

        fun getInstance(context: Context): VisitRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VisitRepository(context).also { INSTANCE = it }
            }
        }
    }

    private fun loadVisitedIds(): Set<String> {
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun toggleVisit(id: String) {
        val current = _visitedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _visitedIds.value = current
        prefs.edit().putStringSet(key, current).apply()
    }
    
    fun isVisited(id: String): Boolean {
        return _visitedIds.value.contains(id)
    }
}
