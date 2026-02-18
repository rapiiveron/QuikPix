package com.quikpix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quikpix.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val categoryRepository: CategoryRepository,
    private val categoryId: Long
) : ViewModel() {
    
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadImages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val images = categoryRepository.getImagesInCategory(categoryId)
                _images.value = images
            } catch (e: Exception) {
                _error.value = "Failed to load images: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    companion object {
        fun provideFactory(
            categoryRepository: CategoryRepository,
            categoryId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryDetailViewModel(categoryRepository, categoryId) as T
            }
        }
    }
}