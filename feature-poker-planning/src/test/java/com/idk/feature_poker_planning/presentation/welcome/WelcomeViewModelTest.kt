package com.idk.feature_poker_planning.presentation.welcome

import app.cash.turbine.test
import com.idk.feature_poker_planning.domain.SetUserProfileUseCase
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WelcomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var setUserProfileUseCase: SetUserProfileUseCase
    private lateinit var viewModel: WelcomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        setUserProfileUseCase = mockk(relaxed = true)
        viewModel = WelcomeViewModel(setUserProfileUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TestDataProvider.INVALID_NAME, state.userName)
            assertEquals(TestDataProvider.INVALID_AVATAR, state.selectedAvatar)
            assertFalse(state.isFormValid)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(
            listOf(
                "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5"
            ), viewModel.avatars
        )
    }

    @Test
    fun selectAvatar_blankName_formInvalid() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.selectAvatar(TestDataProvider.VALID_AVATAR)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertEquals(TestDataProvider.VALID_AVATAR, state.selectedAvatar)
            assertFalse(state.isFormValid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNameChange_blankAvatar_formInvalid() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onNameChange(TestDataProvider.VALID_NAME)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertEquals(TestDataProvider.VALID_NAME, state.userName)
            assertFalse(state.isFormValid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectAvatarAndOnNameChange_validInputs_formValid() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.selectAvatar(TestDataProvider.VALID_AVATAR)
            testDispatcher.scheduler.advanceUntilIdle()
            val afterAvatar = awaitItem()
            assertFalse(afterAvatar.isFormValid)
            assertEquals(TestDataProvider.VALID_AVATAR, afterAvatar.selectedAvatar)

            viewModel.onNameChange(TestDataProvider.VALID_NAME)
            testDispatcher.scheduler.advanceUntilIdle()
            val finalState = awaitItem()
            assertTrue(finalState.isFormValid)
            assertEquals(TestDataProvider.VALID_AVATAR, finalState.selectedAvatar)
            assertEquals(TestDataProvider.VALID_NAME, finalState.userName)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onStartClicked_invalidForm_noUseCaseOrCallback() = runTest {
        var callbackCalled = false
        viewModel.onStartClicked { callbackCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(callbackCalled)
        coVerify(exactly = 0) { setUserProfileUseCase(any()) }
    }

    @Test
    fun onStartClicked_validForm_invokesUseCaseAndCallback() = runTest {
        viewModel.selectAvatar(TestDataProvider.VALID_AVATAR)
        viewModel.onNameChange(TestDataProvider.VALID_NAME)
        testDispatcher.scheduler.advanceUntilIdle()

        var callbackCalled = false
        viewModel.onStartClicked { callbackCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { setUserProfileUseCase(TestDataProvider.defaultUserProfile) }
        assertTrue(callbackCalled)
    }
}
