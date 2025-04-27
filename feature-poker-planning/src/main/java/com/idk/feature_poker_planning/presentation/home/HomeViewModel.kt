package com.idk.feature_poker_planning.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idk.feature_poker_planning.domain.CreateRoomUseCase
import com.idk.feature_poker_planning.domain.GetUserProfileUseCase
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
    private val createRoomUseCase: CreateRoomUseCase,
    private val loadRoomsUseCase: LoadRoomsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        observeRooms()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val profile = getUserProfileUseCase()
            _uiState.update {
                it.copy(
                    userName = profile.userName, userAvatar = profile.avatar
                )
            }
        }
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