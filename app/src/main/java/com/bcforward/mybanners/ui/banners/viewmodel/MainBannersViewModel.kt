package com.bcforward.mybanners.ui.banners.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcforward.mybanners.data.remote.BannersResponse
import com.bcforward.mybanners.ui.banners.data.BannersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainBannersViewModel @Inject constructor(
    private val bannersRepository: BannersRepository
) : ViewModel() {
    sealed class UiState {
        object Loading : UiState()
        data class Success(val banners: List<BannersResponse>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val maxRetryAttempts = 3
    private val retryDelay = 10000L

    init {
        fetchBannersWithRetry()
    }

    private fun fetchBannersWithRetry() {
        viewModelScope.launch {
            var currentAttempt = 0
            var success = false
            _uiState.value = UiState.Loading

            while (currentAttempt < maxRetryAttempts && !success) {
                try {
                    val result = bannersRepository.getBanners()
                    result.onSuccess {
                        _uiState.value = UiState.Success(it)
                        success = true
                    }.onFailure {
                        throw it
                    }
                } catch (e: IOException) {
                    currentAttempt++
                    if (currentAttempt >= maxRetryAttempts) {
                        _uiState.value = UiState.Error("Failed to load banners. Please try again.")
                    } else {
                        delay(retryDelay)
                    }
                }
            }
        }
    }

    fun retryFetchBanners() {
        fetchBannersWithRetry()
    }
}
