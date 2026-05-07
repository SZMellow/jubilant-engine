package com.example.mapaani3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapaani3.data.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Product Detail screen.
 * Exposes UI state using StateFlow to follow UDF principles.
 */
class ProductDetailViewModel(
    private val productId: String,
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    // UI State for the Product Detail screen
    data class ProductUiState(
        val isFavorite: Boolean = false,
        val stockCount: Int = 0
    )

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        // Observe favorite status
        viewModelScope.launch {
            repository.isFavorite(productId).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }

        // Observe live stock updates
        viewModelScope.launch {
            repository.observeStockCount(productId).collect { stock ->
                _uiState.update { it.copy(stockCount = stock) }
            }
        }
    }

    /**
     * Toggles favorite status in the repository.
     */
    fun toggleFavorite() {
        repository.toggleFavorite(productId)
    }
}
