package com.group.mobileparkingchain.features.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group.mobileparkingchain.features.parking.data.model.Booking
import com.group.mobileparkingchain.features.parking.data.model.BookingHistoryRequest
import com.group.mobileparkingchain.features.parking.data.model.Pagination
import com.group.mobileparkingchain.features.parking.domain.repository.IParkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val parkingRepository: IParkingRepository
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _pagination = MutableStateFlow<Pagination?>(null)
    val pagination: StateFlow<Pagination?> = _pagination.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    init {
        fetchUserBookings()
    }

    /**
     * Fetch first page of bookings
     */
    fun fetchUserBookings(status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _selectedStatus.value = status
            currentPage = 1
            
            val request = BookingHistoryRequest(
                page = currentPage,
                limit = pageSize,
                status = status,
                sortField = "createdAt",
                sortOrder = "desc"
            )
            
            val result = parkingRepository.getUserBookings(request)
            
            result.onSuccess { bookingsData ->
                _bookings.value = bookingsData.bookings
                _pagination.value = bookingsData.pagination
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to fetch bookings"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Load next page for infinite scroll
     */
    fun loadMoreBookings() {
        val pagination = _pagination.value
        if (pagination == null || !pagination.hasMore || _isLoadingMore.value) {
            return
        }

        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage++
            
            val request = BookingHistoryRequest(
                page = currentPage,
                limit = pageSize,
                status = _selectedStatus.value,
                sortField = "createdAt",
                sortOrder = "desc"
            )
            
            val result = parkingRepository.getUserBookings(request)
            
            result.onSuccess { bookingsData ->
                // Append new bookings to existing list
                val currentBookings = _bookings.value
                _bookings.value = currentBookings + bookingsData.bookings
                _pagination.value = bookingsData.pagination
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load more bookings"
                currentPage-- // Revert page on failure
            }
            
            _isLoadingMore.value = false
        }
    }

    /**
     * Refresh bookings (pull-to-refresh)
     */
    fun refreshBookings() {
        fetchUserBookings(_selectedStatus.value)
    }

    /**
     * Filter by status
     */
    fun filterByStatus(status: String?) {
        fetchUserBookings(status)
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
}

class BookingViewModelFactory(
    private val parkingRepository: IParkingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(parkingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
