package com.idk.feature_poker_planning.presentation.splash

import app.cash.turbine.test
import com.idk.feature_poker_planning.domain.HasUserProfileUseCase
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SplashViewModelTest {

    @Test
    fun init_hasUserProfileTrue_uiStateHasProfileTrue() = runTest {
        val hasUserProfile = mockk<HasUserProfileUseCase>()
        coEvery { hasUserProfile() } returns TestDataProvider.HAS_PROFILE

        val viewModel = SplashViewModel(hasUserProfile)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TestDataProvider.HAS_PROFILE, state.hasProfile)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_hasUserProfileFalse_uiStateHasProfileFalse() = runTest {
        val hasUserProfile = mockk<HasUserProfileUseCase>()
        coEvery { hasUserProfile() } returns TestDataProvider.NO_PROFILE

        val viewModel = SplashViewModel(hasUserProfile)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TestDataProvider.NO_PROFILE, state.hasProfile)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
