package com.idk.feature_poker_planning.presentation.welcome

data class WelcomeUiState(
    val selectedAvatar: String = "",
    val userName: String = "",
    val isFormValid: Boolean = false
)