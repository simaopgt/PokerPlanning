package com.idk.feature_poker_planning.presentation.rooms

import com.idk.feature_poker_planning.domain.model.Participant

data class RoomUiState(
    val roomName: String,
    val participants: List<Participant> = emptyList(),
    val votesRevealed: Boolean = false,
    val currentVoteInput: String = "",
    val currentUserId: String = "",
)
