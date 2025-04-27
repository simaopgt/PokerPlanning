package com.idk.feature_poker_planning.presentation.rooms

import androidx.lifecycle.SavedStateHandle
import com.idk.feature_poker_planning.domain.GetUserProfileUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.utils.RoomNavArgs
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomViewModelTest {
    private val testRoomId = TestDataProvider.defaultRoom.id
    private val participants = listOf(
        Participant(userId = "u1", name = "Alice", vote = null),
        Participant(userId = "u2", name = "Bob", vote = null)
    )

    private lateinit var savedStateHandle: SavedStateHandle
    private val loadParticipantsUseCase: LoadParticipantsUseCase = mockk()
    private val submitVoteUseCase: SubmitVoteUseCase = mockk(relaxed = true)
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        savedStateHandle = SavedStateHandle(mapOf(RoomNavArgs.ARG_ROOM_ID to testRoomId))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsParticipants_updatesUiState() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(participants)

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        val ui = viewModel.uiState.value
        assertEquals(testRoomId, ui.roomName)
        assertEquals(participants, ui.participants)
    }

    @Test
    fun onVoteInputChange_updatesCurrentVoteInput() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("5")

        assertEquals("5", viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun revealVotes_initialState_updatesVotesRevealed() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.votesRevealed)

        viewModel.revealVotes()

        assertTrue(viewModel.uiState.value.votesRevealed)
    }

    @Test
    fun startNewSession_afterReveal_resetsState() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()
        viewModel.onVoteInputChange("3")
        viewModel.revealVotes()
        assertTrue(viewModel.uiState.value.votesRevealed)
        assertEquals("3", viewModel.uiState.value.currentVoteInput)

        viewModel.startNewSession()

        assertFalse(viewModel.uiState.value.votesRevealed)
        assertEquals(TestDataProvider.INVALID_NAME, viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun submitVote_validInput_callsUseCase() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        coEvery {
            submitVoteUseCase(
                testRoomId,
                TestDataProvider.defaultUserProfile.userId,
                7
            )
        } returns Unit
        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("7")
        viewModel.submitVote()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            submitVoteUseCase(
                testRoomId,
                TestDataProvider.defaultUserProfile.userId,
                7
            )
        }
    }

    @Test
    fun submitVote_invalidInput_doesNotCallUseCase() = runTest {
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, getUserProfileUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("abc")
        viewModel.submitVote()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { submitVoteUseCase(any(), any(), any()) }
    }
}
