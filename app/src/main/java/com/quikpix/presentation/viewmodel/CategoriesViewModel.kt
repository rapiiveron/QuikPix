package com.quikpix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quikpix.data.model.Category
import com.quikpix.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _sortMode = MutableStateFlow(SortMode.RECENT)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()
    
    init {
        loadCategories()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val categories = categoryRepository.getCategories()
                _categories.value = sortCategories(categories, _sortMode.value)
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
        val sorted = sortCategories(_categories.value, mode)
        _categories.value = sorted
    }
    
    private fun sortCategories(categories: List<Category>, mode: SortMode): List<Category> {
        return when (mode) {
            SortMode.RECENT -> categories.sortedByDescending { it.lastModified }
            SortMode.NAME -> categories.sortedBy { it.displayName }
            SortMode.COUNT -> categories.sortedByDescending { it.itemCount }
            SortMode.PINNED -> {
                val pinned = categories.filter { it.isPinned }.sortedByDescending { it.lastModified }
                val unpinned = categories.filterNot { it.isPinned }.sortedByDescending { it.lastModified }
                pinned + unpinned
            }
        }
    }
    
    fun togglePin(category: Category) {
        val updated = _categories.value.map {
            if (it.id == category.id) {
                it.copy(isPinned = !it.isPinned)
            } else {
                it
            }
        }
        _categories.value = sortCategories(updated, _sortMode.value)
    }
    
    fun toggleHide(category: Category) {
        val updated = _categories.value.map {
            if (it.id == category.id) {
                it.copy(isHidden = !it.isHidden)
            } else {
                it
            }
        }.filterNot { it.isHidden }
        _categories.value = sortCategories(updated, _sortMode.value)
    }
    
    fun clearError() {
        _error.value = null
    }
    
    enum class SortMode {
        RECENT, NAME, COUNT, PINNED
    }
}