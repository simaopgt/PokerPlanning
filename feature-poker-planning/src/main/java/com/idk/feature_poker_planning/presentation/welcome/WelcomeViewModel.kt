package com.idk.feature_poker_planning.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idk.feature_poker_planning.domain.SetUserProfileUseCase
import com.idk.feature_poker_planning.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val setUserProfileUseCase: SetUserProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    val avatars = listOf(
        "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5"
    )

    fun selectAvatar(avatar: String) {
        _uiState.update {
            it.copy(
                selectedAvatar = avatar,
                isFormValid = avatar.isNotBlank() && it.userName.isNotBlank()
            )
        }
    }

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                userName = name,
                isFormValid = it.selectedAvatar.isNotBlank() && name.isNotBlank()
            )
        }
    }

    fun onStartClicked(onSuccess: () -> Unit) {
        if (!uiState.value.isFormValid) return
        viewModelScope.launch {
            val profile = UserProfile(
                userId = "",
                userName = uiState.value.userName,
                avatar = uiState.value.selectedAvatar
            )
            setUserProfileUseCase(profile)
            onSuccess()
        }
    }
}
