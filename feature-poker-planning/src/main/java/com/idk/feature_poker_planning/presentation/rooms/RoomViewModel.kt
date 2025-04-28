package com.idk.feature_poker_planning.presentation.rooms

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idk.feature_poker_planning.domain.JoinRoomUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.ResetVotesUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations
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
    private val joinRoomUseCase: JoinRoomUseCase,
    private val resetVotesUseCase: ResetVotesUseCase
) : ViewModel() {

    private val roomId: String = checkNotNull(
        savedStateHandle[PokerPlanningDestinations.ARG_ROOM_ID]
    ) { "roomId é obrigatório" }

    private val rawRoomName: String = checkNotNull(
        savedStateHandle[PokerPlanningDestinations.ARG_ROOM_NAME]
    ) { "roomName é obrigatório" }

    private val roomNameArg: String = Uri.decode(rawRoomName)

    private val _uiState = MutableStateFlow(
        RoomUiState(roomName = roomNameArg, currentUserId = "")
    )
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { joinRoomUseCase(roomId) }
        observeParticipants()
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            loadParticipantsUseCase(roomId).map { list -> list.distinctBy { it.userId } }
                .collect { participants ->
                    _uiState.update { it.copy(participants = participants) }
                }
        }
    }

    fun onVoteInputChange(input: String) {
        _uiState.update { it.copy(currentVoteInput = input) }
    }

    fun submitVote() {
        val vote = _uiState.value.currentVoteInput.toIntOrNull() ?: return
        viewModelScope.launch {
            submitVoteUseCase(roomId, vote)
            _uiState.update { it.copy(currentVoteInput = "", votesRevealed = false) }
        }
    }

    fun revealVotes() {
        _uiState.update { it.copy(votesRevealed = true) }
    }

    fun startNewSession() {
        viewModelScope.launch {
            resetVotesUseCase(roomId)

            _uiState.update {
                it.copy(
                    votesRevealed = false, currentVoteInput = ""
                )
            }
        }
    }
}
