package com.quikpix.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quikpix.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoryDetailUiState {
    object Loading : CategoryDetailUiState()
    data class Success(val images: List<Uri>) : CategoryDetailUiState()
    data class Error(val message: String) : CategoryDetailUiState()
}

class CategoryDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CategoryRepository(application)

    private val _uiState = MutableStateFlow<CategoryDetailUiState>(CategoryDetailUiState.Loading)
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    fun loadImages(categoryName: String) {
        viewModelScope.launch {
            _uiState.value = CategoryDetailUiState.Loading
            try {
                val images = repository.getImagesInCategory(categoryName)
                _uiState.value = CategoryDetailUiState.Success(images)
            } catch (e: Exception) {
                _uiState.value = CategoryDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
