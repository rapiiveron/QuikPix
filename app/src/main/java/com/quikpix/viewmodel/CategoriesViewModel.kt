package com.quikpix.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quikpix.data.model.Category
import com.quikpix.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoriesUiState {
    object Loading : CategoriesUiState()
    data class Success(val categories: List<Category>) : CategoriesUiState()
    data class Error(val message: String) : CategoriesUiState()
}

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CategoryRepository(application)

    private val _uiState = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoriesUiState.Loading
            try {
                val categories = repository.getCategories()
                _uiState.value = CategoriesUiState.Success(categories)
            } catch (e: Exception) {
                _uiState.value = CategoriesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
