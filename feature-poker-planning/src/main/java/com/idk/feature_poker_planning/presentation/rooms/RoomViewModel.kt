package com.idk.feature_poker_planning.presentation.rooms

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idk.feature_poker_planning.domain.GetUserProfileUseCase
import com.idk.feature_poker_planning.domain.JoinRoomUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.utils.RoomNavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadParticipantsUseCase: LoadParticipantsUseCase,
    private val submitVoteUseCase: SubmitVoteUseCase,
    private val joinRoomUseCase: JoinRoomUseCase
) : ViewModel() {

    private val roomId: String = checkNotNull(
        savedStateHandle[RoomNavArgs.ARG_ROOM_ID]
    ) { "roomId is required in SavedStateHandle" }

    private val _uiState = MutableStateFlow(
        RoomUiState(roomName = roomId)
    )
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { joinRoomUseCase(roomId) }
        observeParticipants()
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            loadParticipantsUseCase(roomId)
                .map { list -> list.distinctBy { it.userId } }
                .collect { uniqueList ->
                    _uiState.update { it.copy(participants = uniqueList) }
                }
        }
    }

    fun onVoteInputChange(input: String) {
        _uiState.update { it.copy(currentVoteInput = input) }
    }

    fun submitVote() {
        val voteValue = uiState.value.currentVoteInput.toIntOrNull() ?: return
        viewModelScope.launch {
            submitVoteUseCase(roomId, voteValue)
            _uiState.update { it.copy(currentVoteInput = "", votesRevealed = false) }
        }
    }

    fun revealVotes() {
        _uiState.update { it.copy(votesRevealed = true) }
    }

    fun startNewSession() {
        _uiState.update { it.copy(votesRevealed = false, currentVoteInput = "") }
    }
}
