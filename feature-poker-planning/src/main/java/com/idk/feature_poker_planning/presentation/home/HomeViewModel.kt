package com.idk.feature_poker_planning.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idk.feature_poker_planning.domain.CreateRoomUseCase
import com.idk.feature_poker_planning.domain.LoadRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase, private val loadRoomsUseCase: LoadRoomsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRooms()
    }

    fun createRoom(desiredName: String? = null) {
        viewModelScope.launch {
            createRoomUseCase(desiredName)
            observeRooms()
        }
    }

    private fun observeRooms() {
        viewModelScope.launch {
            loadRoomsUseCase().collect { rooms ->
                _uiState.update { it.copy(rooms = rooms) }
            }
        }
    }
}
